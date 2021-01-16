package com.example.smartrestaurantapp.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartrestaurantapp.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashSet;
import java.util.Random;

public class MobileNumberVerifyActivity extends AppCompatActivity {
    Button get_otp;
    TextView signup;
    TextInputEditText mobile_number;
    String user_id, userToken;
    String generateOtp = "";
    String topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        generateOtp = getIntent().getStringExtra("GETOTP");
        user_id = getIntent().getStringExtra("user_id");
        userToken = getIntent().getStringExtra("userToken");
        topic=getIntent().getStringExtra("topic");
        checkOrientation();
        init();
        listner();
    }

    private void checkOrientation() {
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    private void listner() {
        get_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNo = mobile_number.getText().toString().trim();
                if (mobileNo.isEmpty()) {
                    mobile_number.setError("Please Enter Verified Mobile Number");
                    mobile_number.requestFocus();
                } else {
                    Random r = new Random();
                    HashSet<Integer> set = new HashSet<Integer>();
                    while (set.size() < 1) {
                        int ran = r.nextInt(99) + 1000;
                        set.add(ran);
                    }
                    int len = 4;
                    String random = String.valueOf(len);
                    for (int random1 : set) {
                        System.out.println(random1);
                        random = Integer.toString(random1);
                        Log.e("random_verify_num", random);
                    }
                    Intent intent = new Intent(MobileNumberVerifyActivity.this, OTPVerificationActivity.class);
                    intent.putExtra("GETOTP", random);
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("user_token", userToken);
                    intent.putExtra("mobileNo",mobileNo);
                    intent.putExtra("topic",topic);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MobileNumberVerifyActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void init() {
        get_otp = findViewById(R.id.get_otp);
        signup = findViewById(R.id.signup);
        mobile_number = (TextInputEditText) findViewById(R.id.mobileno_forgot);
    }
}