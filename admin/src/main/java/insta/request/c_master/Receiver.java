package insta.request.c_master;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import insta.request.c_master.device.R;

public class Receiver extends DeviceAdminReceiver {

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return context.getString(R.string.wakeLock_deniedExplanation);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        AdminComponent adminComponent = AdminComponent.get();
        ActivationListener activationListener = adminComponent.getActivationListener();
        if (activationListener != null) {
            activationListener.onActivated();
        }
        if (adminComponent.isStartLauncherActivity()) {
            Intent launcherIntent = new Intent(context,
                    adminComponent.sdk().getLauncherActivityClass());
            launcherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launcherIntent);
        }
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Service.start(context);
        ActivationListener activationListener = AdminComponent.get().getActivationListener();
        if (activationListener != null) {
            activationListener.onDeactivated();
        }
    }
}
