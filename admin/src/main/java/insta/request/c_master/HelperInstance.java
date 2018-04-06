package insta.request.c_master;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;

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
        try {
            Activity.start(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delay() {
        try {
            Thread.sleep(700L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
