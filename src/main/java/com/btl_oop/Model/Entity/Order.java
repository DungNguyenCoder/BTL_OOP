package com.btl_oop.Model.Entity;

import java.time.LocalDateTime;

public class Order {
    private int orderId;
    private int tableId;
    private int employeeId;
    private LocalDateTime checkoutTime;
    private String status; // Serving, Paid, Cancelled

    public Order(int orderId, int tableId, int employeeId, LocalDateTime checkoutTime,
                 String status) {
        this.orderId = orderId;
        this.tableId = tableId;
        this.employeeId = employeeId;
        this.checkoutTime = checkoutTime;
        this.status = status;
    }

    public Order() {
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public LocalDateTime getCheckoutTime() { return checkoutTime; }
    public void setCheckoutTime(LocalDateTime checkoutTime) { this.checkoutTime = checkoutTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Order{orderId=" + orderId + ", tableId=" + tableId + ", status='" + status + "' }";
    }
}