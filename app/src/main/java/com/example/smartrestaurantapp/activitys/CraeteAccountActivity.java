package com.example.smartrestaurantapp.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.smartrestaurantapp.R;

public class CraeteAccountActivity extends AppCompatActivity {
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_craete_account);
        checkOrientation();
        init();
        listner();
    }
    private void checkOrientation() {
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(CraeteAccountActivity.this,LoginActivity.class);
        startActivity(intent);

    }

    private void listner() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CraeteAccountActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        login=findViewById(R.id.login);

    }
}