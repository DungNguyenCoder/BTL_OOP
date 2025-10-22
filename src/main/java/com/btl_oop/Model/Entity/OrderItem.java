package com.btl_oop.Model.Entity;

public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int dishId;
    private int quantity;

    public OrderItem(int orderItemId, int orderId, int dishId, int quantity) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.dishId = dishId;
        this.quantity = quantity;
    }


    public int getOrderItemId() { return orderItemId; }
    public void setOrderItemId(int orderItemId) { this.orderItemId = orderItemId; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getDishId() { return dishId; }
    public void setDishId(int dishId) { this.dishId = dishId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {
        return "OrderItem{orderItemId=" + orderItemId + ", orderId=" + orderId + ", dishId=" + dishId + ", quantity=" + quantity + "}";
    }
}