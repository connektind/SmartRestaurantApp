package com.example.smartrestaurantapp.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.example.smartrestaurantapp.adapter.OrderHistroryAdapter;
import com.example.smartrestaurantapp.adapter.OrderStatusAdapter;
import com.example.smartrestaurantapp.model.OrderHistroryModel;
import com.example.smartrestaurantapp.model.OrderStatusModel;
import com.example.smartrestaurantapp.sharedPrefrence.SmartRestoSharedPreference;
import com.example.smartrestaurantapp.utils.CommonUtilities;
import com.example.smartrestaurantapp.utils.Global;
import com.example.smartrestaurantapp.vollyRequest.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderHistroryActivity extends AppCompatActivity {
    ArrayList<OrderHistroryModel>orderHistroryModelArrayList;
    OrderHistroryAdapter orderHistroryAdapter;
    RecyclerView order_history_recycler;
    String order_history_position;
    ImageView back_btn_orderhistory;
    EditText history_order_select;
    ArrayList<OrderStatusModel> orderStatusModelArrayList;
    OrderStatusAdapter orderStatusAdapter;
    TextView tv_other;
    Button search_order;
    ProgressDialog progressdialog;
    int order_position;
    TextView no_data_found;
    String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_histrory);
        user_id=SmartRestoSharedPreference.loadUserIdFromPreference(this);
        init();
        listner();
        checkOrientation();
//        getHistoryOrderData("Restaurant/GetRestaurantPendingOrders/"+user_id,1);

    }
    public void getHistoryOrderData(String url_order, int order_position)
    {
        if (!CommonUtilities.isOnline(this)) {

            Toast.makeText(OrderHistroryActivity.this, "Please On Your Internet Connection", Toast.LENGTH_LONG).show();
            return;
        }
        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(false);
        progressdialog.setMessage("Loading...");
        progressdialog.setTitle("Wait...");
        progressdialog.show();
        final String string_json = "Result";

        String   url = Global.WEBBASE_URL +url_order;
        //}
        Log.e("url",url);
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String res = response.toString();
                parseResponseAvailableOrderData(res,order_position);
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
                        parseResponseAvailableOrderData(response.toString(), order_position);
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
                headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(OrderHistroryActivity.this));
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Global.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(OrderHistroryActivity.this).addToRequestQueue(stringRequest, string_json);
    }

    private void parseResponseAvailableOrderData(String response, int order_position) {
        Log.e("Response", response);
        try {
            if (response.equalsIgnoreCase("No data found")) {
                search_order.setEnabled(true);
                search_order.setClickable(true);
                progressdialog.dismiss();
                no_data_found.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            JSONArray jsonArray=new JSONArray(response);

            Log.e("array",jsonArray.length()+"");
            for (int i = 0; i < jsonArray.length(); i++) {
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
                String coupounId = null,customerId = null,isCreateAccount=null,
                        isDiffShippingAddr=null,shippingDetailsId=null,processingFee=null,
                        lstOrderDetails=null,billingDetails=null,shippingDetails=null;
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

                }catch (Exception e){
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
                OrderHistroryModel orderHistroryModel = new OrderHistroryModel(orderId,restaurantId,subTotal,tax,deliveryFee,total,orderDate,deliveryTypeId,orderStatusId,
                        billingDetailsId,additionalInformation,coupounId,customerId,isCreateAccount,isDiffShippingAddr,
                        shippingDetailsId,processingFee,lstOrderDetails,billingDetails,shippingDetails,customer,tipAmount,deliveryType,paymentType,order_position);
                orderHistroryModelArrayList.add(orderHistroryModel);
            }

            try {
                orderHistroryAdapter = new OrderHistroryAdapter(OrderHistroryActivity.this,orderHistroryModelArrayList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
                order_history_recycler.setLayoutManager(linearLayoutManager);
                order_history_recycler.setAdapter(orderHistroryAdapter);
            }catch (Exception e){
                e.printStackTrace();
            }
            search_order.setEnabled(true);
            search_order.setClickable(true);
            progressdialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private void checkOrientation() {
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getHistoryOrderData("Restaurant/GetRestaurantPendingOrders/"+user_id,1);
        }
        if (getResources().getBoolean(R.bool.landscape_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getHistoryOrderData("Restaurant/GetRestaurantPendingOrders/"+user_id,1);

        }
    }

    private void listner() {
        search_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (order_position){
                    case 1:
                        search_order.setClickable(false);
                        search_order.setEnabled(false);
                        orderHistroryModelArrayList.clear();
                        getHistoryOrderData("Restaurant/GetRestaurantPendingOrders/"+user_id,order_position);
                        break;
                    case 2:
                        search_order.setClickable(false);
                        search_order.setEnabled(false);
                        orderHistroryModelArrayList.clear();
                        getHistoryOrderData("restaurant/GetRestaurantInProgressOrders/"+user_id, order_position);
                        break;
                    case 3:
                        search_order.setClickable(false);
                        search_order.setEnabled(false);
                        orderHistroryModelArrayList.clear();
                        getHistoryOrderData("restaurant/GetRestaurantCompletedOrders/"+user_id, order_position);
                        break;
                    case 4:
                        search_order.setClickable(false);
                        search_order.setEnabled(false);
                        orderHistroryModelArrayList.clear();
                        getHistoryOrderData("restaurant/GetRestaurantCancelOrders/"+user_id, order_position);
                        break;
                    default:
                        search_order.setClickable(false);
                        search_order.setEnabled(false);
                        orderHistroryModelArrayList.clear();
                        getHistoryOrderData("Restaurant/GetRestaurantPendingOrders/"+user_id, order_position);
                        break;
                }

            }
        });

        back_btn_orderhistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OrderHistroryActivity.this,DashBoardActivity.class);
                startActivity(intent);
            }
        });
        history_order_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOrderstatusSelection();
            }
        });

    }

    private void init() {
        orderHistroryModelArrayList=new ArrayList<>();
        orderStatusModelArrayList=new ArrayList<>();
        order_history_recycler=findViewById(R.id.order_history_recycler);
        back_btn_orderhistory=findViewById(R.id.back_btn_orderhistory);
        history_order_select=findViewById(R.id.history_order_select);
        search_order=findViewById(R.id.search_order);
        no_data_found=findViewById(R.id.no_data_found);


    }
    private void getOrderstatusSelection() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.searchable_list_dialog);
        final ListView lv_listView = (ListView) dialog.findViewById(R.id.listItems);
        // final EditText et_search = (EditText) dialog.findViewById(R.id.search);
        TextView close = (TextView) dialog.findViewById(R.id.close);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.pr_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        tv_other = (TextView) dialog.findViewById(R.id.tv_other);
        tv_other.setVisibility(View.GONE);
        orderStatusModelArrayList.clear();
        getOrderStatusApiCalling(lv_listView, progressBar);
        tv_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   class_select.setText(et_search.getText().toString().trim());
                dialog.dismiss();
                history_order_select.setError(null);
                history_order_select.clearFocus();
            }
        });
        lv_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.cancel();
                OrderStatusModel dd = orderStatusModelArrayList.get(position);
                order_history_position = dd.getOrderStatusId();
                order_position= Integer.parseInt(order_history_position);
                history_order_select.setError(null);
                history_order_select.clearFocus();
                Log.e("Name  & Id", dd.getOrderStatus() + ":" + dd.getOrderStatusId());
                history_order_select.setText(dd.getOrderStatus());


            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void getOrderStatusApiCalling(final ListView listViewdata, final ProgressBar progressBar) {
        if (!CommonUtilities.isOnline(this)) {

            Toast.makeText(this, "Please On Your Internet Connection", Toast.LENGTH_LONG).show();
            return;

        }
        String url = Global.WEBBASE_URL + "Restaurant/GetOrderStatus";
        final String string_json = "Result";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String res = response.toString();
                parseResponseOrderStatus(res, listViewdata, progressBar);
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
                        parseResponseOrderStatus(response.toString(), listViewdata, progressBar);

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
                headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(OrderHistroryActivity.this));

                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest, string_json);

    }

    private void parseResponseOrderStatus(String response, ListView listViewdata, ProgressBar progressBar) {
        Log.e("class_res", response);
        try {
            JSONArray jsonArray = new JSONArray(response);

            Log.e("array", jsonArray.length() + "");

            for (int i = 0; i < jsonArray.length(); i++) {
                Log.e("class_lenth", String.valueOf(i));
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String orderStatusId = jsonObject.getString("orderStatusId");
                String orderStatus = jsonObject.getString("orderStatus");
                OrderStatusModel ods = new OrderStatusModel(orderStatusId, orderStatus);
                orderStatusModelArrayList.add(ods);
            }
            orderStatusAdapter = new OrderStatusAdapter(this, orderStatusModelArrayList);
            listViewdata.setAdapter(orderStatusAdapter);
            progressBar.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}