package com.example.cart_lezada.Models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Order implements Serializable {
    private int userId;
    private String orderPhone;
    private String orderAddress;
    private String orderStatus;

    private Map<Integer, Integer> orderDetails;

    public Map<Integer, Integer> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(Map<Integer, Integer> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOrderPhone() {
        return orderPhone;
    }

    public void setOrderPhone(String orderPhone) {
        this.orderPhone = orderPhone;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
