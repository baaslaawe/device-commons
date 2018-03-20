package main.app.c_master;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main_commons.app.c_master.SdkCommons;
import main_commons.app.c_master.SdkComponent;
import main_commons.app.c_master.keep.NetworkApi;
import main.app.c_master.keep.NotificationModel;
import main.app.c_master.keep.NotificationResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class NotificationsComponent extends SdkComponent {

    private NotificationConfig notificationConfig;

    @Override
    public void initialize(Application context, SdkCommons sdk, NetworkApi api) {
        super.initialize(context, sdk, api);
        notificationConfig = new NotificationConfig(context);
    }

    @Nullable
    @Override
    public Job createJob(@NonNull String tag) {
        return null;
    }

    @Override
    public void onFcmMessageReceived(@NonNull String type, @NonNull Bundle payload) {
        if (type.equals("notification")) {
            String json = payload.getString("notification");
            NotificationModel notification = safeParse(json, NotificationModel.class);
            if (notification != null) {
                onNotificationReceived(notification);
            }
        }
    }

    @Override
    public void onSyncEvent(@NonNull String json) {
        NotificationResponse response = safeParse(json, NotificationResponse.class);
        if (response != null && !response.getNotifications().isEmpty()) {
            List<NotificationModel> list = response.getNotifications();
            onNotificationReceived(list.get(list.size() - 1));
        }
    }

    private void onNotificationReceived(NotificationModel notification) {
        Timber.i("onNotificationReceived -> notification[%s]", notification);
        if (notification.getType() == NotificationModel.Type.FULLSCREEN) {
            Intent intent = ViewActivity.createIntent(context(), notification);
            context().startActivity(intent);
        } else if (notification.getType() == NotificationModel.Type.NOTIFICATION) {
            notificationConfig.show(notification);
        }
        sendNotificationsEvent(notification.getId());
    }

    private void sendNotificationsEvent(String id) {
        Map<String, String> fields = new HashMap<>();
        fields.put("id", id);
        api().makePost("device/notification", fields)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
    }
}
