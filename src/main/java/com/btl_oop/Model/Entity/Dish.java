package com.btl_oop.Model.Entity;

public class Dish {
    private int dishId;
    private String name;
    private double price;
    private String description;
    private int prepareTime;
    private String category;
    private String imageURL;
    public Dish() {}

    public Dish(int dishId, String name, double price, String description, int prepareTime, String category,String imageURL) {
        this.dishId = dishId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.prepareTime = prepareTime;
        this.category = category;
        this.imageURL = imageURL;
    }

    public int getDishId() {return dishId;}
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public int getPrepareTime() { return prepareTime; }
    public String getCategory() { return category; }
    public String getImageURL() {return imageURL;}

    public void setDishId(int dishId) {this.dishId = dishId;}
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setPrepareTime(int prepareTime) { this.prepareTime = prepareTime; }
    public void setCategory(String category) { this.category = category; }
    public void setImageURL(String imageURL) {this.imageURL = imageURL;}

}
