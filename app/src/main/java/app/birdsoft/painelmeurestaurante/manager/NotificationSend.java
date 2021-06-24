package app.birdsoft.painelmeurestaurante.manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import app.birdsoft.painelmeurestaurante.R;
import app.birdsoft.painelmeurestaurante.login.SplashActivity;
import app.birdsoft.painelmeurestaurante.view.PainelPedidosActivity;

public class NotificationSend {

    public static void sendPedido(String title, String message, Context context) {
        try{
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, "notif_0009");
            Intent intent = new Intent(context, Conexao.getFirebaseAuth().getCurrentUser() != null ? PainelPedidosActivity.class : SplashActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            builder.setWhen(System.currentTimeMillis());
            builder.setDefaults(Notification.DEFAULT_ALL);
            builder.setContentIntent(pendingIntent);
            builder.setFullScreenIntent(pendingIntent, true);
            builder.setSmallIcon(R.drawable.delivery_icon);
            builder.setContentText(message);
            builder.setAutoCancel(true);
            builder.setContentTitle(title);
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            builder.setVibrate(new long[]{3000});
            builder.setPriority(Notification.PRIORITY_MAX);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));

            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                String channel_id = "Channel_Delivery_Admin";
                NotificationChannel channel = new NotificationChannel(channel_id, context.getString(R.string.novo_pedido_noti), NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
                builder.setChannelId(channel_id);
            }

            manager.notify(0, builder.build());

        }catch (Exception x){
            new AlertDialog.Builder(context).setMessage(x.getMessage()).show();
        }
    }
}
