package com.btl_oop.Model.Entity;

public class OrderTotals {
    private int orderId;
    private double subtotal;
    private double tax;
    private double total;

    public OrderTotals() {
    }

    public OrderTotals(int orderId, double subtotal, double tax, double total) {
        this.orderId = orderId;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    @Override
    public String toString() {
        return "OrderTotals{orderId=" + orderId +
                ", subtotal=" + subtotal +
                ", tax=" + tax +
                ", total=" + total + "}";
    }
}