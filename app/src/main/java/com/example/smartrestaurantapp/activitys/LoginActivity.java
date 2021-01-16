package com.example.smartrestaurantapp.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.smartrestaurantapp.R;
import com.example.smartrestaurantapp.sharedPrefrence.SmartRestoSharedPreference;
import com.example.smartrestaurantapp.sharedPrefrence.UnclearSmartRestorantSharedPrefrence;
import com.example.smartrestaurantapp.utils.CommonUtilities;
import com.example.smartrestaurantapp.utils.Global;
import com.example.smartrestaurantapp.vollyRequest.VolleySingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextView signup,forget_password;
    Button login_btn;
    TextInputEditText username_login,password_login;
    ProgressDialog progressdialog;
    private boolean isPasswordVisible;
    TextInputLayout password_layout;
    String device_id = "";
    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.e("device_id",device_id);
        Log.e("firebase_token", UnclearSmartRestorantSharedPrefrence.loadFirebaseTokenFromPreference(this));
        init();
        listner();
        checkOrientation();
       // togglePassVisability();
    }
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        // Save UI state changes to the savedInstanceState.
//        // This bundle will be passed to onCreate if the process is
//        // killed and restarted.
//        // etc.
//    }
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        // Restore UI state from the savedInstanceState.
//        // This bundle has also been passed to onCreate.
////        boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
////        double myDouble = savedInstanceState.getDouble("myDouble");
////        int myInt = savedInstanceState.getInt("MyInt");
////        String myString = savedInstanceState.getString("MyString");
//    }

    private void checkOrientation() {
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (getResources().getBoolean(R.bool.landscape_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }


    private void listner() {
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,CraeteAccountActivity.class);
                startActivity(intent);
            }
        });
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                Intent intent=new Intent(LoginActivity.this, WebViewForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mobile = username_login.getText().toString().trim();
                final String password = password_login.getText().toString().trim();
                if (mobile.isEmpty()) {
                    username_login.setError("Please Enter Username/Mobile Number");
                    username_login.requestFocus();
                } else if (password.length() < 6) {
                    password_login.setError("Please Enter Password",null);
                    password_login.requestFocus();
                   // password_layout.togg
                  //  password_layout.setPasswordVisibilityToggleEnabled(false);
                } else {
                    login_btn.setClickable(false);
//                    Intent intent=new Intent(LoginActivity.this, ParentDashBoard.class);
//                    startActivity(intent);
                    getLogin(mobile, password);
                }
            }
        });
    }


    private void getLogin(final String mobile, final String password) {
        if (!CommonUtilities.isOnline(LoginActivity.this)) {
            Toast.makeText(LoginActivity.this, "Please On Your Internet Connection", Toast.LENGTH_LONG).show();
            return;
        }
        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(false);
        progressdialog.setMessage("Loading...");
        progressdialog.setTitle("Wait...");
        progressdialog.show();

        String url= Global.WEBBASE_URL+"Auth/Login";
        JSONObject params = new JSONObject();
        String mRequestBody = "";

        try {

            params.put("UserName", mobile);
            params.put("Password", password);
            params.put("DeviceId",device_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRequestBody = params.toString();
        Log.e("Request Body", mRequestBody);
        final String finalMRequestBody = mRequestBody;

        login_btn.setClickable(true);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String res = response.toString();
                parseResponseLoginUser(res, progressdialog,mobile,password);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //pd.dismiss();
                NetworkResponse response = error.networkResponse;
                Log.e("com.Aarambh", "error response " + response);
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Log.e("mls", "VolleyError TimeoutError error or NoConnectionError");
                } else if (error instanceof AuthFailureError) {                    //TODO
                    Log.e("mls", "VolleyError AuthFailureError");
                } else if (error instanceof ServerError) {
                    Log.e("mls", "VolleyError ServerError");
                } else if (error instanceof NetworkError) {
                    Log.e("mls", "VolleyError NetworkError");
                } else if (error instanceof ParseError || error instanceof VolleyError) {
                    Log.e("mls", "VolleyError TParseError");
                    Log.e("Volley Error", error.toString());
                    progressdialog.dismiss();
                    Toast.makeText(LoginActivity.this,"Invalid Credentials",Toast.LENGTH_LONG).show();
                }
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        // progressDialog.show();
                        parseResponseLoginUser(response.toString(), progressdialog,mobile,password);

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }

            }

        }) {

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                String json;
                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    try {
                        json = new String(volleyError.networkResponse.data,
                                HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));
                    } catch (UnsupportedEncodingException e) {
                        return new VolleyError(e.getMessage());
                    }
                    return new VolleyError(json);
                }
                return volleyError;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }

            // Adding request to request queue
            @Override
            public byte[] getBody() {
                try {
                    return finalMRequestBody == null ? null : finalMRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", finalMRequestBody, "utf-8");
                    return null;
                }
            }

        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                Global.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        jsonObjectRequest.setShouldCache(false);
        // Adding request to request queue
        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    private void parseResponseLoginUser(String response, ProgressDialog progressdialog, final String username, final String password_user) {
        try {
            //getting the whole json object from the response 4878704040
            JSONObject obj = new JSONObject(response);
            Log.e("login_resp", String.valueOf(obj));
            String token=obj.getString("token");
            String userId=obj.getString("userId");
            String message = obj.getString("message");

            login_btn.setClickable(true);
            int status = obj.getInt("status");
            String topic=obj.getString("topic");
            if (message.equalsIgnoreCase("Login Successfull")) {
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(c.getTime());
                Log.e("formatteCureent date",formattedDate);
                SmartRestoSharedPreference.saveLoginDateToPreference(this,formattedDate);
                c.add(Calendar.DATE, 15);
                String formattedDate_next = df.format(c.getTime());
                Log.e("NEXT DATE : ", formattedDate_next);
                SmartRestoSharedPreference.saveTargetLoginDatePreference(this,formattedDate_next);
                SmartRestoSharedPreference.saveUserTokenToPreference(LoginActivity.this,token);
                SmartRestoSharedPreference.saveUserIdToPreference(LoginActivity.this,userId);
                Toast.makeText(LoginActivity.this,""+message,Toast.LENGTH_LONG).show();
                Intent intent=new Intent(LoginActivity.this,DashBoardActivity.class);
                startActivity(intent);

            }
//            else if (message.equalsIgnoreCase("Register FCM Topic")){
//                 Calendar c = Calendar.getInstance();
//                System.out.println("Current time => " + c.getTime());
//                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
//                String formattedDate = df.format(c.getTime());
//                Log.e("formatteCureent date",formattedDate);
//                SmartRestoSharedPreference.saveLoginDateToPreference(this,formattedDate);
//                c.add(Calendar.DATE, 15);
//                String formattedDate_next = df.format(c.getTime());
//                Log.e("NEXT DATE : ", formattedDate_next);
//                SmartRestoSharedPreference.saveTargetLoginDatePreference(this,formattedDate_next);
//               // Toast.makeText(LoginActivity.this,""+message,Toast.LENGTH_LONG).show();
//                Intent intent=new Intent(LoginActivity.this,MobileNumberVerifyActivity.class);
//                intent.putExtra("user_id",userId);
//                intent.putExtra("userToken",token);
//                intent.putExtra("topic",topic);
//                startActivity(intent);
//            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        finishAffinity();

    }

    private void init() {
        signup=(TextView)findViewById(R.id.signup);
        forget_password=(TextView)findViewById(R.id.forget_password);
        login_btn=findViewById(R.id.btn_login);
        username_login=findViewById(R.id.username_login);
        password_login=findViewById(R.id.password_login);
        password_login.setTransformationMethod(null);
        password_layout=findViewById(R.id.password);
       // password.setEndIconCheckable(true);

//        password.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (password.isSelected()) {
//                    password.setSelected(false);
//                    Log.e("mytag", "in case 1");
//                    password_login.setInputType(InputType.TYPE_CLASS_TEXT);
//                } else {
//                    Log.e("mytag", "in case 1");
//                    password.setSelected(true);
//                    password_login.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                }
//            }
//        });

    }

}