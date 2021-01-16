package com.example.smartrestaurantapp.sharedPrefrence;

import android.content.Context;
import android.content.SharedPreferences;

public class UnclearSmartRestorantSharedPrefrence {
    public static final String PREFS_NAME = "SMARTRESTO_PREF_UNCLER";
    public static final String FIREBASE_TOKEN="FireBaseToken";
    public static void saveFirebaseTokenToPreference(Context context, String FireBaseToken){
        try{
            SharedPreferences.Editor editor =  context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE).edit();
            editor.putString(FIREBASE_TOKEN,FireBaseToken);
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String loadFirebaseTokenFromPreference(Context ctx){
        String FireBaseToken="";
        try{
            SharedPreferences pref = ctx.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
            FireBaseToken = pref.getString(FIREBASE_TOKEN,"NA");
        }catch (Exception e){
            e.printStackTrace();
        }
        return FireBaseToken;
    }
}
