package com.btl_oop.Model.Entity;

public class RestaurantTable {
    private int tableId;
    private int tableNumber;
    private int capacity;
    private String status; // Available, Occupied, Reserved

    public RestaurantTable(int tableId, int tableNumber, int capacity, String status) {
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
    }

    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }
    public int getTableNumber() { return tableNumber; }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    @Override
    public String toString() {
        return "RestaurantTable{tableId=" + tableId + ", tableNumber=" + tableNumber + ", status='" + status + "'}";
    }
}