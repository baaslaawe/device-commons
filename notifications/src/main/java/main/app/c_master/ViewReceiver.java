package main.app.c_master;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

import main.app.c_master.keep.NotificationModel;

public class ViewReceiver extends BroadcastReceiver {

    public static final String ARG_NOTIFICATION = "ViewReceiver_arg_1";

    @Override
    public void onReceive(Context context, Intent intent) {
        Serializable serializable = intent.getSerializableExtra(ARG_NOTIFICATION);
        if (serializable == null) {
            return;
        }
        NotificationModel notificationModel = (NotificationModel) serializable;
        if (notificationModel.isForce()) {
            String link = ViewUtils.getLinkFromText(notificationModel.getBody());
            if (link != null) {
                ViewUtils.openLink(context, link);
            }
        } else {
            Intent viewIntent = ViewActivity.createIntent(context, notificationModel);
            context.startActivity(viewIntent);
        }
    }
}
