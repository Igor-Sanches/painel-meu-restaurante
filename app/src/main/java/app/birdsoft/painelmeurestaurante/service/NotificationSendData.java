package app.birdsoft.painelmeurestaurante.service;

public class NotificationSendData {
    private String title, message, uid_pedido, state_pedido, uid_cliente, notificationType;

    public void setUid_cliente(String uid_cliente) {
        this.uid_cliente = uid_cliente;
    }

    public String getUid_cliente() {
        return uid_cliente;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationSendData(){}

    public String getState_pedido() {
        return state_pedido;
    }

    public String getUid_pedido() {
        return uid_pedido;
    }

    public void setState_pedido(String state_pedido) {
        this.state_pedido = state_pedido;
    }

    public void setUid_pedido(String uid_pedido) {
        this.uid_pedido = uid_pedido;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NotificationSendData(String title, String message, String uid_pedido, String uid_cliente, String state_pedido, NotificationType notificationType) {
        this.title = title;
        this.uid_cliente = uid_cliente;
        this.notificationType = notificationType.toString();
        this.message = message;
        this.uid_pedido = uid_pedido;
        this.state_pedido = state_pedido;
    }
}
