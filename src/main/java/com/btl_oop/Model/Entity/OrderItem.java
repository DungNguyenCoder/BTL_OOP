package com.btl_oop.Model.Entity;

public class OrderItem {
    private Dish dish;
    private int quantity;

    public OrderItem(Dish dish, int quantity) {
        this.dish = dish;
        this.quantity = quantity;
    }
    public Dish getDish() { return dish; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
