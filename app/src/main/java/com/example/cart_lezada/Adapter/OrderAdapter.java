package com.example.cart_lezada.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.cart_lezada.Models.OrderDetailView;
import com.example.cart_lezada.R;
import com.example.cart_lezada.Retrofit.ApiService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderAdapter extends BaseAdapter {
    List<OrderDetailView> orderDetailViewList;
    Context context;
    int layout;
    int total;
    Map<Integer, Integer> orderDetails = new HashMap<>();
    Map<Integer, Integer> orderIds = new HashMap<>();

    public OrderAdapter(List<OrderDetailView> orderDetailViewList, Context context) {
        this.orderDetailViewList = orderDetailViewList;
        this.context = context;
    }
    @NonNull
    @Override
    public int getCount() {
        return orderDetailViewList.size();
    }

    @Override
    public Object getItem(int i) {
        return orderDetailViewList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return orderDetailViewList.get(i).getOrderId();
    }



    @Override
    public View getView(int position, @Nullable View convertView, @Nullable ViewGroup parent) {
        System.out.println(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_order, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        OrderDetailView orderDetailView = this.orderDetailViewList.get(position);
        ///Anh xa
        /// setText -setImg
        viewHolder.tvName.setText(orderDetailView.getName());
        viewHolder.tvPrice.setText(String.valueOf(orderDetailView.getPrice()));
        viewHolder.tvQuantity.setText(String.valueOf(orderDetailView.getAmount()));
        Glide.with(context)
                .load(orderDetailView.getUrl())
                .into(viewHolder.ivPhone);
        setAdapterEvent(viewHolder, position);
        return convertView;
    }

    private void setAdapterEvent(ViewHolder viewHolder, int position) {
        viewHolder.ivDetele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.cbBuy.isChecked()) {
                    Toast.makeText(context, "Please unchecked!", Toast.LENGTH_SHORT).show();
                } else {
                    showDeleteConfirmationDialog(position);
                }
            }
        });
        //giam so luong don hang cua 1 product
        viewHolder.ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDetailView orderDetailView = orderDetailViewList.get(position);
                int amount = orderDetailView.getAmount();
                amount--;
                if (amount > 0) {
                    orderDetailView.setAmount(amount);
                    viewHolder.tvQuantity.setText(String.valueOf(amount));
                    if (viewHolder.cbBuy.isChecked()) {
                        total -= orderDetailView.getPrice();
                        orderDetails.remove(orderDetailView.getProductId(), amount+1);
                        orderDetails.put(orderDetailView.getProductId(), amount);
                    }
                } else {
//                    Toast.makeText(context.getApplicationContext(), "Product: the amount must be more than 0!", Toast.LENGTH_SHORT).show();
                }
                sendTotalPriceToMainActivity(total);
            }
        });
        //tang so luong don hang cua 1 product
        viewHolder.ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderDetailView orderDetailView = orderDetailViewList.get(position);
                int amount = orderDetailView.getAmount();
                int remain = orderDetailView.getRemain();
                amount++;
                if (amount <= remain) {
                    orderDetailViewList.get(position).setAmount(amount);
                    viewHolder.tvQuantity.setText(String.valueOf(amount));
                    if (viewHolder.cbBuy.isChecked()) {
                        total += orderDetailView.getPrice();
                        orderDetails.remove(orderDetailView.getProductId(), amount-1);
                        orderDetails.put(orderDetailView.getProductId(), amount);
                    }
                } else {
                    Toast.makeText(context.getApplicationContext(), "Product: the amount must be less than the remaining amount!", Toast.LENGTH_SHORT).show();
                }
                sendTotalPriceToMainActivity(total);
            }
        });
        //checkbox event
        viewHolder.cbBuy.setOnClickListener(new View.OnClickListener() {
            OrderDetailView orderDetailView = orderDetailViewList.get(position);
            @Override
            public void onClick(View view) {
                int money = orderDetailView.getPrice() * orderDetailView.getAmount();
                int productId = orderDetailView.getProductId();
                int orderId = orderDetailView.getOrderId();
                int amount = orderDetailView.getAmount();
                if (viewHolder.cbBuy.isChecked()) {
                    total += money;
                    orderDetails.put(productId, amount);
                    orderIds.put(productId, orderId);
                } else {
                    total -= money;
                    orderDetails.remove(productId, amount);
                    orderIds.remove(productId, orderId);
                }
                sendTotalPriceToMainActivity(total);
            }
        });
    }

    private void sendTotalPriceToMainActivity(int total) {
        Intent intent = new Intent("send2Main");
        intent.putExtra("data", String.valueOf(total));
        intent.putExtra("orderDetails", (Serializable) orderDetails);
        intent.putExtra("orderIds", (Serializable) orderIds);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


    public class ViewHolder{
        TextView tvName,tvPrice;
        ImageView ivPhone;
        ImageView ivDetele, ivMinus, ivPlus;
        TextView tvQuantity;
        CheckBox cbBuy;
        public ViewHolder(View itemView){
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvQuantity = ((TextView) itemView.findViewById(R.id.tvQuantity));
            ivPhone = (ImageView) itemView.findViewById(R.id.ivPhone);
            ivDetele = (ImageView) itemView.findViewById(R.id.ivDelete);
            ivMinus = (ImageView) itemView.findViewById(R.id.ivMinus);
            ivPlus = (ImageView) itemView.findViewById(R.id.ivPlus);
            cbBuy = (CheckBox) itemView.findViewById(R.id.cbBuy);
        }
    }
    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you want delete this order?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DeleteProductID(orderDetailViewList.get(position).getOrderId());
                orderDetailViewList.remove(position);
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void DeleteProductID(int orderId) {
        ApiService.apiService.DeleteOder(orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                System.out.println("response = " + response);
                if (response.isSuccessful()) {
//                    Toast.makeText(context, "deleted success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}