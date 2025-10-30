package com.btl_oop.Model.Enum;

public enum NotificationType {
    NEW_ORDER("New order", "🆕"),
    ORDER_CONFIRMED("Confirmed order", "✅"),
    ORDER_READY("Order ready", "🍽️"),
    ORDER_PAID("Paid order", "💰"),
    TABLE_OCCUPIED("Occupied table", "👥"),
    TABLE_CLEANED("Cleaned table", "🧹"),
    SYSTEM_ALERT("System alert", "⚠️"),
    PAYMENT_ISSUE("Payment issue", "💳");

    private final String displayName;
    private final String icon;

    NotificationType(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
