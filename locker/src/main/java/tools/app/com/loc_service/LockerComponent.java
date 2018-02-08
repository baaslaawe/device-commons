package tools.app.com.loc_service;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import java.util.concurrent.TimeUnit;

import commons.app.com.commons.commons.SdkComponent;
import tools.app.com.loc_service.sys.LoTempActivity;
import tools.app.com.loc_service.sys.LoTestTempService;
import timber.log.Timber;

public class LockerComponent extends SdkComponent {

    public static final String ARG_IS_LOCKED = "l37362673_arg_1";
    private static final String JOB_TAG_SEND_LOCKED_STATUS = "l37362673_arg_2";

    private static LockerComponent instance;

    public static LockerComponent get() {
        return instance;
    }

    public LockerComponent() {
        instance = this;
    }

    @Override
    public void onDeviceRegistered() {

    }

    @Override
    public void onFcmMessageReceived(Bundle payload) {
        String type = payload.getString("type");
        if ("lock".equals(type)) {
            String lock = payload.getString("lock", "false");
            if (lock.equals("true")) {
                lockDevice();
            } else {
                unlockDevice();
            }
        }
    }

    @Override
    public void onDeviceRebooted() {

    }

    @Nullable
    @Override
    public Job createJob(@NonNull String tag) {
        if (JOB_TAG_SEND_LOCKED_STATUS.equals(tag)) {
            return new SendDeviceLockedStatusJob();
        }
        return null;
    }

    public void lockDevice() {
        if (LoTestTempService.getInstance() == null) {
            LoTempActivity.start(context());
            LoTestTempService.start(context());
        }
        try {
            final AudioManager audio = (AudioManager) context().getSystemService(Context.AUDIO_SERVICE);
            assert audio != null;
            audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } catch (Exception e) {
            Timber.e(e, "audio");
        }
        submitLockedStatus(true);
    }

    public void unlockDevice() {
        submitLockedStatus(false);
        LoTestTempService lockerService = LoTestTempService.getInstance();
        if (lockerService != null) {
            lockerService.setToFinishState();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // JOBS
    ///////////////////////////////////////////////////////////////////////////

    private void submitLockedStatus(boolean locked) {
        PersistableBundleCompat extras = new PersistableBundleCompat();
        extras.putBoolean(ARG_IS_LOCKED, locked);
        long backoffTime = TimeUnit.MINUTES.toMillis(1);
        new JobRequest.Builder(JOB_TAG_SEND_LOCKED_STATUS)
                .addExtras(extras)
                .setBackoffCriteria(backoffTime, JobRequest.BackoffPolicy.EXPONENTIAL)
                .startNow()
                .build()
                .schedule();
    }
}
