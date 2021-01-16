package com.example.smartrestaurantapp.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.example.smartrestaurantapp.adapter.DashBoardItemAdapter;
import com.example.smartrestaurantapp.model.DashboardItemModel;
import com.example.smartrestaurantapp.sharedPrefrence.SmartRestoSharedPreference;
import com.example.smartrestaurantapp.utils.CommonUtilities;
import com.example.smartrestaurantapp.utils.Global;
import com.example.smartrestaurantapp.vollyRequest.VolleySingleton;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DashBoardActivity extends AppCompatActivity  {
    DrawerLayout drawer;
    private static final String TAG_HOME = "home";
    public static String CURRENT_TAG = TAG_HOME;
    NavigationView navigationView;
    private View navHeader;
    Toolbar toolbar = null;
    RecyclerView dashboard_recylerview;
    ArrayList<DashboardItemModel>dashboardItemModelArrayList;
    DashboardItemModel dashboardItemModel;
    DashBoardItemAdapter dashBoardItemAdapter;
    ImageView menu_nav;
    boolean doubleBackToExitPressedOnce = false;
    ProgressDialog progressdialog;
    RelativeLayout show_more;
    String user_id;
    TextView available_order,order_history,settings,order_report,logout,
            order_number,dashboard,user_name;
    ImageView nav_user_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        user_id=SmartRestoSharedPreference.loadUserIdFromPreference(this);
        init();
       // getProfileData();
        listner();
        //getDashBoardData();
        checkOrientation();

    }
    public void getProfileData()
    {
        if (!CommonUtilities.isOnline(this)) {

            Toast.makeText(DashBoardActivity.this, "Please On Your Internet Connection", Toast.LENGTH_LONG).show();
            return;
        }
       ProgressDialog progressdialog = new ProgressDialog(this);
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
                progressdialog.dismiss();
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
                headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(DashBoardActivity.this));
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Global.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(DashBoardActivity.this).addToRequestQueue(stringRequest, string_json);
    }

    private void parseResponseUserProfileData(String response) {
        Log.e("Response", response);
        try {
            JSONObject jsonObject=new JSONObject(response);
            String lstRoles = jsonObject.getString("lstRoles");
            String lstRestaurant = jsonObject.getString("lstRestaurant");
            String restaurantId = jsonObject.getString("restaurantId");
            String firstName = jsonObject.getString("firstName");
            String lastName = jsonObject.getString("lastName");
           // userName.setText(firstName+" "+lastName);
            user_name.setText(firstName+" "+lastName);
            String userName = jsonObject.getString("userName");
            String password = jsonObject.getString("password");
            String roleId = jsonObject.getString("roleId");
            String mobileNumber = jsonObject.getString("mobileNumber");
//            userMobileNo.setText(mobileNumber+"");
//            mobileno_heading.setText(mobileNumber+"");
            String restaurantDetails = jsonObject.getString("restaurantDetails");
            String userId = jsonObject.getString("userId");
            String updatedOn = jsonObject.getString("updatedOn");
            String isActive = jsonObject.getString("isActive");
            String statusMessage=jsonObject.getString("statusMessage");
            String emailAddress=jsonObject.getString("emailAddress");
           // userEmailId.setText(emailAddress);
            String address=jsonObject.getString("address");
           // userAddress.setText(address);
            String userImage=jsonObject.getString("userImage");
            profileToBase64(userImage);

           // progressdialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void getDashBoardData()
    {
        if (!CommonUtilities.isOnline(this)) {

             Toast.makeText(DashBoardActivity.this, "Please On Your Internet Connection", Toast.LENGTH_LONG).show();
            return;

        }
        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(false);
        progressdialog.setMessage("Loading...");
        progressdialog.setTitle("Wait...");
        progressdialog.show();
        final String string_json = "Result";
        String url = Global.WEBBASE_URL + "Restaurant/GetRestaurantPendingOrders/"+user_id;
        Log.e("url",url);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String res = response.toString();
                parseResponseDashBoardData(res);
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
                    Toast.makeText(DashBoardActivity.this,"Inavaild Login.Try again",Toast.LENGTH_LONG).show();
                    progressdialog.dismiss();
                    Intent intent=new Intent(DashBoardActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        // progressDialog.show();
                        parseResponseDashBoardData(response.toString());
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
                headers.put("Authorization",SmartRestoSharedPreference.loadUserTokenFromPreference(DashBoardActivity.this));
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Global.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(DashBoardActivity.this).addToRequestQueue(stringRequest, string_json);
    }

    private void parseResponseDashBoardData(String response) {
        Log.e("Response", response);
        try {
            JSONArray jsonArray=new JSONArray(response);

                    Log.e("array",jsonArray.length()+"");
                    order_number.setText(""+jsonArray.length());

                    for (int i = 0; i < jsonArray.length(); i++) {
                      //  Log.e("menu_length", String.valueOf(i));
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String orderId = jsonObject.getString("orderId");
                        String restaurantId = jsonObject.getString("restaurantId");
                        String subTotal = jsonObject.getString("subTotal");
                        String tax = jsonObject.getString("tax");
                        String deliveryFee = jsonObject.getString("deliveryFee");
                        String total = jsonObject.getString("total");
                        String orderDate = jsonObject.getString("orderDate");
                        String deliveryTypeId = jsonObject.getString("deliveryTypeId");
                        String orderStatusId = jsonObject.getString("orderStatusId");

                        String billingDetailsId = jsonObject.getString("billingDetailsId");
                        String additionalInformation = jsonObject.getString("additionalInformation");
                        String coupounId = null,customerId = null,isCreateAccount=null,
                                isDiffShippingAddr=null,shippingDetailsId=null,processingFee=null,
                                lstOrderDetails=null,billingDetails=null,shippingDetails=null,
                                customer=null,tipAmount=null;
                          try {
                               coupounId=jsonObject.getString("coupounId");
                               customerId = jsonObject.getString("customerId");
                               isCreateAccount = jsonObject.getString("isCreateAccount");
                               isDiffShippingAddr = jsonObject.getString("isDiffShippingAddr");
                               shippingDetailsId = jsonObject.getString("shippingDetailsId");
                               processingFee = jsonObject.getString("processingFee");
                               lstOrderDetails = jsonObject.getString("lstOrderDetails");

                               billingDetails = jsonObject.getString("billingDetails");
                               shippingDetails = jsonObject.getString("shippingDetails");
                               customer = jsonObject.getString("customer");
                               tipAmount = jsonObject.getString("tipAmount");
                          }catch (Exception e){
                              e.printStackTrace();
                          }

                       DashboardItemModel dashboardItemModel = new DashboardItemModel(orderId,restaurantId,subTotal,tax,deliveryFee,total,orderDate,deliveryTypeId,orderStatusId,
                                billingDetailsId,additionalInformation,coupounId,customerId,isCreateAccount,isDiffShippingAddr,
                                shippingDetailsId,processingFee,lstOrderDetails,billingDetails,shippingDetails,customer,tipAmount);
                        dashboardItemModelArrayList.add(dashboardItemModel);
                    }

//                    try {
//                        dashBoardItemAdapter = new DashBoardItemAdapter(DashBoardActivity.this,dashboardItemModelArrayList);
//                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
//                        dashboard_recylerview.setLayoutManager(linearLayoutManager);
//                        dashboard_recylerview.setAdapter(dashBoardItemAdapter);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
                    progressdialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    private void checkOrientation() {
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getProfileData();
            getDashBoardData();

        }
        if (getResources().getBoolean(R.bool.landscape_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getProfileData();
            getDashBoardData();
        }
    }

    private void listner() {
//        available_order.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                drawer.closeDrawers();
//                Intent intent=new Intent(DashBoardActivity.this,AvailableOrderActivity.class) ;
//                startActivity(intent);
//            }
//
//        });
        show_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashBoardActivity.this,AvailableOrderActivity.class);
                startActivity(intent);
            }
        });
//        order_history.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                drawer.closeDrawers();
//                Intent intent=new Intent(DashBoardActivity.this,OrderHistroryActivity.class) ;
//                startActivity(intent);
//            }
//        });
//        order_report.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                drawer.closeDrawers();
//                Intent intent=new Intent(DashBoardActivity.this,OrderReportActivity.class);
//                startActivity(intent);
//            }
//        });
//

        menu_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(getApplicationContext(), "Tap again to exit",Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void init() {
        dashboardItemModelArrayList=new ArrayList<>();
        navigationView = findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        available_order=navHeader.findViewById(R.id.available_order);
        order_history=navHeader.findViewById(R.id.order_history);
        settings=navHeader.findViewById(R.id.settings);
        order_report=navHeader.findViewById(R.id.order_report);
        logout=navHeader.findViewById(R.id.logout);
        dashboard=navHeader.findViewById(R.id.dashboard);
        nav_user_profile=navHeader.findViewById(R.id.img_profile_1);
        user_name=navHeader.findViewById(R.id.user_name);
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        menu_nav=findViewById(R.id.menu_nav);
        dashboard_recylerview=findViewById(R.id.dashboard_recylerview);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        order_number=findViewById(R.id.order_no);
        show_more=findViewById(R.id.show_more_dash);
//        navigationView.setNavigationItemSelectedListener(this);


//        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
//        drawer.addDrawerListener(toggle)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeButtonEnabled(true)
    }
    private void profileToBase64(String imageString){
        byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        nav_user_profile.setImageBitmap(decodedByte);
    }


    public void dashboard(MenuItem item) {
        drawer.closeDrawers();
    }

    public void logout(MenuItem item) {
        drawer.closeDrawers();
        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardActivity.this);
                builder.setView(R.layout.exitpopup);
                final AlertDialog alert = builder.create();
                alert.show();
                //dialog.getWindow().setAttributes(windo);
                TextView dialog_cancel = (TextView) alert.findViewById(R.id.dialog_cancel);
                TextView dialog_ok = (TextView) alert.findViewById(R.id.dialog_ok);
                TextView dialog_exit = (TextView) alert.findViewById(R.id.tv_exit);
                TextView exitinformation = (TextView) alert.findViewById(R.id.exitinformation);
                LinearLayout ll_cancel = (LinearLayout) alert.findViewById(R.id.ll_cancel);
                LinearLayout ll_ok = (LinearLayout) alert.findViewById(R.id.ll_ok);
                exitinformation.setText("Do you want to exit from this App?");

                ll_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                    }
                });
                ll_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DashBoardActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        SmartRestoSharedPreference.logoutData(DashBoardActivity.this);
                        startActivity(intent);
                        finish();

                    }
                });
    }

    public void orderReport(MenuItem item) {
        drawer.closeDrawers();
                Intent intent=new Intent(DashBoardActivity.this,OrderReportActivity.class);
                startActivity(intent);
    }

    public void settings(MenuItem item) {
        drawer.closeDrawers();
                Intent intent=new Intent(DashBoardActivity.this,ViewProfileActivity.class);
                startActivity(intent);
    }

    public void orderHistory(MenuItem item) {
        drawer.closeDrawers();
                Intent intent=new Intent(DashBoardActivity.this,OrderHistroryActivity.class) ;
                startActivity(intent);
    }

    public void availableOrder(MenuItem item) {
        drawer.closeDrawers();
                Intent intent=new Intent(DashBoardActivity.this,AvailableOrderActivity.class) ;
                startActivity(intent);
    }
}