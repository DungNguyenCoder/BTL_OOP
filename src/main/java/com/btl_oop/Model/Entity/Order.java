package com.btl_oop.Model.Entity;

import java.util.List;

public class Order {
    private int tableNumber;
    private List<OrderItem> items;
    private double subtotal;
    private double tax;
    private double total;
    private String timestamp;

    public Order(int tableNumber, List<OrderItem> items, double subtotal, double tax, double total, String timestamp) {
        this.tableNumber = tableNumber;
        this.items = items;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.timestamp = timestamp;
    }

    public int getTableNumber() { return tableNumber; }
    public List<OrderItem> getItems() { return items; }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public double getTotal() { return total; }
    public String getTimestamp() { return timestamp; }
}
