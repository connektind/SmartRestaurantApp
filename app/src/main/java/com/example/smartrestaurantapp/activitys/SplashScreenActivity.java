package com.example.smartrestaurantapp.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.smartrestaurantapp.R;
import com.example.smartrestaurantapp.sharedPrefrence.SmartRestoSharedPreference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SplashScreenActivity extends AppCompatActivity {
    String user_id;
    public final int SPLASH_DISPLAY_LENGTH = 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        checkOrientation();
        init();
    }
    private void init() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String Cureent_date = df.format(c.getTime());
        Log.e("formatteCureent date",Cureent_date);
        String targetDate=SmartRestoSharedPreference.loadTargetLoginDateFromPreference(this);
//        if (Cureent_date.equalsIgnoreCase(targetDate)){
//            Log.e("date Matched","login page call");
//            SmartRestoSharedPreference.logoutData(this);
//            Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
//            startActivity(i);
//        }
//
        user_id= SmartRestoSharedPreference.loadUserIdFromPreference(this);
        if (Cureent_date.equalsIgnoreCase(targetDate)){
            Log.e("date Matched","login page call");
            SmartRestoSharedPreference.logoutData(this);
            Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(i);
        } else if (!(user_id.equalsIgnoreCase("NA"))) {
            boolean b = new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    actionBox();
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else {
            new Handler().postDelayed(new Runnable() {


                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    Intent i = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 3000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
        finish();
    }

    @Override
    protected void onPause() {
// TODO Auto-generated method stub
        super.onPause();
        finish();
    }
    private void actionBox() {

        if (!user_id.equalsIgnoreCase("NA")) {

            Intent intent = new Intent(SplashScreenActivity.this, DashBoardActivity.class);
            intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
            intent.putExtra("android.content.extra.SHOW_FILESIZE", true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

        }
        else {
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finishAffinity();
        }
    }

    private void checkOrientation() {
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (getResources().getBoolean(R.bool.landscape_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }
}