package com.btl_oop.Model.Entity;

import com.btl_oop.Model.Enum.NotificationType;

import java.time.LocalDateTime;

public class Notification {
    private int notificationId;
    private String title;
    private String message;
    private NotificationType type;
    private LocalDateTime timestamp;
    private boolean isRead;
    private int tableId;
    private int orderId;
    private String targetRole;

    public Notification() {
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    public Notification(String title, String message, NotificationType type, int tableId, int orderId, String targetRole) {
        this();
        this.title = title;
        this.message = message;
        this.type = type;
        this.tableId = tableId;
        this.orderId = orderId;
        this.targetRole = targetRole;
    }

    // Getters and Setters
    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                ", tableId=" + tableId +
                ", orderId=" + orderId +
                ", targetRole='" + targetRole + '\'' +
                '}';
    }
}
