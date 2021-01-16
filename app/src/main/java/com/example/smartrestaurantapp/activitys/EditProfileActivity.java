package com.example.smartrestaurantapp.activitys;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.example.smartrestaurantapp.vollyRequest.StringRequestVolley;
import com.example.smartrestaurantapp.vollyRequest.VolleySingleton;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {
    ImageButton profileedite_back_btn;
    Button save_detail;
    ProgressDialog progressdialog;
    String user_id, restauant_id, username_unique;
    EditText user_first_name, user_mobile, user_email, user_address, user_last_name;
    TextView name_show, mobile_show;
    Bitmap profilebitmap=null;
    ImageView imgProfile;
    TextView data_not_found;
    private LruCache<String, Bitmap> mMemoryCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        user_id = SmartRestoSharedPreference.loadUserIdFromPreference(this);
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

// Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
        init();
        listner();
        checkOrientation();

       // getProfileData();
    }

    public void getProfileData() {
        if (!CommonUtilities.isOnline(this)) {

            Toast.makeText(EditProfileActivity.this, "Please On Your Internet Connection", Toast.LENGTH_LONG).show();
            return;
        }
        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(false);
        progressdialog.setMessage("Loading...");
        progressdialog.setTitle("Wait...");
        progressdialog.show();
        final String string_json = "Result";

        String url = Global.WEBBASE_URL + "User/GetUserProfileDetails/" + user_id;
        //}
        Log.e("url", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
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
                headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(EditProfileActivity.this));
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Global.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(EditProfileActivity.this).addToRequestQueue(stringRequest, string_json);
    }

    private void parseResponseUserProfileData(String response) {
        Log.e("Response", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            String lstRoles = jsonObject.getString("lstRoles");
            String lstRestaurant = jsonObject.getString("lstRestaurant");
            String restaurantId = jsonObject.getString("restaurantId");
            restauant_id = restaurantId;
            String firstName = jsonObject.getString("firstName");
            user_first_name.setText(firstName + "");
            String lastName = jsonObject.getString("lastName");
            user_last_name.setText(lastName + "");
            name_show.setText(firstName + " " + lastName);
            String userName = jsonObject.getString("userName");
            username_unique = userName;
            String password = jsonObject.getString("password");
            String roleId = jsonObject.getString("roleId");
            String mobileNumber = jsonObject.getString("mobileNumber");
            user_mobile.setText(mobileNumber + "");
            mobile_show.setText(mobileNumber + "");
            String restaurantDetails = jsonObject.getString("restaurantDetails");
            String userId = jsonObject.getString("userId");
            String updatedOn = jsonObject.getString("updatedOn");
            String isActive = jsonObject.getString("isActive");
            String statusMessage = jsonObject.getString("statusMessage");
            String emailAddress = jsonObject.getString("emailAddress");
            user_email.setText(emailAddress);
            String address = jsonObject.getString("address");
            user_address.setText(address);
            String userImage=jsonObject.getString("userImage");
            profileToBase64(userImage);

            progressdialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            try {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                imgProfile.setImageBitmap(thumbnail);
                profilebitmap = thumbnail;
//
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == 102 && resultCode == RESULT_OK) {
            try {
                Uri selectedImage = data.getData();
                //Bitmap bit_profile = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(String.valueOf(selectedImage)));
                Bitmap bit_profile = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                profilebitmap = bit_profile;
                imgProfile.setImageURI(selectedImage);
               // Log.e("profileThumbnail", String.valueOf(profile_uri));
                Log.e("profileSelected", String.valueOf(selectedImage));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
    private void selectProfileTypePic() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};

        TextView title = new TextView(EditProfileActivity.this);
        title.setText("Add Photo!");
        //   int[] colors = {Color.parseColor(base_color_one), Color.parseColor(base_color_two)};

        //create a new gradient color
//            GradientDrawable gd = new GradientDrawable(
//                    GradientDrawable.Orientation.TOP_BOTTOM, colors);
//
//
//            gd.setCornerRadius(0f);
//            //apply the button background to newly created drawable gradient
//            title.setBackground(gd);
//            title.setPadding(10, 15, 15, 10);
//            title.setGravity(Gravity.CENTER);
//            title.setTextColor(Color.WHITE);
//            title.setTextSize(22);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                EditProfileActivity.this);
        builder.setCustomTitle(title);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    openCameraForProfile();
                } else if (items[item].equals("Choose from Gallery")) {
                    openGalleryForProfile();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }
    public void openCameraForProfile() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 101);
    }

    public void openGalleryForProfile() {
        //Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 102);
    }

    private void listner() {
        profileedite_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, ViewProfileActivity.class);
                startActivity(intent);
            }
        });
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfileTypePic();
            }
        });
        save_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userFirstName = user_first_name.getText().toString().trim();
                String userlastName = user_last_name.getText().toString().trim();

                String userMobile = user_mobile.getText().toString().trim();
                String userEmail = user_email.getText().toString().trim();
                String userAddress = user_address.getText().toString().trim();
              //  String mobilep = "[6-9]{1}[0-9]{9}";
                String mobile_usa="[2-9]{1}[0-9]{2}[2-9]{1}[0-9]{6}";
                String validemail = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{0,64}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,253}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";
                Matcher matcherObj = Pattern.compile(validemail).matcher(userEmail);
                if (imgProfile.getDrawable() == null) {
                    ((LinearLayout) findViewById(R.id.ll_profileimagenotuploaded)).setVisibility(View.VISIBLE);
                    data_not_found.setError("Please Upload Profile Picture.");
                    data_not_found.setText("Please Upload Profile Picture.");
                    data_not_found.requestFocus();
                } else if (userFirstName.isEmpty()) {
                    user_first_name.setError("Please Enter First Name");
                    user_first_name.requestFocus();
                } else if (userlastName.isEmpty()) {
                    user_last_name.setError("Please Enter Last Name");
                    user_last_name.requestFocus();
                } else if (userMobile.isEmpty()) {
                    user_mobile.setError("Please Enter Mobile Number");
                    user_mobile.requestFocus();
                } else if (userMobile.length() < 10) {
                    user_mobile.setError("Please Enter 10 Digit Mobile Number");
                    user_mobile.requestFocus();
                }

                else if (!userMobile.matches(mobile_usa)) {
                    user_mobile.setError("Mobile Number Should Start With 2,3,4,5,6,7,8 or 9.Please Enter Valid Mobile Number.");
                    user_mobile.requestFocus();
                }
                else if (userEmail.isEmpty()) {
                    user_email.setError("Please Enter Email Id");
                    user_email.requestFocus();
                }
                else if (!matcherObj.matches()) {
                    user_email.setError("Please Enter Valid Email");
                    user_email.requestFocus();
                }
                else if (userAddress.isEmpty()) {
                    user_address.setError("Please Enter Address");
                    user_address.requestFocus();

                } else {
                    save_detail.setEnabled(false);
                    progressdialog = new ProgressDialog(EditProfileActivity.this);
                    progressdialog.setCancelable(false);
                    progressdialog.setMessage("Loading...");
                    progressdialog.setTitle("Wait...");
                    progressdialog.show();
                    String profileimage = "";

                    try {
                        Log.e("profile_bitmap", String.valueOf(profilebitmap));
                        profileimage=convertBase64Profile(profilebitmap);
                        profileimage=profileimage.replaceAll("\\n","");
                        Log.e("Update Profile", profileimage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    updateUserProfileData(progressdialog,userFirstName, userlastName, userMobile, userEmail, userAddress,profileimage);
                }
            }
        });

    }
    private void profileToBase64(String imageString){
        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        profilebitmap=decodedByte;
        imgProfile.setImageBitmap(decodedByte);
    }

    private String convertBase64Profile(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
     //   Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public Map<String, String> myHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(EditProfileActivity.this));
        return headers;
    }

    private void updateUserProfileData(ProgressDialog progressdialog,String userFirstName, String userlastName, String userMobile, String userEmail, String userAddress,String profileBase64) {
        if (!CommonUtilities.isOnline(this)) {
            Toast.makeText(EditProfileActivity.this, "Please On Your Internet Connection", Toast.LENGTH_LONG).show();
            return;

        }
        String url = Global.WEBBASE_URL + "User/EditUserProfileDetails";

        final String string_json = "Result";
//        progressdialog = new ProgressDialog(this);
//        progressdialog.setCancelable(false);
//        progressdialog.setMessage("Loading...");
//        progressdialog.setTitle("Wait...");
//        progressdialog.show();
        JSONObject params = new JSONObject();
        String mRequestBody = "";
        try {

            params.put("restaurantId", Integer.valueOf(restauant_id));
            params.put("firstName", userFirstName);
            params.put("lastName", userlastName);
            params.put("userName", username_unique);
            params.put("mobileNumber", userMobile);
            params.put("userId", Integer.valueOf(user_id));
            params.put("isActive", true);
            params.put("emailAddress", userEmail);
            params.put("address", userAddress);
            try {
                params.put("userImage", profileBase64);
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.e("apiData---", (params).toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        mRequestBody = params.toString();
        Log.e("Request Body", mRequestBody);
        final String finalMRequestBody = mRequestBody;

        StringRequestVolley stringRequest = new StringRequestVolley(1, url, mRequestBody, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("res", response.toString());
                progressdialog.dismiss();
                Toast.makeText(EditProfileActivity.this, String.valueOf(response), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditProfileActivity.this, DashBoardActivity.class);
                startActivity(intent);
                save_detail.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EditProfileActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, myHeaders());

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Global.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        // Add the request to the RequestQueue.
        // queue.add(booleanRequest);
        VolleySingleton.getInstance(EditProfileActivity.this).addToRequestQueue(stringRequest);
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditProfileActivity.this, ViewProfileActivity.class);
        startActivity(intent);
    }

    private void init() {
        profileedite_back_btn = findViewById(R.id.profileedite_back_btn);
        save_detail = findViewById(R.id.submit_profile);
        user_first_name = (EditText) findViewById(R.id.edit_user_first_name);
        user_last_name = (EditText) findViewById(R.id.edit_user_last_name);
        user_mobile = (EditText) findViewById(R.id.edit_user_mobile);
        user_email = (EditText) findViewById(R.id.edit_user_email);
        user_address = (EditText) findViewById(R.id.edit_user_address);
        imgProfile=(ImageView)findViewById(R.id.img_profile_1);

        name_show = (TextView) findViewById(R.id.name_show);
        mobile_show = (TextView) findViewById(R.id.mobile_show);
        data_not_found=(TextView) findViewById(R.id.data_not_found);

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

}