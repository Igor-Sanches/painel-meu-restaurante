package app.birdsoft.painelmeurestaurante.service;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import app.birdsoft.painelmeurestaurante.manager.NotificationSend;
import app.birdsoft.painelmeurestaurante.settings.Settings;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class MyFireBaseMessagingService extends FirebaseMessagingService {
    String title,message, id_pedido, state_pedido, notificationType;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(Settings.isNotification(this)){
            title=remoteMessage.getData().get("title");
            message=remoteMessage.getData().get("message");
            id_pedido=remoteMessage.getData().get("uid_pedido");
            state_pedido=remoteMessage.getData().get("state_pedido");
            notificationType=remoteMessage.getData().get("notificationType");
            NotificationSend.sendPedido(title, message, this);
        }
    }
}
