package manager.app.com.commons.commons;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;

public interface SdkComponent {

    void onMainSdkInitialized(Application application, SdkCommons sdk);

    void onDeviceRegistered();

    void onFcmMessageReceived(Bundle payload);

    void onDeviceRebooted();

    @Nullable
    Job createJob(@NonNull String tag);
}
