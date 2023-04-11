package com.example.cart_lezada.Retrofit;

import com.example.cart_lezada.Models.Order;
import com.example.cart_lezada.Models.OrderDetailView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbjAwNyIsImlhdCI6MTY4MTExNzQ5MywiZXhwIjoxNjgxMjAzODkzfQ.7HI5XCbynBOP28oFo1WuwQ73uSTFd8lL-Fq3jpJqfyWtQnHDOSENUMW_xbQrZL6l_5xs1s7-dXOvrxwCPsw0bg";

    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100,TimeUnit.SECONDS)
            .addInterceptor(new TokenInterceptor(token))
            .build();
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    // client -> setTime gọi API,

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("http://192.168.1.4:8081/api/")
//            .baseUrl("https://d19cqcnpm01-api.azurewebsites.net/")
            .client(client) // bỏ cũng đc nếu API chạy nhanh
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("orderDetailViews")
    Call<List<OrderDetailView>> getOrderDetailViews(@Query("userId") int userId);
    @DELETE("orders/{orderId}")
    Call<Void> DeleteOder(@Path("orderId") int orderId);
    @POST("orders")
    Call<Void> createOrder(@Body Order order);
//    @PUT("orders")
//    Call<Void> updateOrder(@Body Order order);
}
