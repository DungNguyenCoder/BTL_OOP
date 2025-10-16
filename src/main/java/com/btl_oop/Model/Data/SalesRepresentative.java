package com.btl_oop.Model.Data;

public class SalesRepresentative {
    private String name;
    private double revenue;
    private int products;
    private int premium;
    private String status; // "Gold" or "Silver"
    private String avatarColor;

    public SalesRepresentative(String name, double revenue, int products, int premium,
                               String status, String avatarColor) {
        this.name = name;
        this.revenue = revenue;
        this.products = products;
        this.premium = premium;
        this.status = status;
        this.avatarColor = avatarColor;
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getRevenue() {
        return revenue;
    }

    public int getProducts() {
        return products;
    }

    public int getPremium() {
        return premium;
    }

    public String getStatus() {
        return status;
    }

    public String getAvatarColor() {
        return avatarColor;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public void setProducts(int products) {
        this.products = products;
    }

    public void setPremium(int premium) {
        this.premium = premium;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAvatarColor(String avatarColor) {
        this.avatarColor = avatarColor;
    }
}