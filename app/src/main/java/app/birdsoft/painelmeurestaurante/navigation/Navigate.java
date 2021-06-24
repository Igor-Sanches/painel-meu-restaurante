package app.birdsoft.painelmeurestaurante.navigation;

import android.app.Activity;
import android.content.Intent;

public class Navigate {
    private final Activity activity;
    public Navigate(Activity activity){
        this.activity=activity;
    }

    public static Navigate activity(Activity activity){
        return new Navigate(activity);
    }

    public void navigate(Class<?> classe){
        activity.startActivity(new Intent(activity, classe));
    }

    public void navigate(Intent intent) {
        activity.startActivity(intent);
    }

    public void navigateForResult(Intent intent, int i) {
        activity.startActivityForResult(intent, i);
    }

}
