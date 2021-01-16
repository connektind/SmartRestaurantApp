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
import com.example.smartrestaurantapp.adapter.AvailableOrderAdapter;
import com.example.smartrestaurantapp.adapter.OrderStatusAdapter;
import com.example.smartrestaurantapp.model.AvailableOrderModel;
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

public class AvailableOrderActivity extends AppCompatActivity {
       RecyclerView available_order_recycler;
       ArrayList<AvailableOrderModel>availableOrderModelArrayList;
       AvailableOrderAdapter availableOrderAdapter;
       int order_position;
       ImageView back_btn;
       ProgressDialog progressdialog;
       EditText avail_order_select;
    ArrayList<OrderStatusModel> orderStatusModelArrayList;
    OrderStatusAdapter orderStatusAdapter;
    String order_position_selection;
    TextView tv_other;
    Button search_order;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_order);
        init();
        listner();
        user_id=SmartRestoSharedPreference.loadUserIdFromPreference(this);
        checkOrientation();


    }
    public void getAvailableOrderData(String url_order)
    {
        if (!CommonUtilities.isOnline(this)) {

            Toast.makeText(AvailableOrderActivity.this, "Please On Your Internet Connection", Toast.LENGTH_LONG).show();
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
                parseResponseAvailableOrderData(res);
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
                        parseResponseAvailableOrderData(response.toString());
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
                headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(AvailableOrderActivity.this));
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Global.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(AvailableOrderActivity.this).addToRequestQueue(stringRequest, string_json);
    }

    private void parseResponseAvailableOrderData(String response) {
        Log.e("Response", response);
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

                String tipAmount = jsonObject.getString("tipAmount");
                String deliveryType=jsonObject.getString("deliveryType");
                String paymentType=jsonObject.getString("paymentType");

                AvailableOrderModel avom = new AvailableOrderModel(orderId,restaurantId,subTotal,tax,deliveryFee,total,orderDate,deliveryTypeId,orderStatusId,
                        billingDetailsId,additionalInformation,coupounId,customerId,isCreateAccount,isDiffShippingAddr,
                        shippingDetailsId,processingFee,lstOrderDetails,billingDetails,shippingDetails,customer,tipAmount,deliveryType,paymentType);
                availableOrderModelArrayList.add(avom);
            }

                    try {
                        availableOrderAdapter = new AvailableOrderAdapter(AvailableOrderActivity.this,availableOrderModelArrayList);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
                        available_order_recycler.setLayoutManager(linearLayoutManager);
                        available_order_recycler.setAdapter(availableOrderAdapter);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
            search_order.setEnabled(true);

            progressdialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void checkOrientation() {
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getAvailableOrderData("Restaurant/GetRestaurantPendingOrders/"+user_id);
        }
        if (getResources().getBoolean(R.bool.landscape_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getAvailableOrderData("Restaurant/GetRestaurantPendingOrders/"+user_id);
        }
    }
    private void listner() {
        search_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (order_position){
                    case 1:
                        availableOrderModelArrayList.clear();
                        search_order.setEnabled(false);
                        getAvailableOrderData("Restaurant/GetRestaurantPendingOrders/"+user_id);
                        break;
                    case 2:
                        availableOrderModelArrayList.clear();
                        search_order.setEnabled(false);
                        getAvailableOrderData("restaurant/GetRestaurantInProgressOrders/"+user_id);
                        break;
                    case 3:
                        availableOrderModelArrayList.clear();
                        search_order.setEnabled(false);

                        getAvailableOrderData("restaurant/GetRestaurantCompletedOrders/"+user_id);
                        break;
                    case 4:
                        availableOrderModelArrayList.clear();
                        search_order.setEnabled(false);

                        getAvailableOrderData("restaurant/GetRestaurantCancelOrders/"+user_id);
                        break;
                    default:
                        availableOrderModelArrayList.clear();
                        search_order.setEnabled(false);

                        getAvailableOrderData("Restaurant/GetRestaurantPendingOrders/"+user_id);
                        break;
                }
            }
        });


       back_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(AvailableOrderActivity.this,DashBoardActivity.class);
               startActivity(intent);
           }
       });
        avail_order_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 getOrderstatusSelection();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AvailableOrderActivity.this,DashBoardActivity.class);
        startActivity(intent);

    }

    private void init() {
        availableOrderModelArrayList=new ArrayList<>();
        orderStatusModelArrayList=new ArrayList<>();
        available_order_recycler=(RecyclerView) findViewById(R.id.available_order_recycler);
        back_btn=(ImageView)findViewById(R.id.back_btn_availorder);
        avail_order_select=findViewById(R.id.avail_order_select);
        search_order=findViewById(R.id.search_order);
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
                avail_order_select.setError(null);
                avail_order_select.clearFocus();
            }
        });
        lv_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.cancel();
                OrderStatusModel dd = orderStatusModelArrayList.get(position);
                order_position_selection = dd.getOrderStatusId();
                order_position= Integer.parseInt(order_position_selection);
                avail_order_select.setError(null);
                avail_order_select.clearFocus();
                Log.e("Name  & Id", dd.getOrderStatus() + ":" + dd.getOrderStatusId());
                avail_order_select.setText(dd.getOrderStatus());


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
                headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(AvailableOrderActivity.this));

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