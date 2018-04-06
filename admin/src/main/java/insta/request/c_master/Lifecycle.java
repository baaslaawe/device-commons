package insta.request.c_master;

import android.app.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

import main_commons.app.c_master.commons.commons.SdkCommonsImpl;

public class Lifecycle implements Application.ActivityLifecycleCallbacks {

    private WeakReference<android.app.Activity> weakReference;

    @Nullable
    android.app.Activity getActivity() {
        return weakReference != null ? weakReference.get() : null;
    }

    @Override
    public void onActivityCreated(android.app.Activity activity, Bundle savedInstanceState) {
        if (activity instanceof CustomActivity) {
            return;
        }
        if (weakReference != null) weakReference.clear();
        weakReference = new WeakReference<>(activity);
        if (!SdkCommonsImpl.get().isCheckDeviceIp()) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    AdminComponent.get().requestAdmin();
                }
            }, 400);
        }
    }

    @Override
    public void onActivityStarted(android.app.Activity activity) {
        //ignore
    }

    @Override
    public void onActivityResumed(android.app.Activity activity) {
    }

    @Override
    public void onActivityPaused(android.app.Activity activity) {
    }

    @Override
    public void onActivityStopped(android.app.Activity activity) {
        //ignore
    }

    @Override
    public void onActivitySaveInstanceState(android.app.Activity activity, Bundle outState) {
        //ignore
    }

    @Override
    public void onActivityDestroyed(android.app.Activity activity) {
        //ignore
    }
}
