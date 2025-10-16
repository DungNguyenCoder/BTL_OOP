package com.btl_oop.Model.Entity;

import java.time.LocalDateTime;

public class Customer {
    private int id;
    private String fullName;
    private String nickName;
    private String email;
    private String gender;
    private String language;
    private String avatarUrl;
    private boolean isActive;
    private LocalDateTime emailAddedDate;

    public Customer(int id, String fullName, String nickName, String email,
                    String gender,
                    boolean isActive, LocalDateTime emailAddedDate) {
        this.id = id;
        this.fullName = fullName;
        this.nickName = nickName;
        this.email = email;
        this.gender = gender;
        this.language = language;
        this.avatarUrl = avatarUrl;
        this.isActive = isActive;
        this.emailAddedDate = emailAddedDate;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getNickName() {
        return nickName;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getLanguage() {
        return language;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getEmailAddedDate() {
        return emailAddedDate;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setEmailAddedDate(LocalDateTime emailAddedDate) {
        this.emailAddedDate = emailAddedDate;
    }

    // Helper method to get time ago string
    public String getEmailTimeAgo() {
        if (emailAddedDate == null) return "";

        LocalDateTime now = LocalDateTime.now();
        long months = java.time.temporal.ChronoUnit.MONTHS.between(emailAddedDate, now);
        long days = java.time.temporal.ChronoUnit.DAYS.between(emailAddedDate, now);
        long hours = java.time.temporal.ChronoUnit.HOURS.between(emailAddedDate, now);

        if (months > 0) {
            return months + " month" + (months > 1 ? "s" : "") + " ago";
        } else if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else {
            return "Just now";
        }
    }
}
