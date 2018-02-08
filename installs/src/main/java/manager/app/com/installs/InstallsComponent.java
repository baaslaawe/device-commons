package manager.app.com.installs;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import java.util.concurrent.TimeUnit;

import manager.app.com.commons.commons.SdkCommons;
import manager.app.com.commons.commons.SdkComponent;

public class InstallsComponent implements SdkComponent {

    private static InstallsComponent instance;

    public static final String ARG_APP_ID = "i18927878_arg_1";
    private static final String JOB_TAG_INSTALL_EVENT = "i92797272729_j_1";

    public static InstallsComponent get() {
        return instance;
    }

    public InstallsComponent() {
        instance = this;
    }

    @Override
    public void onMainSdkInitialized(Application application, SdkCommons sdk) {

    }

    @Override
    public void onDeviceRegistered() {

    }

    @Override
    public void onFcmMessageReceived(Bundle payload) {

    }

    @Override
    public void onDeviceRebooted() {

    }

    @Nullable
    @Override
    public Job createJob(@NonNull String tag) {
        if (JOB_TAG_INSTALL_EVENT.equals(tag)) {
            return new UploadEventJob();
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // INNER
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
