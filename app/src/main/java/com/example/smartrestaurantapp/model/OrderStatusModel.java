package com.example.smartrestaurantapp.model;

public class OrderStatusModel {
    String orderStatusId;
    String orderStatus;
    public OrderStatusModel(String orderStatusId, String orderStatus) {
        this.orderStatusId=orderStatusId;
        this.orderStatus=orderStatus;
    }

    public String getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(String orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
