package app.birdsoft.painelmeurestaurante.service;

public class NotificationSender {
    public NotificationSendData data;
    public String to;

    public NotificationSender(NotificationSendData data, String to) {
        this.data = data;
        this.to = to;
    }

}
