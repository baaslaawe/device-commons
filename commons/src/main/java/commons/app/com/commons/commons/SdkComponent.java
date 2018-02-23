package commons.app.com.commons.commons;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.evernote.android.job.Job;
import com.google.gson.JsonParseException;

import commons.app.com.keep.NetworkApi;

public abstract class SdkComponent {

    private SdkCommons sdk;
    private NetworkApi api;

    @CallSuper
    public void initialize(Application context, SdkCommons sdk, NetworkApi api) {
        this.sdk = sdk;
        this.api = api;
    }

    public Context context() {
        return sdk().ctx();
    }

    public NetworkApi api() {
        return api;
    }

    public SdkCommons sdk() {
        return sdk;
    }

    @Nullable
    abstract public Job createJob(@NonNull String tag);

    public void onDeviceRegistered() {
        // handle
    }

    public void onDeviceRebooted() {
        // handle
    }

    public void onNewAppAdded(String packageName) {
        // handle
    }

    public void onFcmMessageReceived(@NonNull String type, @NonNull Bundle payload) {
        // handle
    }

    public void onSyncEvent(@NonNull String json) {
        // handle
    }

    @Nullable
    protected final <T> T safeParse(String json, Class<T> classOfT) {
        if (TextUtils.isEmpty(json)) return null;
        try {
            return sdk().gson().fromJson(json, classOfT);
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
