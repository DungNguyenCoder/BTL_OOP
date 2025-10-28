package com.btl_oop.Model.Enum;

public enum TableStatus {
    AVAILABLE("Available", "✔", "status-available", "table-btn-available"),
    OCCUPIED("Occupied", "👥", "status-occupied", "table-btn-occupied"),
    CLEANING("Cleaning", "❗", "status-cleaning", "table-btn-cleaning"),
    ACTIVE_ORDERS("Active Orders", "👨‍🍳", "status-active-orders", "table-btn-active-orders"),
    READY_TO_SERVE("Ready to Serve", "🍽", "status-ready-serve", "table-btn-ready-serve");

    private final String displayText;
    private final String icon;
    private final String styleClass;
    private final String buttonStyleClass;

    TableStatus(String displayText, String icon, String styleClass, String buttonStyleClass) {
        this.displayText = displayText;
        this.icon = icon;
        this.styleClass = styleClass;
        this.buttonStyleClass = buttonStyleClass;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getIcon() {
        return icon;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public String getButtonStyleClass() {
        return buttonStyleClass;
    }

    // Helper method to get next valid status
    public TableStatus getNextStatus() {
        switch (this) {
            case AVAILABLE:
                return OCCUPIED;
            case OCCUPIED:
                return ACTIVE_ORDERS;
            case ACTIVE_ORDERS:
                return READY_TO_SERVE;
            case READY_TO_SERVE:
                return CLEANING;
            case CLEANING:
                return AVAILABLE;
            default:
                return this;
        }
    }

    // Helper method to check if status transition is valid
    public boolean canTransitionTo(TableStatus newStatus) {
        switch (this) {
            case AVAILABLE:
                return newStatus == OCCUPIED;
            case OCCUPIED:
                return newStatus == ACTIVE_ORDERS;
            case ACTIVE_ORDERS:
                return newStatus == READY_TO_SERVE;
            case READY_TO_SERVE:
                return newStatus == CLEANING;
            case CLEANING:
                return newStatus == AVAILABLE;
            default:
                return false;
        }
    }

    // Helper method to get action text for buttons
    public String getActionText() {
        switch (this) {
            case AVAILABLE:
                return "Seat Guests";
            case OCCUPIED:
                return "View Order";
            case ACTIVE_ORDERS:
                return "In Kitchen";
            case READY_TO_SERVE:
                return "Ready to Serve";
            case CLEANING:
                return "Mark Clean";
            default:
                return "Unknown";
        }
    }
}
