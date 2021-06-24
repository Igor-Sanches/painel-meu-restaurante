package app.birdsoft.painelmeurestaurante;

import android.app.Application;

import com.google.firebase.FirebaseApp;

import app.birdsoft.painelmeurestaurante.manager.Conexao;
import app.birdsoft.painelmeurestaurante.service.SendNotification;
import app.birdsoft.painelmeurestaurante.settings.Settings;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        if(Conexao.getFirebaseAuth().getCurrentUser() != null){
            if(Settings.getUID(this) != null)
                SendNotification.UpdateToken(this);
        }
    }
}
