package com.btl_oop.Model;

public class CustomerFeedback {
    private String customerName;
    private String avatarColor;
    private String timeAgo;
    private String review;
    private double rating;
    private String foodImageUrl;

    public CustomerFeedback(String customerName, String avatarColor, String timeAgo,
                            String review, double rating, String foodImageUrl) {
        this.customerName = customerName;
        this.avatarColor = avatarColor;
        this.timeAgo = timeAgo;
        this.review = review;
        this.rating = rating;
        this.foodImageUrl = foodImageUrl;
    }

    // Getters
    public String getCustomerName() {
        return customerName;
    }

    public String getAvatarColor() {
        return avatarColor;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public String getReview() {
        return review;
    }

    public double getRating() {
        return rating;
    }

    public String getFoodImageUrl() {
        return foodImageUrl;
    }

    // Setters
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setAvatarColor(String avatarColor) {
        this.avatarColor = avatarColor;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setFoodImageUrl(String foodImageUrl) {
        this.foodImageUrl = foodImageUrl;
    }
}