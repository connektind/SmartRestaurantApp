package com.example.smartrestaurantapp.sharedPrefrence;

import android.content.Context;
import android.content.SharedPreferences;

public class SmartRestoSharedPreference {
    public static final String PREFS_NAME = "SMARTRESTO_PREF";
    public static final String USER_ID="UserId";
    public static final String USER_TOKEN="UserToken";
    public static final String FIREBASE_TOKEN="FireBaseToken";
    public static final String LOGINDATE="LoginDate";
    public  static final String TARGETLOGINDATE="TargetLoginDate";

    public static void logoutData(Context context){
        try{
            SharedPreferences.Editor editor =  context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void saveLoginDateToPreference(Context context,String LoginDate){
        try{
            SharedPreferences.Editor editor =  context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE).edit();
            editor.putString(LOGINDATE,LoginDate);
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String loadLoginDateFromPreference(Context ctx){
        String LoginDate="";
        try{
            SharedPreferences pref = ctx.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
            LoginDate = pref.getString(LOGINDATE,"NA");
        }catch (Exception e){
            e.printStackTrace();
        }
        return LoginDate;
    }


    public static void saveTargetLoginDatePreference(Context context,String TargetLoginDate){
        try{
            SharedPreferences.Editor editor =  context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE).edit();
            editor.putString(TARGETLOGINDATE,TargetLoginDate);
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String loadTargetLoginDateFromPreference(Context ctx){
        String TargetLoginDate="";
        try{
            SharedPreferences pref = ctx.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
            TargetLoginDate = pref.getString(TARGETLOGINDATE,"NA");
        }catch (Exception e){
            e.printStackTrace();
        }
        return TargetLoginDate;
    }



    public static void saveUserIdToPreference(Context context,String UserId){
        try{
            SharedPreferences.Editor editor =  context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE).edit();
            editor.putString(USER_ID,UserId);
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String loadUserIdFromPreference(Context ctx){
        String UserId="";
        try{
            SharedPreferences pref = ctx.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
            UserId = pref.getString(USER_ID,"NA");
        }catch (Exception e){
            e.printStackTrace();
        }
        return UserId;
    }
    public static void saveUserTokenToPreference(Context context,String UserToken){
        try{
            SharedPreferences.Editor editor =  context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE).edit();
            editor.putString(USER_TOKEN,UserToken);
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String loadUserTokenFromPreference(Context ctx){
        String UserToken="";
        try{
            SharedPreferences pref = ctx.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
            UserToken = pref.getString(USER_TOKEN,"NA");
        }catch (Exception e){
            e.printStackTrace();
        }
        return UserToken;
    }

}
