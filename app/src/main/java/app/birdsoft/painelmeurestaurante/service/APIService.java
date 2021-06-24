package app.birdsoft.painelmeurestaurante.service;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAtob3KU0:APA91bEzWvHvvPvnoQr9NRECUCBdcVt8h3_I-bG3S5vJAz9icvjwr--K3dIMfDcoQ6dfx2dq3tfrWdLWEogcDN4nCv3DRlcgtNMBqg9ilftQ1FRuMo91h7apfoZFt9M8gbL-P0GCrSZg"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}
