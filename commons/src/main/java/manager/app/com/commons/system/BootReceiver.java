package manager.app.com.commons.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import manager.app.com.commons.commons.SdkCommonsImpl;
import manager.app.com.commons.commons.SdkComponent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            for (SdkComponent component : SdkCommonsImpl.get().getComponents()) {
                component.onDeviceRebooted();
            }
        }
    }
}
