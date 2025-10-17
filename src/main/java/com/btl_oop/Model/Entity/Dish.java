package com.btl_oop.Model.Entity;

import com.btl_oop.Model.Enum.Category;

public class Dish {
    private String name;
    private double price;
    private String description;
    private int prepareTime;
    private String imageUrl;
    private Category category;
    private boolean isPopular;

    public Dish() {}

    public Dish(String name, double price, String description, int prepareTime, Category category, String imageUrl, boolean isPopular) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.prepareTime = prepareTime;
        this.category = category;
        this.imageUrl = imageUrl;
        this.isPopular = isPopular;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public int getPrepareTime() { return prepareTime; }
    public Category getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public boolean isPopular() { return isPopular; }

    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setPrepareTime(int prepareTime) { this.prepareTime = prepareTime; }
    public void setCategory(Category category) { this.category = category; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setPopular(boolean popular) { isPopular = popular; }
}
