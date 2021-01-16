package com.example.smartrestaurantapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartrestaurantapp.R;
import com.example.smartrestaurantapp.activitys.bluethoodPrinter.BTPrinterActivity;
import com.example.smartrestaurantapp.model.BillPrintingDetailModel;
import com.example.smartrestaurantapp.model.OrderDetailModel;
import com.example.smartrestaurantapp.model.OrderProductModifierModel;
import com.example.smartrestaurantapp.model.OrderProductSubModifierModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public  class BillPrintingDetailAdapter extends RecyclerView.Adapter<BillPrintingDetailAdapter.MyViewHolder> {
    Context context;
    ArrayList<BillPrintingDetailModel> billPrintingDetailModelArrayList;
    ArrayList<OrderProductModifierModel> orderProductModifierModelArrayList;
    ArrayList<OrderProductSubModifierModel> orderProductSubModifierModelArrayList;


    public BillPrintingDetailAdapter(Context context, ArrayList<BillPrintingDetailModel> billPrintingDetailModelArrayList) {
        this.context=context;
        this.billPrintingDetailModelArrayList=billPrintingDetailModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.bill_print_item, parent, false);
        return new BillPrintingDetailAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BillPrintingDetailModel bpm=billPrintingDetailModelArrayList.get(position);
        holder.item_name.setText(bpm.getProductName());
        holder.quent.setText(" X "+bpm.getProductQuantity());
        Log.e("price",bpm.getPrice());
        holder.amount.setText(bpm.getPrice());
        try {
            JSONArray jsonArray_productModifier = new JSONArray(bpm.getLstModifier());
            Log.e("array_productModifier", jsonArray_productModifier.length() + "");
            Log.e("array_product-------", jsonArray_productModifier + "");
            if (jsonArray_productModifier.length() == 0) {

            } else {
             //   holder.extra_item_layout.setVisibility(View.VISIBLE);
                for (int k = 0; k < jsonArray_productModifier.length(); k++) {
                    JSONObject jsonObject_productModifier = jsonArray_productModifier.getJSONObject(k);
                    String restaurantId_product = jsonObject_productModifier.getString("restaurantId");
                    String modifierId_product = jsonObject_productModifier.getString("modifierId");
                    String modifierName_product = jsonObject_productModifier.getString("modifierName");
                    String price_product = jsonObject_productModifier.getString("price");
                    String isActive_product = jsonObject_productModifier.getString("isActive");
                    String productList_product = jsonObject_productModifier.getString("productList");
                    String modifierDetails_product = jsonObject_productModifier.getString("modifierDetails");
                    String modifierTypeId_product = jsonObject_productModifier.getString("modifierTypeId");
                    String lstModifierType_product = jsonObject_productModifier.getString("lstModifierType");
                    String lstMenuModifierOptions_product = jsonObject_productModifier.getString("lstMenuModifierOptions");

                    OrderProductModifierModel orderProductModifierModel=new OrderProductModifierModel(restaurantId_product, modifierId_product, modifierName_product, price_product, isActive_product, productList_product,
                            modifierDetails_product, modifierTypeId_product, lstModifierType_product, lstMenuModifierOptions_product);
                    holder.orderProductModifierModelArrayList.add(orderProductModifierModel);
                }
                holder.orderProductModifierAdapter = new OrderProductModifierAdapter(context, holder.orderProductModifierModelArrayList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
                holder.extra_item_recycler.setLayoutManager(linearLayoutManager);
                holder.extra_item_recycler.setAdapter(holder.orderProductModifierAdapter);
            }
        }catch (Exception e){
            e.printStackTrace();
        }




//        try{
//            Log.e("product_size", String.valueOf(orderProductModifierModelArrayList.size()));
//            if(orderProductModifierModelArrayList.size()==0){
//                holder.extra_item_layout.setVisibility(View.INVISIBLE);
//            }else {
//                holder.extra_item_layout.setVisibility(View.VISIBLE);
//                OrderProductModifierModel orpm=orderProductModifierModelArrayList.get(position);
//                for (int i = 0; i < orderProductModifierModelArrayList.size(); i++) {
//                    holder.extra_item.setText(" + " + orpm.getModifierName_product());
//                    holder.extra_item_price.setText(orderProductModifierModelArrayList.get(i).getPrice_product());
////                try{
////                    for (int j=0;j<orderProductSubModifierModelArrayList.size();j++){
////                        holder.item_name.setText(" + "+orderProductSubModifierModelArrayList.get(i).getModifierOptionName_sub());
////                        holder.amount.setText(orderProductSubModifierModelArrayList.get(i).getPrice_sub());
////
////                    }
//
////                }catch (Exception e){
////                    e.printStackTrace();
////                }
//                }
//            }


//
//        }catch (Exception e){
//            e.printStackTrace();
//        }


    //    holder.amount.setText(bpm.get()+" ");

    }

    @Override
    public int getItemCount() {
        return billPrintingDetailModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView quent,item_name,amount;
        TextView extra_item,extra_item_price;
        LinearLayout extra_item_layout,extra_sub_item_layout;
        TextView extra_sub_item,extra_sub_item_price;
        RecyclerView extra_item_recycler;
        ArrayList<OrderProductModifierModel> orderProductModifierModelArrayList;
        OrderProductModifierAdapter orderProductModifierAdapter;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            quent = itemView.findViewById(R.id.questity);
            item_name = itemView.findViewById(R.id.item_name);
            amount=itemView.findViewById(R.id.amount);
            extra_item_recycler=itemView.findViewById(R.id.extra_item_recycler);
            orderProductModifierModelArrayList=new ArrayList<>();

        }
    }
}
