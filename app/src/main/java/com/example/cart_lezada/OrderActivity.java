package com.example.cart_lezada;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.example.cart_lezada.Models.OrderDetailView;
import com.example.cart_lezada.Retrofit.ApiService;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {
    private PieChart pieChart;

    int userId = 8;
    List<OrderDetailView> orderDetailViewList = new ArrayList<OrderDetailView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        setControl();
        getListOrderAPI();
    }

    private void setEvent() {
        // Tạo dữ liệu cho biểu đồ
        float numberOfCart = getOrderByStatus("CART");
        float numberOfPrepare = getOrderByStatus("PREPARE");
        float numberOfShipping = getOrderByStatus("SHIPPING");
        float numberOfSuccess = getOrderByStatus("SUCCESS");
        float numberOfConfirm = getOrderByStatus("CONFIRM");
        float numberOfCanceled = getOrderByStatus("CANCELED");

        List<PieEntry> entries = new ArrayList<>();
        if (numberOfCart > 0.0) {
            entries.add(new PieEntry(numberOfCart, "CART: " + Float.toString(numberOfCart)));
        }
        if (numberOfPrepare > 0.0) {
            entries.add(new PieEntry(numberOfPrepare, "PREPARE: " + Float.toString(numberOfPrepare)));
        }
        if (numberOfShipping > 0.0) {
            entries.add(new PieEntry(numberOfShipping, "SHIPPING: " + Float.toString(numberOfShipping)));
        }
        if (numberOfSuccess > 0.0) {
            entries.add(new PieEntry(numberOfSuccess, "SUCCESS: " + Float.toString(numberOfSuccess)));
        }
        if (numberOfConfirm > 0.0) {
            entries.add(new PieEntry(numberOfConfirm, "CONFIRM: " + Float.toString(numberOfConfirm)));
        }
        if (numberOfCanceled > 0.0) {
            entries.add(new PieEntry(numberOfCanceled, "CANCELED: " + Float.toString(numberOfCanceled)));
        }




        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.GRAY, Color.WHITE);
        dataSet.setValueTextColor(Color.BLACK);

        PieData pieData = new PieData(dataSet);

        // Đặt dữ liệu vào biểu đồ
        pieChart.setData(pieData);

        // Tùy chỉnh biểu đồ
        pieChart.setCenterText("ORDERS");
        pieChart.setCenterTextSize(10);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setRotationEnabled(true);

        // Cập nhật giao diện
        pieChart.invalidate();
    }

    private void getListOrderAPI() {
        ApiService.apiService.getOrderDetailViews(userId).enqueue(new Callback<List<OrderDetailView>>() {
            @Override
            public void onResponse(Call<List<OrderDetailView>> call, Response<List<OrderDetailView>> response) {
                orderDetailViewList = response.body();
                System.out.println(orderDetailViewList.get(0).getStatus());
                System.out.println("getListOrderAPI call API success!");
                setEvent();
            }

            @Override
            public void onFailure(Call<List<OrderDetailView>> call, Throwable t) {
                Toast.makeText(OrderActivity.this, "Can't get data!" + t, Toast.LENGTH_SHORT).show();
                System.out.println("getListOrderAPI call API error " + t);
            }
        });
    }
    private float getOrderByStatus(String status) {
        float amount = 0;
        for (int i = 0 ; i < orderDetailViewList.size(); i++) {
            if (orderDetailViewList.get(i).getStatus().equals(status)){
                amount ++;
            }
        }
        return amount;
    }

    private void setControl() {
        pieChart = (PieChart) findViewById(R.id.pieChart);
    }
}