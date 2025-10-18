package com.btl_oop.Model.Enum;

public enum Category {
    SNACK("Snack"),
    MEAL("Meal"),
    VEGAN("Vegan"),
    DESSERT("Dessert"),
    DRINK("Drink");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Category fromString(String text) {
        for (Category category : Category.values()) {
            if (category.displayName.equalsIgnoreCase(text) || category.name().equalsIgnoreCase(text)) {
                return category;
            }
        }
        return null;
    }
}