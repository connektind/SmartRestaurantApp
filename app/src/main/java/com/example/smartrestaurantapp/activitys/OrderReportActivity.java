package com.example.smartrestaurantapp.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.smartrestaurantapp.R;
import com.example.smartrestaurantapp.adapter.OrderReportAdapter;
import com.example.smartrestaurantapp.model.OrderReportModel;
import com.example.smartrestaurantapp.sharedPrefrence.SmartRestoSharedPreference;
import com.example.smartrestaurantapp.utils.CommonUtilities;
import com.example.smartrestaurantapp.utils.Global;
import com.example.smartrestaurantapp.vollyRequest.MyJsonArrayRequest;
import com.example.smartrestaurantapp.vollyRequest.StringRequestVolley;
import com.example.smartrestaurantapp.vollyRequest.VolleySingleton;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrderReportActivity extends AppCompatActivity implements View.OnClickListener {
    Calendar calendar;
    CalendarView calendarView;
    ArrayList<OrderReportModel>orderReportModelArrayList;
    OrderReportAdapter orderReportAdapter;
    RecyclerView order_report_recyclerview;
    ImageView back_btn_orderreport;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", /*Locale.getDefault()*/Locale.ENGLISH);
    private int mYear, mMonth, mDay, mHour, mMinute;
    String date_geting;

    private CompactCalendarView compactCalendarView;
    TextView no_data_found;

    private boolean isExpanded = false;
    private AppBarLayout appBarLayout;
Toolbar toolbar;
    String output_date_api;
    ProgressDialog progressdialog;
    EditText date_selection;
        Button search_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_report);
        init();
        listner();
        checkOrientation();

    }
    private void checkOrientation() {
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getOrderReportData();

        }
        if (getResources().getBoolean(R.bool.landscape_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getOrderReportData();
        }
    }


    private void listner() {
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderReportModelArrayList.clear();
                search_btn.setEnabled(false);
                search_btn.setClickable(false);
                getOrderReportData();
            }
        });
        back_btn_orderreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(OrderReportActivity.this,DashBoardActivity.class);
                startActivity(intent);
            }
        });
        date_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                if (getResources().getBoolean(R.bool.portrait_only)) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(OrderReportActivity.this, R.style.MyAppTheme,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int month, int dayOfMonth) {
                                        if (month == 0) {
                                            date_selection.setText(dayOfMonth + " " + "Jan" + " " + year);
                                            date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);
                                            //jun
                                        } else if (month == 1) {
                                            date_selection.setText(dayOfMonth + " " + "Feb" + " " + year);
                                            date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);
                                        } else if (month == 2) {
                                            date_selection.setText(dayOfMonth + " " + "Mar" + " " + year);
                                            date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);

                                        } else if (month == 3) {
                                            date_selection.setText(dayOfMonth + " " + "Apr" + " " + year);
                                            date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);

                                        } else if (month == 4) {
                                            date_selection.setText(dayOfMonth + " " + "May" + " " + year);
                                            date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);
                                        } else if (month == 5) {
                                            date_selection.setText(dayOfMonth + " " + "June" + " " + year);
                                            date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);

                                        } else if (month == 6) {
                                            date_selection.setText(dayOfMonth + " " + "July" + " " + year);
                                            date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);

                                        } else if (month == 7) {
                                            date_selection.setText(dayOfMonth + " " + "Aug" + " " + year);
                                            date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);


                                        } else if (month == 8) {
                                            date_selection.setText(dayOfMonth + " " + "Sept" + " " + year);
                                            date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);

                                        } else if (month == 9) {
                                            date_selection.setText(dayOfMonth + " " + "Oct" + " " + year);
                                            date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);
                                        } else if (month == 10) {
                                            date_selection.setText(dayOfMonth + " " + "Nov" + " " + year);
                                            date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);

                                        } else if (month == 11) {
                                            date_selection.setText(dayOfMonth + " " + "Dec" + " " + year);
                                            date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);
                                        } else {
                                            date_selection.setText(dayOfMonth + " " + "Jan" + " " + year);
                                            date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);
                                        }
                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                        datePickerDialog.show();
                    }
                } else {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(OrderReportActivity.this, R.style.TabletCalender,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int month, int dayOfMonth) {
                                    if (month == 0) {
                                        date_selection.setText(dayOfMonth + " " + "Jan" + " " + year);
                                        date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);
                                        //jun
                                    } else if (month == 1) {
                                        date_selection.setText(dayOfMonth + " " + "Feb" + " " + year);
                                        date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);
                                    } else if (month == 2) {
                                        date_selection.setText(dayOfMonth + " " + "Mar" + " " + year);
                                        date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);

                                    } else if (month == 3) {
                                        date_selection.setText(dayOfMonth + " " + "Apr" + " " + year);
                                        date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);

                                    } else if (month == 4) {
                                        date_selection.setText(dayOfMonth + " " + "May" + " " + year);
                                        date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);
                                    } else if (month == 5) {
                                        date_selection.setText(dayOfMonth + " " + "June" + " " + year);
                                        date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);

                                    } else if (month == 6) {
                                        date_selection.setText(dayOfMonth + " " + "July" + " " + year);
                                        date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);

                                    } else if (month == 7) {
                                        date_selection.setText(dayOfMonth + " " + "Aug" + " " + year);
                                        date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);


                                    } else if (month == 8) {
                                        date_selection.setText(dayOfMonth + " " + "Sept" + " " + year);
                                        date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);

                                    } else if (month == 9) {
                                        date_selection.setText(dayOfMonth + " " + "Oct" + " " + year);
                                        date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);
                                    } else if (month == 10) {
                                        date_selection.setText(dayOfMonth + " " + "Nov" + " " + year);
                                        date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);

                                    } else if (month == 11) {
                                        date_selection.setText(dayOfMonth + " " + "Dec" + " " + year);
                                        date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);
                                    } else {
                                        date_selection.setText(dayOfMonth + " " + "Jan" + " " + year);
                                        date_geting = (dayOfMonth + "-" + (month + 1) + "-" + year);
                                    }
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                    datePickerDialog.show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent =new Intent(OrderReportActivity.this,DashBoardActivity.class);
        startActivity(intent);
    }

    private void init() {
        orderReportModelArrayList=new ArrayList<>();
        order_report_recyclerview=findViewById(R.id.order_report_recyclerview);
        back_btn_orderreport=findViewById(R.id.back_btn_orderreport);
        appBarLayout = findViewById(R.id.app_bar_layout);
        toolbar = findViewById(R.id.toolbar_order_report);
        date_selection=findViewById(R.id.date_selection);
        search_btn=findViewById(R.id.search_btn);
        no_data_found=findViewById(R.id.no_data_found);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        Log.e("current year", String.valueOf(year));
        Log.e("current month", String.valueOf(month));
        Log.e("current day", String.valueOf(mDay));
        getMonthandYear(mDay,month,year);
    }

    private void getMonthandYear(int dayOfMonth, int month, int year) {
        if (month==0){
            date_selection.setText(dayOfMonth + " " + "Jan" + " " + year);
            date_geting=(dayOfMonth+"-"+(month+1)+"-"+year);
            //jun
        }else if(month==1){
            date_selection.setText(dayOfMonth + " " + "Feb" + " " + year);
            date_geting=(dayOfMonth+"-"+(month+1)+"-"+year);
        }
        else if(month==2){
            date_selection.setText(dayOfMonth + " " + "Mar" + " " + year);
            date_geting=(dayOfMonth+"-"+(month+1)+"-"+year);
        }else if(month==3){
            date_selection.setText(dayOfMonth + " " + "Apr" + " " + year);
            date_geting=(dayOfMonth+"-"+(month+1)+"-"+year);
        }else if(month==4){
            date_selection.setText(dayOfMonth + " " + "May" + " " + year);
            date_geting=(dayOfMonth+"-"+(month+1)+"-"+year);
        }else if(month==5){
            date_selection.setText(dayOfMonth + " " + "June" + " " + year);
            date_geting=(dayOfMonth+"-"+(month+1)+"-"+year);

        }else if(month==6){
            date_selection.setText(dayOfMonth + " " + "July" + " " + year);
            date_geting=(dayOfMonth+"-"+(month+1)+"-"+year);

        }else if(month==7){
            date_selection.setText(dayOfMonth + " " + "Aug" + " " + year);
            date_geting=(dayOfMonth+"-"+(month+1)+"-"+year);


        }else if(month==8){
            date_selection.setText(dayOfMonth + " " + "Sept" + " " + year);
            date_geting=(dayOfMonth+"-"+(month+1)+"-"+year);

        }else if(month==9){
            date_selection.setText(dayOfMonth + " " + "Oct" + " " + year);
            date_geting=(dayOfMonth+"-"+(month+1)+"-"+year);

        }else if(month==10){
            date_selection.setText(dayOfMonth + " " + "Nov" + " " + year);
            date_geting=(dayOfMonth+"-"+(month+1)+"-"+year);


        }else if(month==11){
            date_selection.setText(dayOfMonth + " " + "Dec" + " " + year);
            date_geting=(dayOfMonth+"-"+(month+1)+"-"+year);

        }else {
            date_selection.setText(dayOfMonth + " " + "Jan" + " " + year);
            date_geting=(dayOfMonth+"-"+(month+1)+"-"+year);
        }


    }

    private void setCurrentDate(Date date) {
        setSubtitle(dateFormat.format(date));
        if (compactCalendarView != null) {
            Log.e("date", (dateFormat.format(date)));
            compactCalendarView.setCurrentDate(date);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        TextView tvTitle = findViewById(R.id.title);

        if (tvTitle != null) {
            Log.e("title_date", String.valueOf(title));
            tvTitle.setText(title);
        }
    }

    private void setSubtitle(String subtitle) {
        TextView datePickerTextView = findViewById(R.id.date_picker_text_view);
        if (datePickerTextView != null) {
            Log.e("subtitle_date",subtitle);
            datePickerTextView.setText(subtitle);
            try {

                DateFormat inputFormat = new SimpleDateFormat("dd MMMM yyyy");
                DateFormat outputFormat = new SimpleDateFormat("yyyy-mm-dd");
                Date date = inputFormat.parse(subtitle);

                String outputDateStr = outputFormat.format(date);
                Log.e("OutputDate",outputDateStr);
                 output_date_api = outputDateStr;

            }catch (Exception e){e.printStackTrace();}

        }
    }
    public Map<String, String> myHeaders(){
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(OrderReportActivity.this));
        return headers;
    }
    private void getOrderReportData() {
        if (!CommonUtilities.isOnline(OrderReportActivity.this)) {
            Toast.makeText(OrderReportActivity.this, "Please On Your Internet Connection", Toast.LENGTH_LONG).show();
            return;
        }
        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(false);
        progressdialog.setMessage("Loading...");
        progressdialog.setTitle("Wait...");
        progressdialog.show();
        //This is the live api url in aarambh app for playstore
        //  String url = Global.WEBBASE_URL + "appLoginNew";

        //This is the api calling only for testing
        //  String url=Global.WEBBASE_URL+"appLoginNewTest";

        //This is the api calling only for Device
        String url = Global.WEBBASE_URL + "restaurant/GetRestaurantCompletedOrdersByDate";
        JSONObject params = new JSONObject();
//        JSONArray params=new JSONArray();
        String mRequestBody = "";

        try {
            params.put(("userId"), Integer.valueOf(SmartRestoSharedPreference.loadUserIdFromPreference(OrderReportActivity.this)));
          //Log.e("date_order", output_date_api);
            try {

                DateFormat inputFormat = new SimpleDateFormat("dd-mm-yyyy");
                DateFormat outputFormat = new SimpleDateFormat("yyyy-mm-dd");
                Date date = inputFormat.parse(date_geting);
                String outputDateStr = outputFormat.format(date);
                Log.e("OutputDate",outputDateStr);
                output_date_api = outputDateStr;

            }catch (Exception e){e.printStackTrace();}

            params.put(("OrderDate"), output_date_api);
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
               // progressdialog.dismiss();
                parseResponseOrderReport(response,progressdialog);
//                Toast.makeText(EditProfileActivity.this, String.valueOf(response), Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(EditProfileActivity.this, DashBoardActivity.class);
//                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OrderReportActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, myHeaders());

//        MyJsonArrayRequest request=new MyJsonArrayRequest(Request.Method.POST,url, params, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                String res=response.toString();
//                   parseResponseOrderReport(res,progressdialog);
//                Log.i("onResponse", ""+response.toString());
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                Log.i("onErrorResponse", "Error");
//            }
//        },myHeaders());
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json; charset=utf-8");
//                headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(OrderReportActivity.this));
//                return headers;
//            }
//
//            @Override
//            public Priority getPriority() {
//                return Priority.IMMEDIATE;
//            }
//
//            // Adding request to request queue
//            @Override
//            public byte[] getBody() {
//                try {
//                    return finalMRequestBody == null ? null : finalMRequestBody.getBytes("utf-8");
//                } catch (UnsupportedEncodingException uee) {
//                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", finalMRequestBody, "utf-8");
//                    return null;
//                }
//
//            }
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Global.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        stringRequest.setShouldCache(false);
        // Adding request to request queue
        VolleySingleton.getInstance(OrderReportActivity.this).addToRequestQueue(stringRequest);
    }

    private void parseResponseOrderReport(String response, ProgressDialog progressdialog) {
        try {
            if (response.equalsIgnoreCase("No data found")) {
                search_btn.setEnabled(true);
                search_btn.setClickable(true);
                progressdialog.dismiss();
                no_data_found.setVisibility(View.VISIBLE);
                orderReportAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            JSONArray jsonArray=new JSONArray(response);
            Log.e("array",jsonArray.length()+"");
            if (jsonArray.length()==0){
                no_data_found.setVisibility(View.VISIBLE);
            }else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    no_data_found.setVisibility(View.GONE);
                    Log.e("menu_length", String.valueOf(i));
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
                    String coupounId = null, customerId = null, isCreateAccount = null,
                            isDiffShippingAddr = null, shippingDetailsId = null, processingFee = null,
                            lstOrderDetails = null, billingDetails = null, shippingDetails = null;
                    try {
                        coupounId = jsonObject.getString("coupounId");
                        customerId = jsonObject.getString("customerId");
                        isCreateAccount = jsonObject.getString("isCreateAccount");
                        isDiffShippingAddr = jsonObject.getString("isDiffShippingAddr");
                        shippingDetailsId = jsonObject.getString("shippingDetailsId");
                        processingFee = jsonObject.getString("processingFee");
                        lstOrderDetails = jsonObject.getString("lstOrderDetails");

                        billingDetails = jsonObject.getString("billingDetails");
                        shippingDetails = jsonObject.getString("shippingDetails");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String customer = jsonObject.getString("customer");
//                JSONObject jsonObject_customer = new JSONObject(customer);
//                String customerId_cust=jsonObject_customer.getString("customerId");
//               Log.e("customer__id",customerId_cust);
//                String customerType_cust=jsonObject_customer.getString("customerType");
//                String firstName_cust=jsonObject_customer.getString("firstName");
//                String lastName_cust=jsonObject_customer.getString("lastName");
//                String companyName_cust=jsonObject_customer.getString("companyName");
//                String countryId_cust=jsonObject_customer.getString("countryId");
//                String address_cust=jsonObject_customer.getString("address");
//                String addressLine2_cust=jsonObject_customer.getString("addressLine2");
//                String cityId_cust=jsonObject_customer.getString("cityId");
//                String stateId_cust=jsonObject_customer.getString("stateId");
//
//                String postCode_cust=jsonObject_customer.getString("postCode");
//                String mobile_cust=jsonObject_customer.getString("mobile");
//                String userName_cust=jsonObject_customer.getString("userName");
//                String isActive_cust=jsonObject_customer.getString("isActive");
//                String deliveryFee_cust=jsonObject_customer.getString("deliveryFee");
//

                    String tipAmount = jsonObject.getString("tipAmount");
                    String deliveryType=jsonObject.getString("deliveryType");
                    String paymentType=jsonObject.getString("paymentType");
                    OrderReportModel orm = new OrderReportModel(orderId, restaurantId, subTotal, tax, deliveryFee, total, orderDate, deliveryTypeId, orderStatusId,
                            billingDetailsId, additionalInformation, coupounId, customerId, isCreateAccount, isDiffShippingAddr,
                            shippingDetailsId, processingFee, lstOrderDetails, billingDetails, shippingDetails, customer, tipAmount,deliveryType,paymentType);
                    orderReportModelArrayList.add(orm);
                }
            }
            try {
                orderReportAdapter = new OrderReportAdapter(OrderReportActivity.this,orderReportModelArrayList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
                order_report_recyclerview.setLayoutManager(linearLayoutManager);
                order_report_recyclerview.setAdapter(orderReportAdapter);
                orderReportAdapter.notifyDataSetChanged();
            }catch (Exception e){
                e.printStackTrace();
            }
            search_btn.setClickable(true);
            search_btn.setEnabled(true);
            progressdialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {

    }
}