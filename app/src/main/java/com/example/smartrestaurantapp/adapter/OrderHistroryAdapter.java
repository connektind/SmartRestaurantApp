package com.example.smartrestaurantapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
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
import com.example.smartrestaurantapp.model.OrderDetailModel;
import com.example.smartrestaurantapp.model.OrderHistroryModel;
import com.example.smartrestaurantapp.sharedPrefrence.SmartRestoSharedPreference;
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

public class OrderHistroryAdapter extends RecyclerView.Adapter<OrderHistroryAdapter.MyViewHolder> {
    Context context;
    ArrayList<OrderHistroryModel> orderHistroryModelArrayList;
    public OrderHistroryAdapter(Context context, ArrayList<OrderHistroryModel> orderHistroryModelArrayList) {
    this.context=context;
    this.orderHistroryModelArrayList=orderHistroryModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.order_history, parent, false);
        return new OrderHistroryAdapter.MyViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OrderHistroryModel ohm=orderHistroryModelArrayList.get(position);
        holder.order_id.setText(ohm.getOrderId());
        holder.getItemDataCallApi(ohm.getOrderId());
        String assign_date=ohm.getOrderDate();
        Log.e("date_order",ohm.getCustomer());
        JSONObject jsonObject_customer = null;
        try {
            jsonObject_customer = new JSONObject(ohm.getCustomer());
            String customerId_cust=jsonObject_customer.getString("customerId");
            Log.e("customer__id",customerId_cust);
            String customerType_cust=jsonObject_customer.getString("customerType");
            String firstName_cust=jsonObject_customer.getString("firstName");
            holder.customer_name.setText(firstName_cust+"");
            String lastName_cust=jsonObject_customer.getString("lastName");
            String companyName_cust=jsonObject_customer.getString("companyName");
            String countryId_cust=jsonObject_customer.getString("countryId");
            String address_cust=jsonObject_customer.getString("address");
            String addressLine2_cust=jsonObject_customer.getString("addressLine2");
            String cityId_cust=jsonObject_customer.getString("cityId");
            String stateId_cust=jsonObject_customer.getString("stateId");

            String postCode_cust=jsonObject_customer.getString("postCode");
            String mobile_cust=jsonObject_customer.getString("mobile");
            String userName_cust=jsonObject_customer.getString("userName");
            String isActive_cust=jsonObject_customer.getString("isActive");
            String deliveryFee_cust=jsonObject_customer.getString("deliveryFee");


        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            DateFormat inputFormat = new SimpleDateFormat("yyyy-mm-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd-mm-yyyy");
            Date date = inputFormat.parse(assign_date.substring(0,10));

            String outputDateStr = outputFormat.format(date);
            Log.e("OutputDate",outputDateStr);
            String output = outputDateStr;
            holder.order_date.setText(output);
        }catch (Exception e){e.printStackTrace();}
          if (ohm.getOrder_position()==1){
              holder.order_status.setText("Pending");
              holder.order_status.setTextColor(holder.order_status.getResources().getColor(R.color.orange));
             // holder.order_status.setTextColor(()R.color.orange);
          }else if (ohm.getOrder_position()==2){
              holder.order_status.setText("In-Progress");
              holder.order_status.setTextColor(holder.order_status.getResources().getColor(R.color.blue));
          }else if (ohm.getOrder_position()==3){
              holder.order_status.setText("Completed");
              holder.order_status.setTextColor(holder.order_status.getResources().getColor(R.color.green));

          }else if (ohm.getOrder_position()==4){
              holder.order_status.setText("Cancelled");
              holder.order_status.setTextColor(holder.order_status.getResources().getColor(R.color.red));

          }else {
              holder.order_status.setText("Pending");
              holder.order_status.setTextColor(holder.order_status.getResources().getColor(R.color.orange));

          }
          holder.delivery_type.setText(ohm.getDeliveryType());


    }

    @Override
    public int getItemCount() {
        return orderHistroryModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView order_date,order_id,customer_name,order_item,delivery_type,order_status;
        RecyclerView recycler_productname;
        ArrayList<OrderDetailModel> orderDetailModelArrayList;
        OrderDetailAdapter orderDetailAdapter;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDetailModelArrayList=new ArrayList<>();
            order_date=itemView.findViewById(R.id.order_date);
            order_id=itemView.findViewById(R.id.order_id);
            customer_name=itemView.findViewById(R.id.customer_name);
            delivery_type=itemView.findViewById(R.id.delivery_type);
            order_status=itemView.findViewById(R.id.order_status);
            recycler_productname=itemView.findViewById(R.id.recycler_productname);
        }

        public void getItemDataCallApi(String orderId) {
            if (!CommonUtilities.isOnline(context)) {

                Toast.makeText(context, "Please On Your Internet Connection", Toast.LENGTH_LONG).show();
                return;

            }
//            progressdialog = new ProgressDialog(context);
//            progressdialog.setCancelable(false);
//            progressdialog.setMessage("Loading...");
//            progressdialog.setTitle("Wait...");
//            progressdialog.show();
            final String string_json = "Result";
            String user_id=SmartRestoSharedPreference.loadUserIdFromPreference(context);

            String url = Global.WEBBASE_URL + "restaurant/GetOrderDetailsByOrderId/"+user_id+"/"+orderId;
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
                    headers.put("Authorization", SmartRestoSharedPreference.loadUserTokenFromPreference(context));
                    return headers;
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(stringRequest, string_json);
        }

        private void parseResponseAvailableOrderData(String response) {
            Log.e("Response", response);
            orderDetailModelArrayList.clear();
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
                orderDetailModelArrayList.clear();
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
                    orderDetailAdapter = new OrderDetailAdapter(context, orderDetailModelArrayList);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
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
             //   progressdialog.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }
}
