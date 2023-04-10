package com.example.cart_lezada;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cart_lezada.Models.Order;
import com.example.cart_lezada.Retrofit.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    TextView tvUsername, tvPhoneNumber, tvAddress, tvPrice, tvDiscount, tvDeliveryFee, tvTotalPrice;
    RadioButton rbPaymentWithCard, rbPaymentOnDelivery;
    Button btnClose, btnConfirm;
    EditText edtCoupon;
    ImageView ivRefreshCoupon;
    String COUPONSHOP = "lezada"; //coupon discount 10%
    String FREESHIP = "freeship";
    Order order;
    //List<AmountProduct> orderDetails = new ArrayList<AmountProduct>();
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        setControl();
        setEvent();
    }


    private void setEvent() {
        Intent intent = getIntent();
        int orderId = intent.getIntExtra("orderId", -1);
        int userId = intent.getIntExtra("userId", -1);
        String username = intent.getStringExtra("username");
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String address = intent.getStringExtra("address");
        order = (Order) intent.getSerializableExtra("order");


        int price = intent.getIntExtra("price", 0);
        tvPrice.setText(String.valueOf(price)+ ".00$" );

        tvUsername.setText(username);
        tvPhoneNumber.setText(phoneNumber);
        tvAddress.setText(address);
        //all price before use coupon
        int deliveryPrice = 2;
        tvDeliveryFee.setText("+" + String.valueOf(deliveryPrice) + ".00$");
        int totalPrice = price + deliveryPrice;
        tvTotalPrice.setText(totalPrice + ".00$");
        //set price after use coupon
        ivRefreshCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //discount 10%
                String coupon = edtCoupon.getText().toString().trim();
                System.out.println(coupon);
                int discount = getDiscount(coupon, price);
                int deliveryPrice = getDeliveryPrice(coupon);
                if (discount != 0 || deliveryPrice == 0) {
                    int totalPrice = price - discount + deliveryPrice;
                    tvTotalPrice.setText(String.valueOf(totalPrice + ".00$"));
                    tvDiscount.setText("-" + String.valueOf(discount) + ".00$");
                    tvDeliveryFee.setText("+" + String.valueOf(deliveryPrice) + ".00$");
                } else {
                    Toast.makeText(PaymentActivity.this, "This coupon is not exist!", Toast.LENGTH_SHORT).show();
                }
            }
        });



        //radio box payment with visa
        rbPaymentWithCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rbPaymentWithCard.isChecked()) {
                    Toast.makeText(PaymentActivity.this, "The system is maintenance!", Toast.LENGTH_SHORT).show();
                    rbPaymentWithCard.setVisibility(View.GONE);
                }
            }
        });

        //confirm buy
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!rbPaymentOnDelivery.isChecked()) {
                    Toast.makeText(PaymentActivity.this, "Please choose payment method!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentActivity.this, "order confirmation successful!", Toast.LENGTH_SHORT).show();
                    createOrder();
                    System.out.println("orderId: " + String.valueOf(orderId));
                    deleteOrder(orderId);
                    quitApp();
                }

            }
        });
        //close and return previous page
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private int getDiscount(String coupon, int price) {
        if (coupon.toLowerCase().equals(COUPONSHOP)) {
            int discount = (int) (price * 0.1);
            return discount;
        }
        return 0;
    }

    private int getDeliveryPrice(String coupon) {
        if(coupon.toLowerCase().equals(FREESHIP)){
            return  0;
        }
        return 2;
    }


    private void setControl() {
        tvUsername = findViewById(R.id.tvUsername);
        tvPhoneNumber = findViewById(R.id.tvPhoneNumber);
        tvAddress = findViewById(R.id.tvAddress);
        tvPrice = findViewById(R.id.tvPrice);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvDeliveryFee = findViewById(R.id.tvDeliveryFee);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        rbPaymentWithCard = findViewById(R.id.rbPaymentWithCard);
        rbPaymentOnDelivery = findViewById(R.id.rbPaymentOnDelivery);
        btnClose = findViewById(R.id.btnClose);
        btnConfirm = findViewById(R.id.btnConfirm);
        edtCoupon = findViewById(R.id.edtCoupon);
        ivRefreshCoupon = findViewById(R.id.ivRefreshCoupon);
    }

    private void createOrder() {
        ApiService.apiService.createOrder(order).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response from the server
                    Toast.makeText(PaymentActivity.this, "Order created successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the error response from the server
                    Toast.makeText(PaymentActivity.this, "Failed to create order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle the network failure
                Toast.makeText(PaymentActivity.this, "Failed to create order: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void quitApp() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finishAffinity();
            }
        }, 2000);
    }

    private void deleteOrder(int orderId) {
        ApiService.apiService.DeleteOder(orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Handle the successful response from the server
                    Toast.makeText(PaymentActivity.this, "Order deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle the error response from the server
                    Toast.makeText(PaymentActivity.this, "Failed to delete order", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle the network failure
                Toast.makeText(PaymentActivity.this, "Failed to delete order: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}