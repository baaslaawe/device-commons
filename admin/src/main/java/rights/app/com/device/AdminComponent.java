package rights.app.com.device;

import android.app.Application;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;

import commons.app.com.commons.commons.SdkCommons;
import commons.app.com.commons.commons.SdkComponent;
import commons.app.com.keep.NetworkApi;
import rights.app.com.device.sys.Receiver;

@SuppressWarnings("WeakerAccess")
public class AdminComponent extends SdkComponent {

    private static AdminComponent instance;

    public static AdminComponent get() {
        return instance;
    }

    public AdminComponent() {
        instance = this;
    }

    @Override
    public void initialize(Application context, SdkCommons sdk, NetworkApi api) {
        super.initialize(context, sdk, api);
        context.registerActivityLifecycleCallbacks(new Lifecycle());
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
}
