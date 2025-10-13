package com.btl_oop.Model.Entity;

public class Dish {
    private String name;
    private double price;
    private String description;
    private int prepareTime;
    private String category;

    public Dish() {}

    public Dish(String name, double price, String description, int prepareTime, String category) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.prepareTime = prepareTime;
        this.category = category;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public int getPrepareTime() { return prepareTime; }
    public String getCategory() { return category; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setPrepareTime(int prepareTime) { this.prepareTime = prepareTime; }
    public void setCategory(String category) { this.category = category; }
}
