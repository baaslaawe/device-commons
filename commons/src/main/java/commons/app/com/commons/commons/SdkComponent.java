package commons.app.com.commons.commons;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;

import commons.app.com.keep.NetworkApi;

public abstract class SdkComponent {

    private SdkCommons sdk;
    private Context context;
    private NetworkApi api;

    @CallSuper
    public void initialize(Application context, SdkCommons sdk, NetworkApi api) {
        this.context = context;
        this.sdk = sdk;
        this.api = api;
    }

    public Context context() {
        return context;
    }

    public NetworkApi api() {
        return api;
    }

    public SdkCommons sdk() {
        return sdk;
    }

    abstract public void onDeviceRegistered();

    abstract public void onFcmMessageReceived(Bundle payload);

    abstract public void onDeviceRebooted();

    @Nullable
    abstract public Job createJob(@NonNull String tag);
}
