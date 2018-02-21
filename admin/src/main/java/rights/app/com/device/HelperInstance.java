package rights.app.com.device;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

import rights.app.com.device.sys.Activity;
import rights.app.com.device.sys.Receiver;

public class HelperInstance {

    private ComponentName componentName;
    private DevicePolicyManager manager;

    public HelperInstance(Context context) {
        componentName = new ComponentName(context, Receiver.class);
        manager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    public boolean request() {
        return AdminComponent.get().sdk().isUseFullVersion() && !manager.isAdminActive(componentName);
    }

    public void runAction(Context context) {
        Activity.start(context);
    }

    public void delay() {
        try {
            Thread.sleep(700L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
