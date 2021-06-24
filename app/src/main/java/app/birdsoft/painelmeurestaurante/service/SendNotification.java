package app.birdsoft.painelmeurestaurante.service;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import app.birdsoft.painelmeurestaurante.manager.FirebaseUtils;
import app.birdsoft.painelmeurestaurante.settings.Settings;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotification {
    private final APIService apiService;
    public SendNotification(){
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }

    public static void UpdateToken(Context context){
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token = new Token(refreshToken);
        FirebaseUtils.getDatabaseRef().child("AdminTokens").child(Settings.getUID(context)).child("Tokens").setValue(token);
    }

    public void onPush(NotificationSendData data, Context context){
       FirebaseUtils.getDatabaseRef().child("Tokens").child(Settings.getUID(context)).child(data.getUid_cliente()).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String usertoken = dataSnapshot.getValue(String.class);
                    sendNotifications(usertoken, data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendNotifications(String usertoken, NotificationSendData data) {
        NotificationSender sender = new NotificationSender(data, usertoken);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {

            }

            @Override
            public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
             }
        });
    }

}
