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
import android.widget.Spinner;
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
import com.example.smartrestaurantapp.activitys.bluethoodPrinter.BTPrinterActivity;
import com.example.smartrestaurantapp.activitys.bluethoodPrinter.BillingPDFCreateActivity;
import com.example.smartrestaurantapp.activitys.bluethoodPrinter.BluetoothPrinterActivity;
import com.example.smartrestaurantapp.activitys.bluethoodPrinter.NewBluethooth.BlueThoothDeviceListActivity;
import com.example.smartrestaurantapp.adapter.OrderDetailAdapter;
import com.example.smartrestaurantapp.adapter.OrderStatusAdapter;
import com.example.smartrestaurantapp.model.AvailableOrderModel;
import com.example.smartrestaurantapp.model.OrderDetailModel;
import com.example.smartrestaurantapp.model.OrderStatusModel;
import com.example.smartrestaurantapp.sharedPrefrence.SmartRestoSharedPreference;
import com.example.smartrestaurantapp.vollyRequest.BooleanRequest;
import com.example.smartrestaurantapp.utils.CommonUtilities;
import com.example.smartrestaurantapp.utils.Global;
import com.example.smartrestaurantapp.vollyRequest.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailActivity extends AppCompatActivity {
    RecyclerView recycler_productname;
    ArrayList<OrderDetailModel> orderDetailModelArrayList;
    OrderDetailAdapter orderDetailAdapter;
    ImageView back_btn_orderdetail;
    Button chnge_order;
    AvailableOrderModel ords;
    Spinner order_complete_select;
    String order_position;
    EditText order_select;
    ProgressDialog progressdialog;
    ArrayList<OrderStatusModel> orderStatusModelArrayList;
    OrderStatusAdapter orderStatusAdapter;
    String user_id;
    TextView tv_other, order_date, order_id, customer_name, item_name, delivery_type,printing_bill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        user_id=SmartRestoSharedPreference.loadUserIdFromPreference(this);
        ords = (AvailableOrderModel) getIntent().getSerializableExtra("available_orderData");
        try {
            Log.e("availa", ords.getOrderId() + "");
        }catch (Exception e){
            e.printStackTrace();
        }
        init();
        listner();
        checkOrientation();
    }

    private void listner() {
        printing_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(OrderDetailActivity.this, BTPrinterActivity.class);
                intent.putExtra("order_id",ords.getOrderId());
                startActivity(intent);
            }
        });
        back_btn_orderdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, AvailableOrderActivity.class);
                startActivity(intent);
            }
        });
        chnge_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status=order_select.getText().toString().trim();
                if (status.isEmpty()){
                    order_select.setError("Please Select Order Status");
                    order_select.requestFocus();
                }else {
                    chnge_order.setEnabled(false);
                    updateOrderStatus();
                }

            }
        });

    }

    public Map<String, String> myHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(OrderDetailActivity.this));
        return headers;
    }

    Map<String, String> headers = myHeaders();

    private void updateOrderStatus() {
        if (!CommonUtilities.isOnline(OrderDetailActivity.this)) {
            Toast.makeText(OrderDetailActivity.this, "Please On Your Internet Connection", Toast.LENGTH_LONG).show();
            return;
        }
        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(false);
        progressdialog.setMessage("Loading...");
        progressdialog.setTitle("Wait...");
        progressdialog.show();
        String url = Global.WEBBASE_URL + "restaurant/UpdateOrderStatusByRestaurant";
        JSONObject params = new JSONObject();
        String mRequestBody = "";
        try {
            params.put("orderId", Integer.valueOf(ords.getOrderId()));
            Log.e("order_id", ords.getOrderId());
            params.put("OrderStatusId", Integer.valueOf(order_position));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRequestBody = params.toString();
        Log.e("Request Body", mRequestBody);
        final String finalMRequestBody = mRequestBody;
        BooleanRequest booleanRequest = new BooleanRequest(1, url, mRequestBody, new Response.Listener<Boolean>() {
            @Override
            public void onResponse(Boolean response) {
                Log.e("res", response.toString());
                parseResponseLoginUser(response.toString(), progressdialog);
                //  Toast.makeText(OrderDetailActivity.this, String.valueOf(response), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OrderDetailActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }, myHeaders());

        booleanRequest.setRetryPolicy(new DefaultRetryPolicy(
                Global.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        // Add the request to the RequestQueue.
        // queue.add(booleanRequest);
        VolleySingleton.getInstance(OrderDetailActivity.this).addToRequestQueue(booleanRequest);
    }

    private void parseResponseLoginUser(String response, ProgressDialog progressdialog) {
        try {
            //getting the whole json object from the response 4878704040
            //  JSONObject obj = new JSONObject(response);
            Log.e("order_detail", String.valueOf(response));
            progressdialog.dismiss();
            Toast.makeText(OrderDetailActivity.this, "Saved Detail's", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(OrderDetailActivity.this, DashBoardActivity.class);
            startActivity(intent);
            chnge_order.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(OrderDetailActivity.this, AvailableOrderActivity.class);
        startActivity(intent);
    }

    private void checkOrientation() {
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getOrderDeatailData();
        }
        if (getResources().getBoolean(R.bool.landscape_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getOrderDeatailData();
        }
    }
    private void init() {
        orderDetailModelArrayList = new ArrayList<>();
        recycler_productname = findViewById(R.id.recycler_productname);
        back_btn_orderdetail = findViewById(R.id.back_btn_orderdetail);
        printing_bill=findViewById(R.id.printing_bill);
        chnge_order = findViewById(R.id.chnge_order);
        order_date = findViewById(R.id.order_date);
        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-mm-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd-mm-yyyy");
            Date date = inputFormat.parse(ords.getOrderDate().substring(0, 16));
            String outputDateStr = outputFormat.format(date);
            Log.e("OutputDate", outputDateStr);
            String output = outputDateStr;
            order_date.setText(output + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        order_id = findViewById(R.id.order_id);
        order_id.setText(ords.getOrderId());
        customer_name = findViewById(R.id.customer_name);
        JSONObject jsonObject_customer = null;
        try {
            jsonObject_customer = new JSONObject(ords.getCustomer());
            String customerId_cust = jsonObject_customer.getString("customerId");
            Log.e("customer__id", customerId_cust);
            String customerType_cust = jsonObject_customer.getString("customerType");
            String firstName_cust = jsonObject_customer.getString("firstName");
            customer_name.setText(firstName_cust + "");
            String lastName_cust = jsonObject_customer.getString("lastName");
            String companyName_cust = jsonObject_customer.getString("companyName");
            String countryId_cust = jsonObject_customer.getString("countryId");
            String address_cust = jsonObject_customer.getString("address");
            String addressLine2_cust = jsonObject_customer.getString("addressLine2");
            String cityId_cust = jsonObject_customer.getString("cityId");
            String stateId_cust = jsonObject_customer.getString("stateId");
            String postCode_cust = jsonObject_customer.getString("postCode");
            String mobile_cust = jsonObject_customer.getString("mobile");
            String userName_cust = jsonObject_customer.getString("userName");
            String isActive_cust = jsonObject_customer.getString("isActive");
            String deliveryFee_cust = jsonObject_customer.getString("deliveryFee");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        item_name = findViewById(R.id.item_name);
        // item_name.setText(ords.get);
        delivery_type = findViewById(R.id.delivery_type);
        delivery_type.setText(ords.getDeliveryType());
        order_complete_select = findViewById(R.id.order_complete_select);
        order_select = findViewById(R.id.order_select);
        orderStatusModelArrayList = new ArrayList<>();
        order_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOrderstatusSelection();
                order_select.setSelected(true);
            }
        });
    }

    public void getOrderDeatailData() {
        if (!CommonUtilities.isOnline(this)) {
            Toast.makeText(OrderDetailActivity.this, "Please On Your Internet Connection", Toast.LENGTH_LONG).show();
            return;
        }
        progressdialog = new ProgressDialog(this);
        progressdialog.setCancelable(false);
        progressdialog.setMessage("Loading...");
        progressdialog.setTitle("Wait...");
        progressdialog.show();
        final String string_json = "Result";

        String url = Global.WEBBASE_URL + "restaurant/GetOrderDetailsByOrderId/"+user_id+"/"+ords.getOrderId();
        //}
        Log.e("url", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
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
                headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(OrderDetailActivity.this));
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Global.WEBSERVICE_TIMEOUT_VALUE_IN_MILLIS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
        stringRequest.setShouldCache(false);
        VolleySingleton.getInstance(OrderDetailActivity.this).addToRequestQueue(stringRequest, string_json);
    }

    private void parseResponseAvailableOrderData(String response) {
        Log.e("Response", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            // Log.e("menu_length", String.valueOf(i));
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            lstOrderDetails = jsonObject.getString("lstOrderDetails");
            JSONArray jsonArray_productName = new JSONArray(lstOrderDetails);
            Log.e("array_productName", jsonArray_productName.length() + "");
            for (int j = 0; j < jsonArray_productName.length(); j++) {
                JSONObject jsonObject_productName = jsonArray_productName.getJSONObject(j);

                String orderDetailsId = jsonObject_productName.getString("orderDetailsId");
                String orderId_product = jsonObject_productName.getString("orderId");
                // customer_name.setText(firstName_cust+"");
                String productId = jsonObject_productName.getString("productId");
                String productQuantity = jsonObject_productName.getString("productQuantity");
                String modifierId = jsonObject_productName.getString("modifierId");
                String modifierQuantity = jsonObject_productName.getString("modifierQuantity");
                String price = jsonObject_productName.getString("price");
                String isModifierType = jsonObject_productName.getString("isModifierType");
                String modifierTypeId = jsonObject_productName.getString("modifierTypeId");

                String lstModiferOptions = jsonObject_productName.getString("lstModiferOptions");
                String productName = jsonObject_productName.getString("productName");
                String additionalInformation_product = jsonObject_productName.getString("additionalInformation");
                String lstModifier = jsonObject_productName.getString("lstModifier");

                OrderDetailModel avom = new OrderDetailModel(orderDetailsId, orderId_product, productId, productQuantity, modifierId, modifierQuantity,
                        price, isModifierType, modifierTypeId, lstModiferOptions, productName, additionalInformation_product, lstModifier);
                orderDetailModelArrayList.add(avom);
            }

                try {
                    orderDetailAdapter = new OrderDetailAdapter(OrderDetailActivity.this, orderDetailModelArrayList);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
                    recycler_productname.setLayoutManager(linearLayoutManager);
                    recycler_productname.setAdapter(orderDetailAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            try {
                billingDetails = jsonObject.getString("billingDetails");
                shippingDetails = jsonObject.getString("shippingDetails");

            } catch (Exception e) {
                e.printStackTrace();
            }
            String customer = jsonObject.getString("customer");
            String tipAmount = jsonObject.getString("tipAmount");
            String deliveryType = jsonObject.getString("deliveryType");
            String paymentType = jsonObject.getString("paymentType");

//                AvailableOrderModel avom = new AvailableOrderModel(orderId,restaurantId,subTotal,tax,deliveryFee,total,orderDate,deliveryTypeId,orderStatusId,
//                        billingDetailsId,additionalInformation,coupounId,customerId,isCreateAccount,isDiffShippingAddr,
//                        shippingDetailsId,processingFee,lstOrderDetails,billingDetails,shippingDetails,customer,tipAmount,deliveryType,paymentType);
//                availableOrderModelArrayList.add(avom);

//            try {
//                availableOrderAdapter = new AvailableOrderAdapter(AvailableOrderActivity.this,availableOrderModelArrayList);
//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
//                available_order_recycler.setLayoutManager(linearLayoutManager);
//                available_order_recycler.setAdapter(availableOrderAdapter);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            progressdialog.dismiss();

        } catch (Exception e) {
            e.printStackTrace();
        }

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
                order_select.setError(null);
                order_select.clearFocus();
            }
        });
        lv_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.cancel();
                OrderStatusModel dd = orderStatusModelArrayList.get(position);
                order_position = dd.getOrderStatusId();
                order_select.setError(null);
                order_select.clearFocus();
                Log.e("Name  & Id", dd.getOrderStatus() + ":" + dd.getOrderStatusId());
                order_select.setText(dd.getOrderStatus());


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
                headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(OrderDetailActivity.this));

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
            orderDetailModelArrayList.clear();

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
            orderStatusAdapter.notifyDataSetChanged();
            order_select.setSelected(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}