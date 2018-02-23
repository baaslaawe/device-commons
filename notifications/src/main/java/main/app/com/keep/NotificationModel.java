package main.app.com.keep;

import java.io.Serializable;

public class NotificationModel implements Serializable {

    public enum Type {
        NOTIFICATION, FULLSCREEN
    }

    private String id;
    private String title;
    private String body;
    private String type;
    private boolean force;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Type getType() {
        if (type.equals("notification")) {
            return Type.NOTIFICATION;
        }
        if (type.equals("fullscreen")) {
            return Type.FULLSCREEN;
        }
        return null;
    }

    public boolean isForce() {
        return force;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", type='" + type + '\'' +
                ", force=" + force +
                '}';
    }
}
