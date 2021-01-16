package com.example.smartrestaurantapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurantapp.R;
import com.example.smartrestaurantapp.activitys.OrderReportActivity;
import com.example.smartrestaurantapp.activitys.bluethoodPrinter.BTPrinterActivity;
import com.example.smartrestaurantapp.model.AvailableOrderModel;
import com.example.smartrestaurantapp.model.OrderReportModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderReportAdapter extends RecyclerView.Adapter<OrderReportAdapter.MyViewHolder> {
    Context context;
    ArrayList<OrderReportModel> orderReportModelArrayList;
    public OrderReportAdapter(Context context, ArrayList<OrderReportModel> orderReportModelArrayList) {
    this.context=context;
    this.orderReportModelArrayList=orderReportModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.order_report, parent, false);
        return new OrderReportAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OrderReportModel ordm=orderReportModelArrayList.get(position);
        String assign_date=ordm.getOrderDate();
        Log.e("date_order",ordm.getCustomer());
        JSONObject jsonObject_customer = null;
        try {
            jsonObject_customer = new JSONObject(ordm.getCustomer());
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
            holder.date.setText(output);
        }catch (Exception e){e.printStackTrace();}
        holder.total_ammount.setText(ordm.getTotal());
         holder.delivery_type.setText(ordm.getDeliveryType());
         holder.print_order.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(context, BTPrinterActivity.class);
                 intent.putExtra("order_id",ordm.getOrderId());
                 context.startActivity(intent);
             }
         });
    }

    @Override
    public int getItemCount() {
        return orderReportModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date,customer_name,delivery_type,total_ammount,print_order;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            customer_name=itemView.findViewById(R.id.customer);
            delivery_type=itemView.findViewById(R.id.delivery_type);
            total_ammount=itemView.findViewById(R.id.total_ammount);
            print_order=itemView.findViewById(R.id.print_order);
        }
    }
}
