package com.example.smartrestaurantapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurantapp.R;
import com.example.smartrestaurantapp.model.OrderProductSubModifierModel;

import org.w3c.dom.Text;

import java.util.ArrayList;

public  class OrderProductSubModifierAdapter extends RecyclerView.Adapter<OrderProductSubModifierAdapter.MyViewHolder> {
    Context context;
    ArrayList<OrderProductSubModifierModel> orderProductSubModifierModelArrayList;
    public OrderProductSubModifierAdapter(Context context, ArrayList<OrderProductSubModifierModel> orderProductSubModifierModelArrayList) {
        this.context=context;
        this.orderProductSubModifierModelArrayList=orderProductSubModifierModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.extra_sub_item, parent, false);
        return new OrderProductSubModifierAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OrderProductSubModifierModel ordms=orderProductSubModifierModelArrayList.get(position);
        holder.extra_sub_item.setText(" + "+ordms.getModifierOptionName_sub());
        holder.extra_item_sub_price.setText(ordms.getPrice_sub());

    }

    @Override
    public int getItemCount() {
        return orderProductSubModifierModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView extra_item_sub_price,extra_sub_item;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            extra_item_sub_price=itemView.findViewById(R.id.extra_item_sub_price);
            extra_sub_item=itemView.findViewById(R.id.extra_sub_item);
        }
    }
}
