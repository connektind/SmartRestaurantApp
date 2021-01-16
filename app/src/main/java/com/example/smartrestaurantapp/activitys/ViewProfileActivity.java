package com.example.smartrestaurantapp.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.smartrestaurantapp.R;
import com.example.smartrestaurantapp.sharedPrefrence.SmartRestoSharedPreference;
import com.example.smartrestaurantapp.utils.CommonUtilities;
import com.example.smartrestaurantapp.utils.Global;
import com.example.smartrestaurantapp.vollyRequest.VolleySingleton;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ViewProfileActivity extends AppCompatActivity {
    ImageButton edit_profile;
    ImageButton back_btn_profile_show;
    TextView name_heading,mobileno_heading;
    TextView userName,userMobileNo,userEmailId,userAddress;
    ProgressDialog progressdialog;
    String user_id,restaurant_id;
    ImageView imgProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        user_id=SmartRestoSharedPreference.loadUserIdFromPreference(this);
        init();
        listner();
        checkOrientation();
       // getProfileData();
    }

    public void getProfileData()
    {
        if (!CommonUtilities.isOnline(this)) {

            Toast.makeText(ViewProfileActivity.this, "Please On Your Internet Connection", Toast.LENGTH_LONG).show();
            return;
        }
        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(false);
        progressdialog.setMessage("Loading...");
        progressdialog.setTitle("Wait...");
        progressdialog.show();
        final String string_json = "Result";

        String   url = Global.WEBBASE_URL +"User/GetUserProfileDetails/"+user_id;
        //}
        Log.e("url",url);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String res = response.toString();
                parseResponseUserProfileData(res);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressDialog.dismiss();
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
                }
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        // progressDialog.show();
                        parseResponseUserProfileData(response.toString());
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }

            }
        }){

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
                headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(ViewProfileActivity.this));
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Global.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(ViewProfileActivity.this).addToRequestQueue(stringRequest, string_json);
    }

    private void parseResponseUserProfileData(String response) {
        Log.e("Response", response);
        try {
                   JSONObject jsonObject=new JSONObject(response);
                String lstRoles = jsonObject.getString("lstRoles");
                String lstRestaurant = jsonObject.getString("lstRestaurant");
                String restaurantId = jsonObject.getString("restaurantId");
                restaurant_id=restaurantId;
                String firstName = jsonObject.getString("firstName");
                String lastName = jsonObject.getString("lastName");
                userName.setText(firstName+" "+lastName);
                name_heading.setText(firstName+" "+lastName);
                String userName = jsonObject.getString("userName");
                String password = jsonObject.getString("password");
                String roleId = jsonObject.getString("roleId");
                String mobileNumber = jsonObject.getString("mobileNumber");
                userMobileNo.setText(mobileNumber+"");
                mobileno_heading.setText(mobileNumber+"");
                String restaurantDetails = jsonObject.getString("restaurantDetails");
                String userId = jsonObject.getString("userId");
                String updatedOn = jsonObject.getString("updatedOn");
                String isActive = jsonObject.getString("isActive");
                String statusMessage=jsonObject.getString("statusMessage");
                String emailAddress=jsonObject.getString("emailAddress");
                userEmailId.setText(emailAddress);
                String address=jsonObject.getString("address");
                userAddress.setText(address);
            String userImage=jsonObject.getString("userImage");
            profileToBase64(userImage);

            progressdialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void profileToBase64(String imageString){
        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imgProfile.setImageBitmap(decodedByte);
    }



    private void checkOrientation() {
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getProfileData();
        }
        if (getResources().getBoolean(R.bool.landscape_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getProfileData();
        }
    }

    private void listner() {
        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewProfileActivity.this,EditProfileActivity.class);
                intent.putExtra("restaurant_id",restaurant_id);
                startActivity(intent);
            }
        });
        back_btn_profile_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ViewProfileActivity.this,DashBoardActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ViewProfileActivity.this,DashBoardActivity.class);
        startActivity(intent);
    }

    private void init() {
        imgProfile=(ImageView)findViewById(R.id.img_profile_1);
        edit_profile=findViewById(R.id.edit_profile);
        back_btn_profile_show=findViewById(R.id.back_btn_profile_show);
        name_heading=(TextView)findViewById(R.id.user_name_heading);
        mobileno_heading=(TextView)findViewById(R.id.user_mobile_heading);
        userName=(TextView)findViewById(R.id.user_name);
        userMobileNo=(TextView)findViewById(R.id.user_mobile_no);
        userEmailId=(TextView)findViewById(R.id.user_email_id);
        userAddress=(TextView)findViewById(R.id.user_address);

    }
}