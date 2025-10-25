package com.btl_oop.Model.Entity;

import com.btl_oop.Model.Enum.Category;

public class Dish {
    private int dishId;
    private String name;
    private double price;
    private String description;
    private int prepareTime;
    private String imageUrl;
    private String category;

    public Dish() {}

    public Dish(int dishId, String name, double price, String description, int prepareTime, String category, String imageUrl) {
        this.dishId = dishId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.prepareTime = prepareTime;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public int getDishId() {return dishId;}
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public int getPrepareTime() { return prepareTime; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }

    public void setDishId(int dishId) {this.dishId = dishId;}
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setPrepareTime(int prepareTime) { this.prepareTime = prepareTime; }
    public void setCategory(String category) { this.category = category; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
