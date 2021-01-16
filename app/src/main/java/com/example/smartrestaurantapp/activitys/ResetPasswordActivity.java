package com.example.smartrestaurantapp.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.smartrestaurantapp.R;

public class ResetPasswordActivity extends AppCompatActivity {
Button reset_my_pass;
ImageView back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
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
        reset_my_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ResetPasswordActivity.this,PasswordSucessfullyActivity.class);
                startActivity(intent);
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ResetPasswordActivity.this, MobileNumberVerifyActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ResetPasswordActivity.this, MobileNumberVerifyActivity.class);
        startActivity(intent);

    }

    private void init() {
        reset_my_pass=findViewById(R.id.reset_my_pass);
        back_btn=findViewById(R.id.back_btn_resetpass);
    }
}