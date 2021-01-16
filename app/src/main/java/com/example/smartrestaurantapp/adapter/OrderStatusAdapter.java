package com.example.smartrestaurantapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.smartrestaurantapp.R;
import com.example.smartrestaurantapp.model.OrderStatusModel;

import java.util.ArrayList;

public class OrderStatusAdapter extends BaseAdapter {
       Context context;
    ArrayList<OrderStatusModel> orderStatusModelArrayList;
    public OrderStatusAdapter(Context context, ArrayList<OrderStatusModel> orderStatusModelArrayList) {
        this.context=context;
        this.orderStatusModelArrayList=orderStatusModelArrayList;
        notifyDataSetChanged(); // Fix
    }

    @Override
    public int getCount() {
        return orderStatusModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderStatusModelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderStatusModel ods = orderStatusModelArrayList.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.search_list, null);
        TextView tv_name = ((TextView) view.findViewById(R.id.tv_name));
        tv_name.setText(ods.getOrderStatus());
        return view;
    }
}
