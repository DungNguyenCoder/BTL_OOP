package com.btl_oop.Model.Entity;

import com.btl_oop.Model.Enum.TableStatus;

public class RestaurantTable {
    private int tableId;
    private int tableNumber;
    private int capacity;
    private TableStatus status;
    private String currentOrderId; // ID của order hiện tại (nếu có)
    private long statusChangeTime; // Thời gian thay đổi trạng thái cuối cùng

    public RestaurantTable(int tableId, int tableNumber, int capacity, TableStatus status) {
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
        this.currentOrderId = null;
        this.statusChangeTime = System.currentTimeMillis();
    }

    public RestaurantTable(int tableId, int tableNumber, int capacity, TableStatus status, String currentOrderId) {
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
        this.currentOrderId = currentOrderId;
        this.statusChangeTime = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }
    
    public int getTableNumber() { return tableNumber; }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }
    
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    
    public TableStatus getStatus() { return status; }
    public void setStatus(TableStatus status) { 
        this.status = status; 
        this.statusChangeTime = System.currentTimeMillis();
    }
    
    public String getCurrentOrderId() { return currentOrderId; }
    public void setCurrentOrderId(String currentOrderId) { this.currentOrderId = currentOrderId; }
    
    public long getStatusChangeTime() { return statusChangeTime; }
    public void setStatusChangeTime(long statusChangeTime) { this.statusChangeTime = statusChangeTime; }

    // Helper methods
    public boolean isAvailable() {
        return status == TableStatus.AVAILABLE;
    }
    
    public boolean isOccupied() {
        return status == TableStatus.OCCUPIED;
    }
    
    public boolean isCleaning() {
        return status == TableStatus.CLEANING;
    }
    
    public boolean hasActiveOrder() {
        return status == TableStatus.ACTIVE_ORDERS;
    }
    
    public boolean isReadyToServe() {
        return status == TableStatus.READY_TO_SERVE;
    }
    
    public String getStatusDisplayText() {
        return status.getDisplayText();
    }
    
    public String getStatusIcon() {
        return status.getIcon();
    }
    
    public String getStatusStyleClass() {
        return status.getStyleClass();
    }

    @Override
    public String toString() {
        return "RestaurantTable{tableId=" + tableId + ", tableNumber=" + tableNumber + 
               ", status='" + status + "', orderId='" + currentOrderId + "'}";
    }
}