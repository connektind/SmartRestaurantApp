package com.example.smartrestaurantapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurantapp.R;
import com.example.smartrestaurantapp.model.OrderProductModifierModel;
import com.example.smartrestaurantapp.model.OrderProductSubModifierModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public  class OrderProductModifierAdapter extends RecyclerView.Adapter<OrderProductModifierAdapter.MyViewHolder> {
    Context context;
    ArrayList<OrderProductModifierModel> orderProductModifierModelArrayList;
    public OrderProductModifierAdapter(Context context, ArrayList<OrderProductModifierModel> orderProductModifierModelArrayList) {
        this.context=context;
        this.orderProductModifierModelArrayList=orderProductModifierModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.extra_item, parent, false);
        return new OrderProductModifierAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OrderProductModifierModel ordm=orderProductModifierModelArrayList.get(position);
        holder.extra_item.setText(" + "+ordm.getModifierName_product());
        holder.extra_item_price.setText(ordm.getPrice_product());
        try {
            JSONArray jsonArray_productSubModifier = new JSONArray(ordm.getLstMenuModifierOptions_product());
//                                Log.e("array_productSubModifi", jsonArray_productSubModifier.length() + "");
//                                Log.e("array_productSub-------", jsonArray_productSubModifier + "");
            if (jsonArray_productSubModifier.length()==0){
            }else {
                for (int p = 0; p < jsonArray_productSubModifier.length(); p++) {
                    JSONObject jsonObject_productSubModifier = jsonArray_productSubModifier.getJSONObject(p);
                    String modifierOptionId_sub = jsonObject_productSubModifier.getString("modifierOptionId");
                    String modifierOptionName_sub = jsonObject_productSubModifier.getString("modifierOptionName");
                    //      Log.e("modifierOptionName_sub",modifierOptionName_sub);
                    String modifierId_sub = jsonObject_productSubModifier.getString("modifierId");
                    String price_sub = jsonObject_productSubModifier.getString("price");
                    String isActive_sub = jsonObject_productSubModifier.getString("isActive");

                    OrderProductSubModifierModel orpsm = new OrderProductSubModifierModel(modifierOptionId_sub, modifierOptionName_sub, modifierId_sub, price_sub, isActive_sub);
                    holder.orderProductSubModifierModelArrayList.add(orpsm);

                }
                holder.orderProductSubModifierAdapter = new OrderProductSubModifierAdapter(context, holder.orderProductSubModifierModelArrayList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                holder.extra_sub_item_recycler.setLayoutManager(linearLayoutManager);
                holder.extra_sub_item_recycler.setAdapter(holder.orderProductSubModifierAdapter);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return orderProductModifierModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView extra_item,extra_item_price;
        RecyclerView extra_sub_item_recycler;
        ArrayList<OrderProductSubModifierModel>orderProductSubModifierModelArrayList;
        OrderProductSubModifierAdapter orderProductSubModifierAdapter;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            extra_item_price=itemView.findViewById(R.id.extra_item_price);
            extra_item=itemView.findViewById(R.id.extra_item);
            extra_sub_item_recycler=itemView.findViewById(R.id.extra_sub_item_recycler);
            orderProductSubModifierModelArrayList=new ArrayList<>();

        }
    }
}
