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
import com.example.cart_lezada.Models.OrderDetailView;
import com.example.cart_lezada.Retrofit.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity{
    ListView lvOrders;
    TextView tvTotals;
    ImageView ivBuyNow;
    List<OrderDetailView> orderDetailViewList = new ArrayList<OrderDetailView>();

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
                        System.out.println(i + " H ");
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
//                String price = String.valueOf(orderDetailView.getPrice());
                String total = tvTotals.getText().toString().trim();
                Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("phoneNumber", phoneNumber);
                intent.putExtra("address", address);
                if (total == "") {
                    Toast.makeText(MainActivity.this, "You didn't choose item!", Toast.LENGTH_SHORT).show();
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
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("my-event"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

}