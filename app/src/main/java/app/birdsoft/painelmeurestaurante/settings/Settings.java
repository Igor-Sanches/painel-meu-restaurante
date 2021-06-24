package app.birdsoft.painelmeurestaurante.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Settings{
    private static final String NOTIFICATION = "NOTIFICATION", FORMATO_DATA = "FORMATO_DATA";
    private static final String PREFERENCE_FILE_NAME = "BirdSoft_db";
    private static SharedPreferences mSharedPreferences;

    private static SharedPreferences getmSharedPreferencesEditor(Context context){
        if(mSharedPreferences ==null){
            mSharedPreferences = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        }
        return mSharedPreferences;
    }

    public static boolean isConfig(Context context){
        return get("Config", false, context);
    }

    public static void setConfig(boolean config, Context context){
        put("Config", config, context);
    }

    public static boolean isNotification(Context context){
        return get(NOTIFICATION, true, context);
    }

    private static void remove(Context context){
        SharedPreferences.Editor editor = getmSharedPreferencesEditor(context).edit();
        editor.remove("uid");
        editor.apply();
    }

    public static void setFormateDate(String value, Context context){
        put(FORMATO_DATA, value, context);
    }

    public static void put(String key, String value, Context context) {
        SharedPreferences.Editor editor = getmSharedPreferencesEditor(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void put(String key, boolean value, Context context) {
        SharedPreferences.Editor editor = getmSharedPreferencesEditor(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static String get(String key, String _default, Context context) {
        return getmSharedPreferencesEditor(context).getString(key, _default);
    }

    public static boolean get(String key, boolean _default, Context context) {
        return getmSharedPreferencesEditor(context).getBoolean(key, _default);
    }

    public static String getUID(Context context) {
        return get("uid", "null", context);
    }

    public static void setUID(String uid, Context context) {
        put("uid", uid, context);
    }

    public static void delete(Activity activity) {
        remove(activity);
    }
}
