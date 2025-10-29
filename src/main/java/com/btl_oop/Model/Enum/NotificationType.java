package com.btl_oop.Model.Enum;

public enum NotificationType {
    NEW_ORDER("Đơn hàng mới", "🆕"),
    ORDER_CONFIRMED("Đơn hàng đã xác nhận", "✅"),
    ORDER_READY("Đơn hàng sẵn sàng", "🍽️"),
    ORDER_PAID("Đơn hàng đã thanh toán", "💰"),
    TABLE_OCCUPIED("Bàn có khách", "👥"),
    TABLE_CLEANED("Bàn đã dọn dẹp", "🧹"),
    SYSTEM_ALERT("Cảnh báo hệ thống", "⚠️"),
    PAYMENT_ISSUE("Vấn đề thanh toán", "💳");

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
