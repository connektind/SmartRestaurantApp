package com.example.smartrestaurantapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurantapp.R;
import com.example.smartrestaurantapp.model.OrderDetailModel;

import java.util.ArrayList;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.MyViewHolder> {
    Context context;
    ArrayList<OrderDetailModel> orderDetailModelArrayList;
    public OrderDetailAdapter(Context context, ArrayList<OrderDetailModel> orderDetailModelArrayList) {
   this.context=context;
   this.orderDetailModelArrayList=orderDetailModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.order_detail, parent, false);
        return new OrderDetailAdapter.MyViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OrderDetailModel ordm=orderDetailModelArrayList.get(position);
        holder.item_name.setText(ordm.getProductName());
        holder.quent.setText(ordm.getProductQuantity());


    }

    @Override
    public int getItemCount() {
        return orderDetailModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
       TextView quent,item_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            quent = itemView.findViewById(R.id.questity);
            item_name = itemView.findViewById(R.id.item_name);


        }


    };
}
