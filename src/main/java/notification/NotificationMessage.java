package notification;

import com.google.gson.annotations.SerializedName;

public class NotificationMessage {
    @SerializedName("to") //  "to" changed to token
    private String token;

    @SerializedName("notification")
    private NotificationModel notification;

    @SerializedName("data")
    private MessageData data;

    public NotificationMessage(String token, NotificationModel notification, MessageData data) {
        this.token = token;
        this.notification = notification;
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NotificationModel getNotification() {
        return notification;
    }

    public void setNotification(NotificationModel notification) {
        this.notification = notification;
    }

    public MessageData getData() {
        return data;
    }

    public void setData(MessageData data) {
        this.data = data;
    }
}
