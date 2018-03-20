package main_commons.app.c_master;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import main_commons.app.c_master.SdkComponent;
import main_commons.app.c_master.SdkCommonsImpl;

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
