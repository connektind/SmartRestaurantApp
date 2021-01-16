package com.example.smartrestaurantapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurantapp.R;
import com.example.smartrestaurantapp.activitys.AvailableOrderActivity;
import com.example.smartrestaurantapp.model.AvailableOrderModel;
import com.example.smartrestaurantapp.model.DashboardItemModel;

import java.util.ArrayList;

public class DashBoardItemAdapter extends RecyclerView.Adapter<DashBoardItemAdapter.MyViewHolder> {
    Context context;
    ArrayList<DashboardItemModel> dashboardItemModels;
    public DashBoardItemAdapter(Context context, ArrayList<DashboardItemModel> dashboardItemModels) {
        this.context=context;
        this.dashboardItemModels=dashboardItemModels;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.dashboard_item, parent, false);
        return new DashBoardItemAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position){
        holder.show_more_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, AvailableOrderActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dashboardItemModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView order_no,date,availble_order_item;
        RelativeLayout show_more_detail;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            order_no=itemView.findViewById(R.id.order_no);
            availble_order_item=itemView.findViewById(R.id.availble_order_item);
            show_more_detail=itemView.findViewById(R.id.show_more_dash);
            order_no.setText(dashboardItemModels.size()+"");


        }
    }
}
