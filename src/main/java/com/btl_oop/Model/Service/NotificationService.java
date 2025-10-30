package com.btl_oop.Model.Service;

import com.btl_oop.Model.Entity.Notification;
import com.btl_oop.Model.Enum.NotificationType;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.shape.Rectangle;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationService {
    private static NotificationService instance;
    private final List<Consumer<Notification>> listeners = new CopyOnWriteArrayList<>();
    private final List<Notification> notifications = new CopyOnWriteArrayList<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    // Track active popup stages to stack them and avoid NPEs
    private final List<Stage> activeStages = new CopyOnWriteArrayList<>();
    // Anchor stage (Manager window) to position popups relative to it
    private volatile Stage anchorStage;

    private NotificationService() {}

    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    // removed old container-based stage initialization

    public void addListener(Consumer<Notification> listener) {
        listeners.add(listener);
    }

    public void removeListener(Consumer<Notification> listener) {
        listeners.remove(listener);
    }

    public void sendNotification(Notification notification) {
        // Ensure unique ID assigned for stable identification
        if (notification.getNotificationId() == 0) {
            notification.setNotificationId(idGenerator.getAndIncrement());
        }
        notifications.add(notification);
        notifyListeners(notification);
        // Only show popup when an anchorStage (Manager) is registered
        if (anchorStage != null) {
            showNotificationPopup(notification);
        }
    }

    public void setAnchorStage(Stage stage) {
        this.anchorStage = stage;
    }

    public void sendNewOrderNotification(int tableId, int orderId) {
        Notification notification = new Notification(
            "New order",
            "Table " + tableId + " has a new order #" + orderId,
            NotificationType.NEW_ORDER,
            tableId,
            orderId,
            "MANAGER"
        );
        sendNotification(notification);
    }

    public void sendOrderConfirmedNotification(int tableId, int orderId) {
        Notification notification = new Notification(
            "Confirmed order",
            "Order #" + orderId + " at table " + tableId + " has been confirmed",
            NotificationType.ORDER_CONFIRMED,
            tableId,
            orderId,
            "ORDER"
        );
        sendNotification(notification);
    }

    public void sendOrderReadyNotification(int tableId, int orderId) {
        Notification notification = new Notification(
            "Order ready",
            "Order #" + orderId + " at table " + tableId + " is ready to be served",
            NotificationType.ORDER_READY,
            tableId,
            orderId,
            "MANAGER"
        );
        sendNotification(notification);
    }

    public void sendTableOccupiedNotification(int tableId) {
        Notification notification = new Notification(
            "Occupied table",
            "Table " + tableId + " is now occupied",
            NotificationType.TABLE_OCCUPIED,
            tableId,
            0,
            "MANAGER"
        );
        sendNotification(notification);
    }

    private void notifyListeners(Notification notification) {
        for (Consumer<Notification> listener : listeners) {
            try {
                listener.accept(notification);
            } catch (Exception e) {
                System.err.println("Error notifying listener: " + e.getMessage());
            }
        }
    }

    private void showNotificationPopup(Notification notification) {
        Platform.runLater(() -> {
            // Create individual stage for each notification
            Stage individualStage = new Stage();
            individualStage.initStyle(StageStyle.UNDECORATED);
            individualStage.setAlwaysOnTop(true);
            individualStage.setResizable(false);
            
            VBox notificationBox = createNotificationBox(notification);
            
            Scene scene = new Scene(notificationBox);
            scene.setFill(Color.TRANSPARENT);
            individualStage.setScene(scene);
            
            // Position at top-right corner with offset for multiple notifications
            double screenWidth = javafx.stage.Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = javafx.stage.Screen.getPrimary().getVisualBounds().getHeight();
            
            // Calculate position based on existing popup count
            int notificationCount = activeStages.size();

            double baseX;
            double baseY;

            if (anchorStage != null && anchorStage.isShowing()) {
                baseX = anchorStage.getX() + anchorStage.getWidth() - 350; // align to right of Manager window
                baseY = anchorStage.getY() + 20 + (notificationCount * 120);
                try {
                    individualStage.initOwner(anchorStage);
                } catch (Exception ignore) { }
            } else {
                // Fallback to primary screen
                baseX = screenWidth - 350;
                baseY = 20 + (notificationCount * 120);
            }

            individualStage.setX(baseX);
            individualStage.setY(baseY);
            
            // Show stage
            individualStage.show();
            activeStages.add(individualStage);

            // Auto-hide after 5 seconds
            Timeline autoHide = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
                hideIndividualNotification(individualStage);
            }));
            autoHide.play();

            // Animate in
            notificationBox.setTranslateX(400);
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), notificationBox);
            slideIn.setToX(0);
            slideIn.play();
        });
    }

    private VBox createNotificationBox(Notification notification) {
        VBox box = new VBox(5);
        box.setPadding(new Insets(15));
        box.setMaxWidth(320);
        box.setStyle(
            "-fx-background-color: #ffffff; " +
            "-fx-background-radius: 8; " +
            "-fx-border-color: #d0d0d0; " +
            "-fx-border-radius: 8; " +
            "-fx-border-width: 1;"
        );

        // Add drop shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5);
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));
        box.setEffect(shadow);

        // Header with icon, title and close button
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setSpacing(10);

        VBox titleTimeBox = new VBox(2);
        titleTimeBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(notification.getType().getIcon() + " " + notification.getTitle());
        titleLabel.setFont(new Font("System Bold", 14));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");

        Label timeLabel = new Label(notification.getTimestamp().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        timeLabel.setFont(new Font("System", 10));
        timeLabel.setStyle("-fx-text-fill: #7f8c8d;");

        titleTimeBox.getChildren().addAll(titleLabel, timeLabel);

        // Close button
        Label closeButton = new Label("✕");
        closeButton.setFont(new Font("System Bold", 14));
        closeButton.setStyle("-fx-text-fill: #e74c3c; -fx-cursor: hand; -fx-padding: 5;");
        closeButton.setOnMouseClicked(e -> {
            // Get the stage from the scene
            Stage stage = (Stage) box.getScene().getWindow();
            hideIndividualNotification(stage);
        });

        headerBox.getChildren().addAll(titleTimeBox, closeButton);

        // Message
        Label messageLabel = new Label(notification.getMessage());
        messageLabel.setFont(new Font("System", 12));
        messageLabel.setStyle("-fx-text-fill: #34495e;");
        messageLabel.setWrapText(true);

        box.getChildren().addAll(headerBox, messageLabel);

        return box;
    }

    private void hideIndividualNotification(Stage stage) {
        if (stage != null && stage.isShowing()) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                stage.close();
                activeStages.remove(stage);
            });
            fadeOut.play();
        }
    }

    // removed legacy container-based hide

    public List<Notification> getNotifications() {
        return new ArrayList<>(notifications);
    }

    public List<Notification> getUnreadNotifications() {
        return notifications.stream()
                .filter(n -> !n.isRead())
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void markAsRead(int notificationId) {
        notifications.stream()
                .filter(n -> n.getNotificationId() == notificationId)
                .findFirst()
                .ifPresent(n -> n.setRead(true));
    }

    public void clearAllNotifications() {
        notifications.clear();
        Platform.runLater(() -> {
            // Close all active popup stages
            for (Stage s : new ArrayList<>(activeStages)) {
                if (s != null && s.isShowing()) {
                    s.close();
                }
            }
            activeStages.clear();
        });
    }

    public void deleteNotificationById(int notificationId) {
        notifications.removeIf(n -> n.getNotificationId() == notificationId);
    }

    public void deleteNotification(Notification notification) {
        if (notification == null) return;
        notifications.remove(notification);
    }
}
