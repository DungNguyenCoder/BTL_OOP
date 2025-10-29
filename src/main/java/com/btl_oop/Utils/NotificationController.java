package com.btl_oop.Utils;

import com.btl_oop.Model.Entity.Notification;
import com.btl_oop.Model.Service.NotificationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationController {
    @FXML
    private Button notificationButton;
    @FXML
    private ListView<Notification> notificationListView;
    @FXML
    private VBox notificationPanel;
    
    private NotificationService notificationService;
    private Circle notificationBadge;
    private int unreadCount = 0;

    public void initialize() {
        notificationService = NotificationService.getInstance();
        
        // Create notification badge
        notificationBadge = new Circle(8);
        notificationBadge.setFill(Color.RED);
        notificationBadge.setVisible(false);
        
        // Add badge to notification button
        if (notificationButton != null) {
            HBox buttonContainer = new HBox();
            buttonContainer.getChildren().addAll(notificationButton, notificationBadge);
            notificationButton.setGraphic(buttonContainer);
        }
        
        // Setup cell factory for independent, interactive items
        if (notificationListView != null) {
            notificationListView.setCellFactory(lv -> new NotificationCell());
            // Allow variable cell heights to prevent overlapping content
            notificationListView.setFixedCellSize(-1);
        }

        // Listen for new notifications
        notificationService.addListener(this::onNotificationReceived);
        
        // Load existing notifications
        loadNotifications();
    }

    private void onNotificationReceived(Notification notification) {
        Platform.runLater(() -> {
            updateUnreadCount();
            // Luôn đồng bộ danh sách để panel mở ra là có ngay dữ liệu mới
            if (notificationListView != null) {
                refreshNotificationList();
            }
        });
    }

    private void updateUnreadCount() {
        unreadCount = notificationService.getUnreadNotifications().size();
        if (notificationBadge != null) {
            notificationBadge.setVisible(unreadCount > 0);
        }
        
        if (notificationButton != null) {
            String buttonText = unreadCount > 0 ? 
                "Thông báo (" + unreadCount + ")" : "Thông báo";
            notificationButton.setText(buttonText);
        }
    }

    @FXML
    private void toggleNotificationPanel() {
        if (notificationPanel != null) {
            boolean isVisible = notificationPanel.isVisible();
            notificationPanel.setVisible(!isVisible);
            notificationPanel.setManaged(!isVisible);
            
            if (!isVisible) {
                // Only load unread notifications, do NOT auto-mark as read
                refreshNotificationList();
            }
        }
    }

    private void loadNotifications() {
        if (notificationListView == null) return;
        // Chỉ hiển thị thông báo chưa đọc để tránh rối
        List<Notification> notifications = notificationService.getUnreadNotifications();
        // Sort newest first for determinism
        notifications.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        notificationListView.getItems().setAll(notifications);
        
        updateUnreadCount();
    }

    private void refreshNotificationList() {
        loadNotifications();
    }

    private static class NotificationCell extends ListCell<Notification> {
        private final VBox root = new VBox(5);
        private final HBox header = new HBox(10);
        private final Label iconLabel = new Label();
        private final VBox titleTimeBox = new VBox(2);
        private final Label titleLabel = new Label();
        private final Label timeLabel = new Label();
        private final Label messageLabel = new Label();
        private final HBox actionBox = new HBox(5);
        private final Button markReadButton = new Button("Đã đọc");

        public NotificationCell() {
            header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            iconLabel.setFont(new Font(16));
            titleLabel.setFont(new Font("System Bold", 12));
            titleLabel.setStyle("-fx-text-fill: #2c3e50;");
            timeLabel.setFont(new Font(10));
            timeLabel.setStyle("-fx-text-fill: #7f8c8d;");
            messageLabel.setFont(new Font(11));
            messageLabel.setStyle("-fx-text-fill: #34495e;");
            messageLabel.setWrapText(true);
            actionBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            markReadButton.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 3 8; -fx-background-radius: 3;"
            );
            header.getChildren().addAll(iconLabel, titleTimeBox);
            titleTimeBox.getChildren().addAll(titleLabel, timeLabel);
            actionBox.getChildren().add(markReadButton);
            root.getChildren().addAll(header, messageLabel, actionBox);
            root.setStyle("-fx-background-radius: 5; -fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-border-width: 1;");
        }

        @Override
        protected void updateItem(Notification notification, boolean empty) {
            super.updateItem(notification, empty);
            if (empty || notification == null) {
                // Clean up to avoid state carry-over between reused cells
                setText(null);
                setGraphic(null);
                return;
            }

            // Ensure width bindings don't accumulate across updates
            ListView<Notification> listView = getListView();
            if (listView != null) {
                root.prefWidthProperty().unbind();
                messageLabel.maxWidthProperty().unbind();
                root.prefWidthProperty().bind(listView.widthProperty().subtract(24));
                messageLabel.maxWidthProperty().bind(root.prefWidthProperty().subtract(10));
            }

            root.setStyle(
                "-fx-background-color: " + (notification.isRead() ? "#f8f9fa" : "#e3f2fd") + "; " +
                "-fx-background-radius: 5; -fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-border-width: 1;"
            );
            iconLabel.setText(notification.getType().getIcon());
            titleLabel.setText(notification.getTitle());
            timeLabel.setText(notification.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            messageLabel.setText(notification.getMessage());

            // Tooltip
            String tooltipText = String.format(
                "Loại: %s\nBàn: %d\nĐơn hàng: %d\nThời gian: %s",
                notification.getType().getDisplayName(),
                notification.getTableId(),
                notification.getOrderId(),
                notification.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );
            Tooltip tooltip = new Tooltip(tooltipText);
            Tooltip.install(root, tooltip);

            // Actions
            markReadButton.setOnAction(e -> {
                NotificationService.getInstance().markAsRead(notification.getNotificationId());
                // Remove from list immediately
                ListView<Notification> lv = getListView();
                if (lv != null) {
                    lv.getItems().remove(notification);
                }
            });

            setGraphic(root);
            setText(null);
        }
    }

    private void markAsRead(int notificationId) {
        notificationService.markAsRead(notificationId);
        refreshNotificationList();
    }

    private void markAllAsRead() {
        notificationService.getUnreadNotifications().forEach(n -> 
            notificationService.markAsRead(n.getNotificationId()));
        updateUnreadCount();
    }

    private void removeNotification(Notification notification) {
        notificationService.deleteNotification(notification);
        loadNotifications();
        updateUnreadCount();
    }

    @FXML
    private void clearAllNotifications() {
        notificationService.clearAllNotifications();
        refreshNotificationList();
    }

    public void cleanup() {
        if (notificationService != null) {
            notificationService.removeListener(this::onNotificationReceived);
        }
    }
}
