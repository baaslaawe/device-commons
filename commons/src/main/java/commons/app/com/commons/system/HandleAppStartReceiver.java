package commons.app.com.commons.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import commons.app.com.commons.commons.SdkCommonsImpl;
import commons.app.com.commons.commons.SdkComponent;

public class HandleAppStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Uri data = intent.getData();
        if (!Intent.ACTION_PACKAGE_ADDED.equals(action) || data == null) {
            return;
        }
        String packageName = data.getEncodedSchemeSpecificPart();
        // new app was installed or version updated
        for (SdkComponent component : SdkCommonsImpl.get().getComponents()) {
            component.onNewAppAdded(packageName);
        }
    }
}
