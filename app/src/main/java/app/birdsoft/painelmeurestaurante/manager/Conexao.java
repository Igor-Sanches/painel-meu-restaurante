package app.birdsoft.painelmeurestaurante.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Conexao {

    private static FirebaseAuth firebaseAuth;

    public static FirebaseAuth getFirebaseAuth(){
        if(firebaseAuth == null){
            carregarAuth();
        }
        return firebaseAuth;
    }

    private static void carregarAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {

        };
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public static boolean isConnected(Context context) {
        try{
            NetworkInfo info = ((ConnectivityManager) Objects.requireNonNull(context.getSystemService(Context.CONNECTIVITY_SERVICE))).getActiveNetworkInfo();
            if(info == null){
                return false;
            }
            return info.isConnected();
        }catch (Exception x){
            return false;
        }
    }

}
