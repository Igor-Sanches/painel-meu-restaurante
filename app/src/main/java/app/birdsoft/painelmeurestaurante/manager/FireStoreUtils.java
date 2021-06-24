package app.birdsoft.painelmeurestaurante.manager;

import android.annotation.SuppressLint;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class FireStoreUtils {
    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore db;
    public static FirebaseFirestore getDatabase(){
        if(db == null){
            db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);
        }
        return db;
    }

    public static FirebaseFirestore getDatabaseOnline(){
        if(db == null){
            db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(false)
                    .build();
            db.setFirestoreSettings(settings);
        }
        return db;
    }
}
