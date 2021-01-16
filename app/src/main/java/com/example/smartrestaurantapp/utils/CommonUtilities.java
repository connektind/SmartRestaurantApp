package com.example.smartrestaurantapp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;


public class CommonUtilities {

//    public static boolean isOnline(Context ctx) {
//        ConnectivityManager cm =
//                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//
//        return netInfo != null && netInfo.isConnected();
//    }


    public static boolean isOnline(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        Log.e("net info", String.valueOf(info));
        return (info != null && info.isConnected() && isConnectionFast(info.getType(), info.getSubtype(), context));
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo();
    }

    public static boolean isConnectionFast(int type, int subType, final Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        int downSpeed = nc.getLinkDownstreamBandwidthKbps();
//        int upSpeed = nc.getLinkUpstreamBandwidthKbps();
//        Log.e("downSpeed", String.valueOf(downSpeed));
//        Log.e("upSpeed", String.valueOf(upSpeed));
//        Toast.makeText(context, "is high speed net= "+downSpeed , Toast.LENGTH_LONG).show();
//        Toast.makeText(context, "is low speed net= "+upSpeed, Toast.LENGTH_LONG).show();


               return true;

    }
}

