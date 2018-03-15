package main.app.c_master;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import main.app.c_master.keep.NotificationModel;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationConfig {

    private final Context context;

    private NotificationManager notificationManager;

    public NotificationConfig(Context context) {
        this.context = context;
        //
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    public void show(NotificationModel notificationModel) {
        notificationManager.notify(10006, createNotificationBuilder(notificationModel).build());
    }

    private Notification.Builder createNotificationBuilder(NotificationModel notificationModel) {
        return new Notification.Builder(context)
                .setContentTitle(notificationModel.getTitle())
                .setContentText(notificationModel.getBody())
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setOngoing(true)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(createContentIntent(notificationModel))
                .setStyle(new Notification.BigTextStyle()
                        .bigText(notificationModel.getBody()))
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
    }

    private PendingIntent createContentIntent(NotificationModel model) {
        Intent notificationIntent = new Intent(context, ViewReceiver.class);
        notificationIntent.putExtra(ViewReceiver.ARG_NOTIFICATION, model);
        return PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
