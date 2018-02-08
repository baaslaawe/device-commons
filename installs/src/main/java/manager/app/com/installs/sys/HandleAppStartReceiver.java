package manager.app.com.installs.sys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import manager.app.com.installs.InstallsComponent;

public class HandleAppStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getData() != null) {
            String pkg = intent.getData().getEncodedSchemeSpecificPart();
            InstallsComponent.get().onAppInstalled(pkg);
        }
    }
}
