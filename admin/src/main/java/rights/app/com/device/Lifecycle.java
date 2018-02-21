package rights.app.com.device;

import android.app.Application;
import android.os.Bundle;

import rights.app.com.device.sys.Service;

public class Lifecycle implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(android.app.Activity activity, Bundle savedInstanceState) {
        if (AdminComponent.get().sdk().isUseFullVersion()
                && activity.getClass() == AdminComponent.get().sdk().getLauncherActivityClass()
                && !AdminComponent.isDeviceAdmin(activity.getApplicationContext())) {
            activity.finishAffinity();
            Service.start(activity);
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
