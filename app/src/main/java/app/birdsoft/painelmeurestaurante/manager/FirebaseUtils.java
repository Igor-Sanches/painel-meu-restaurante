package app.birdsoft.painelmeurestaurante.manager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtils {
    private static DatabaseReference mDatabaseRef;
    private static FirebaseDatabase database;
    public static DatabaseReference getDatabaseRef() {
        if (mDatabaseRef == null) {
            getDatabaseOffLine();
            mDatabaseRef = database.getReference();

        }
        return mDatabaseRef;
    }

    public static void getDatabaseOffLine() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);

        }
    }

}
