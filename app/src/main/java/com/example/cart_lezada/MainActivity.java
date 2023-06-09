package com.example.cart_lezada;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cart_lezada.Adapter.OrderAdapter;
import com.example.cart_lezada.Models.Order;
import com.example.cart_lezada.Models.OrderDetailView;
import com.example.cart_lezada.Retrofit.ApiService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{
    ListView lvOrders;
    TextView tvTotals;
    ImageView ivBuyNow;
    List<OrderDetailView> orderDetailViewList = new ArrayList<OrderDetailView>();
    Map<Integer, Integer> orderDetails = new HashMap<>();
    Map<Integer, Integer> orderIds = new HashMap<>();
    OrderAdapter adapter;
    int userId = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setControl();
        getListOrderAPI();
    }

    private void getListOrderAPI() {
        ApiService.apiService.getOrderDetailViews(userId).enqueue(new Callback<List<OrderDetailView>>() {
            @Override
            public void onResponse(Call<List<OrderDetailView>> call, Response<List<OrderDetailView>> response) {
                List<OrderDetailView> listTmp = new ArrayList<OrderDetailView>();
                listTmp = response.body();
                //lay list order dang co status = CART
                for (int i = 0; i < listTmp.size(); i++) {
                    if (listTmp.get(i).getStatus().equals("CART")) {
                        orderDetailViewList.add(listTmp.get(i));
                        System.out.println(listTmp.get(i).getOrderId() + " ---- ");
                    }
                }
                setEvent();
                System.out.println("getListOrderAPI call API success!");
            }

            @Override
            public void onFailure(Call<List<OrderDetailView>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Can't get data!" + t, Toast.LENGTH_SHORT).show();
                System.out.println("getListOrderAPI call API error " + t);
            }
        });
    }
//    private void updateOrderAPI(Order order) {
//        ApiService.apiService.updateOrder(order).enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                if (response.isSuccessful()) {
//                    // Handle the successful response from the server
//                    System.out.println("update order success");
//                } else {
//                    // Handle the error response from the server
//                    System.out.println("update order failure");
//                }
//            }
//            @Override
//            public void onFailure(Call<Void> call, Throwable t) {
//                // Handle the network failure
//                System.out.println("update order failure");
//            }
//        });
//    }

    private void setEvent() {
        setOrderAdapter();
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        tvTotals.setText(data);
        ivBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderDetailView orderDetailView = orderDetailViewList.get(0);
                String username = orderDetailView.getFirstName() + " " + orderDetailView.getLastName();
                if (username == null || username == "") {
                    username = "anonymous";
                }
                String phoneNumber = orderDetailView.getPhone();
                if (phoneNumber == null || phoneNumber == "" || phoneNumber.trim().length() < 1) {
                    phoneNumber = "(please update your phone number!)";
                }
                String address = orderDetailView.getAddress();
                if (address.length() < 1) {
                    address = "(please update your address!)";
                }
                int orderId = orderDetailView.getOrderId();
//                String price = String.valueOf(orderDetailView.getPrice());
                String total = tvTotals.getText().toString().trim();
                Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
                //intent.putExtra("orderId", orderId);
                intent.putExtra("userId", userId);
                intent.putExtra("username", username);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("address", address);


                Order order = new Order();
                order.setUserId(userId);
                order.setOrderPhone(phoneNumber);
                order.setOrderAddress(address);
                order.setOrderStatus("PREPARE");
                order.setOrderDetails(orderDetails);
                intent.putExtra("order", order);
                intent.putExtra("mainOrderIds", (Serializable) orderIds);

//                updateOrderAPI(order);

                if (total == "") {
                    Toast.makeText(MainActivity.this, "You didn't choose any item yet!", Toast.LENGTH_SHORT).show();
                } else {
                    intent.putExtra("price", Integer.parseInt(total));
                    startActivity(intent);
                }
            }
        });
    }
    private void setOrderAdapter() {
        adapter = new OrderAdapter(orderDetailViewList, MainActivity.this);
        lvOrders.setAdapter(adapter);
    }
    private void setControl() {
        lvOrders = findViewById(R.id.lvOrders);
        tvTotals = findViewById(R.id.tvTotals);
        ivBuyNow = findViewById(R.id.ivBuyNow);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String total = intent.getStringExtra("data");
            tvTotals.setText(total);
            orderDetails = (Map<Integer, Integer>) intent.getSerializableExtra("orderDetails");
            orderIds = (Map<Integer, Integer>) intent.getSerializableExtra("orderIds");
            orderDetails.forEach((key, value) -> {
                System.out.println(key + " - " + value);
            });
            System.out.println("----");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("send2Main"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

}