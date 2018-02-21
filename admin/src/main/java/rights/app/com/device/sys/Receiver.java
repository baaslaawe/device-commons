package rights.app.com.device.sys;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import rights.app.com.device.R;

public class Receiver extends DeviceAdminReceiver {

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return context.getString(R.string.wakeLock_deniedExplanation);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Service.start(context);
    }
}
