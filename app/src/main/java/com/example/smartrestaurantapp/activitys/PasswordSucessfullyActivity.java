package com.example.smartrestaurantapp.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.smartrestaurantapp.R;

public class PasswordSucessfullyActivity extends AppCompatActivity {
Button btn_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_sucessfully);
        checkOrientation();
         init();
         listner();
    }

    private void listner() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PasswordSucessfullyActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(PasswordSucessfullyActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void init() {
        btn_login=(Button)findViewById(R.id.btn_login);

    }

    private void checkOrientation() {
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

}