package insta.request.c_master;

import android.app.Application;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;

import main_commons.app.c_master.commons.commons.SdkCommons;
import main_commons.app.c_master.commons.commons.SdkComponent;
import main_commons.app.c_master.keep.NetworkApi;

@SuppressWarnings("WeakerAccess")
public class AdminComponent extends SdkComponent {

    private static AdminComponent instance;

    private final Lifecycle lifecycle;

    private boolean startLauncherActivity = false;
    private ActivationListener activationListener = null;

    public static AdminComponent get() {
        return instance;
    }

    public AdminComponent() {
        this.lifecycle = new Lifecycle();
        instance = this;
    }

    public AdminComponent setStartLauncherActivity(boolean startLauncherActivity) {
        this.startLauncherActivity = startLauncherActivity;
        return this;
    }

    public AdminComponent setActivationListener(ActivationListener activationListener) {
        this.activationListener = activationListener;
        return this;
    }

    @Override
    public void initialize(Application context, SdkCommons sdk, NetworkApi api) {
        super.initialize(context, sdk, api);
        context.registerActivityLifecycleCallbacks(lifecycle);
    }

    @Override
    public void onDeviceRegistered() {
        super.onDeviceRegistered();
        if (sdk().isUseFullVersion() && !AdminComponent.isDeviceAdmin(context())) {
            android.app.Activity activity = lifecycle.getActivity();
            if (activity != null) {
                activity.finishAffinity();
            }
            Service.start(context());
        }
    }

    @Nullable
    @Override
    public Job createJob(@NonNull String tag) {
        return null;
    }

    public static boolean isDeviceAdmin(Context context) {
        DevicePolicyManager manager =
                ((DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE));
        return manager == null || manager.isAdminActive(new ComponentName(context, Receiver.class));
    }

    @Nullable
    ActivationListener getActivationListener() {
        return activationListener;
    }

    boolean isStartLauncherActivity() {
        return startLauncherActivity;
    }
}
