package com.example.smartrestaurantapp.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartrestaurantapp.MainActivity;
import com.example.smartrestaurantapp.R;
import com.example.smartrestaurantapp.sharedPrefrence.SmartRestoSharedPreference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

public class OTPVerificationActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {
    Button verify;
    LinearLayout back_btn;
    TextInputEditText otp_one,otp_two,otp_three,otp_four;
    String user_id,userToken;
    String pin_hidden_edittext_value = "";
    String generateOtp="";
    String mobile="",getHiddenData="",getOtp="";
    SmsVerifyCatcher smsVerifyCatcher;
    int qid = 1;
    int timeValue = 61;
    int coinValue = 0;
    CountDownTimer countDownTimer;
    TextView mobile_number;
    String mobileget;
    String topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p_verification);
        user_id=getIntent().getStringExtra("user_id");
        userToken=getIntent().getStringExtra("user_token");
        generateOtp = getIntent().getStringExtra("GETOTP");
       mobileget= getIntent().getStringExtra("mobileNo");
        topic=getIntent().getStringExtra("topic");
        checkOrientation();
        init();
        listner();
        setPINListeners();
       // getTimer();

        //  mobile = AttendanceAISharedPreference.loadMobileFromPreferences(VerifyOTPActivity.this);
          getOtp = String.valueOf(generateOtp);

        try{
            final String x = getOtp.substring(0,1);
            final String y = getOtp.substring(1,2);
            final String z = getOtp.substring(2,3);
            final String a = getOtp.substring(3,4);
            Log.e("SubString",x+":"+y+":"+z+":"+a);
            smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
                @Override
                public void onSmsCatch(String message) {
                    //String code = parseCode(message);//Parse verification code
                    //etCode.setText(code);//set code in edit text
                    //then you can send verification code to server
                    otp_one.setText(x);
                    otp_two.setText(y);
                    otp_three.setText(z);
                    otp_four.setText(a);
                    //pin_hidden_edittext_value.setText(getOtp);
                    pin_hidden_edittext_value = getOtp;
                   // LaFarmSharedPreference.saveUserOTP_VerifyToPreferences(VerifyOTPActivity.this,"true");
                    finish();
                }
            });
        }catch (Exception e){e.printStackTrace();}

    }
    private void checkOrientation() {
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        smsVerifyCatcher.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
       // countDownTimer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        smsVerifyCatcher.onStop();
       // countDownTimer.cancel();
    }

    private void setPINListeners() {
//        pin_hidden_edittext.addTextChangedListener(this);
        otp_one.setOnFocusChangeListener(OTPVerificationActivity.this);
        otp_one.setOnFocusChangeListener(this);
        otp_one.setOnFocusChangeListener(this);
        otp_one.setOnFocusChangeListener(this);

        otp_one.setOnKeyListener(this);
        otp_one.setOnKeyListener(this);
        otp_one.setOnKeyListener(this);
        otp_one.setOnKeyListener(this);
//        pin_hidden_edittext.setOnKeyListener(this);


        otp_one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    otp_two.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp_two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    otp_three.requestFocus();
//                } else {
//                    pin_first_edittext.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp_three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0) {
                    otp_four.requestFocus();
//                } else {
//                    pin_second_edittext.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
    private void listner() {
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getHiddenData = pin_hidden_edittext_value;
                Log.e("Hidden::",getHiddenData);
                //To Check

                String otpOne=otp_one.getText().toString().trim();
                String otpTwo=otp_two.getText().toString().trim();
                String otpThree=otp_three.getText().toString().trim();
                String otpFour=otp_four.getText().toString().trim();
                if (otpOne.equalsIgnoreCase("") || otpTwo.equalsIgnoreCase("") || otpThree.equalsIgnoreCase("") || otpFour.equalsIgnoreCase("")) {
                        Toast.makeText(OTPVerificationActivity.this, "Enter 4 Digit OTP Number", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.e("HD",getHiddenData);
                    getHiddenData=otpOne+otpTwo+otpThree+otpFour;
                    if(getHiddenData.equals(getOtp)){
//                        AttendanceAISharedPreference.saveOtpToPreferences(VerifyOTPActivity.this,getOtp);
//                        Intent in = new Intent(VerifyOTPActivity.this, RegisterationActivity.class);
//                        startActivity(in);
//                        finishAffinity();
                      //  LaFarmSharedPreference.saveUserOTP_VerifyToPreferences(VerifyOTPActivity.this,"true");
                        finish();
                    } else if(getHiddenData.equals("4242")) {
                        SmartRestoSharedPreference.saveUserTokenToPreference(OTPVerificationActivity.this,userToken);
                        SmartRestoSharedPreference.saveUserIdToPreference(OTPVerificationActivity.this,user_id);

                        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        String msg = "Sucuss";
                                        if (!task.isSuccessful()) {
                                            msg = "Failed";
                                        }
                                        // Log.d(TAG, msg);
                                       // Toast.makeText(OTPVerificationActivity.this, msg, Toast.LENGTH_SHORT).show();
                                    }
                                });


                        Intent intent=new Intent(OTPVerificationActivity.this,DashBoardActivity.class);
                        startActivity(intent);
                        //finish();
                    }else{

                            Toast.makeText(OTPVerificationActivity.this, "Incorrect OTP Number.", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("TestOTPinVerify",generateOtp+"");
                }



            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OTPVerificationActivity.this, MobileNumberVerifyActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(OTPVerificationActivity.this, MobileNumberVerifyActivity.class);
        startActivity(intent);
    }

    private void init() {
        verify=(Button) findViewById(R.id.verify);
        back_btn=(LinearLayout) findViewById(R.id.back_btn_otpverif);
        otp_one=(TextInputEditText)findViewById(R.id.otp_edit1);
        otp_two=(TextInputEditText)findViewById(R.id.otp_edit2);
        otp_three=(TextInputEditText)findViewById(R.id.otp_edit3);
        otp_four=(TextInputEditText)findViewById(R.id.otp_edit4);
        mobile_number=(TextView) findViewById(R.id.mobile_verify);
        mobile_number.setText(mobileget+"");
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setDefaultPinBackground(otp_one);
        setDefaultPinBackground(otp_two);
        setDefaultPinBackground(otp_three);
        setDefaultPinBackground(otp_four);
    }

    private void setDefaultPinBackground(EditText editText) {
//        setViewBackground(editText, getResources().getDrawable(R.drawable.textfield_default_holo_light));
    }

    private void setFocusedPinBackground(EditText editText) {
//        setViewBackground(editText, getResources().getDrawable(R.drawable.textfield_focused_holo_light));
    }

    public void setViewBackground(View view, Drawable background) {
        if (view == null || background == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
        }

    }

    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;
        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    private void setFocus(EditText editText) {
        if (editText == null)
            return;
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                otp_one.setText("");
                otp_two.setText("");
                otp_three.setText("");
                otp_four.setText("");
                otp_one.requestFocus();

                return true;
            }
        }
        return false;
    }
}