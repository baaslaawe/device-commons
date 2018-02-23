package main.app.com.keep;

import java.util.Collections;
import java.util.List;

public class NotificationResponse {

    private List<NotificationModel> notifications;

    public List<NotificationModel> getNotifications() {
        return notifications != null ? notifications : Collections.<NotificationModel>emptyList();
    }
}
