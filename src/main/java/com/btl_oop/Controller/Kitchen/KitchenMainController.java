package com.btl_oop.Controller.Kitchen;

import com.btl_oop.Model.DAO.OrderDAO;
import com.btl_oop.Model.DAO.RestaurantTableDAO;
import com.btl_oop.Model.Service.TableManager;
import com.btl_oop.Model.Entity.Order;
import com.btl_oop.Model.Entity.RestaurantTable;
import com.btl_oop.Utils.AppConfig;
import com.btl_oop.Utils.SceneUtils;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class KitchenMainController {
    @FXML private VBox activeOrdersContainer;
    @FXML private VBox readyOrdersContainer;
    @FXML private Label orderSummaryLabel;
    @FXML private Label activeTitleLabel;
    @FXML private Label readyTitleLabel;
    @FXML private ScrollPane activeOrdersPane;
    @FXML private ScrollPane readyOrdersPane;
    private final OrderDAO orderDAO = new OrderDAO();
    private final RestaurantTableDAO tableDAO = new RestaurantTableDAO();
    private Timeline refreshTimeline;
    private MediaPlayer mediaPlayer;
    private final Set<Integer> knownActiveOrderIds = new HashSet<>();
    private boolean isFirstLoad = true;
    @FXML
    private void initialize() {
        System.out.println("KitchenMainController initialized");
        if (activeOrdersContainer != null && readyOrdersContainer != null) {
            initSound();
            refreshTables();
        } else {
            System.err.println("FXML fields not properly injected. Check fx:ids in kitchen-main.fxml");
        }
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> refreshTables()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }
    private void initSound() {
        try {
            String soundPath = AppConfig.PATH_NOTIFICATION_SOUND;
            if (soundPath != null && !soundPath.isBlank()) {
                java.net.URL url = getClass().getResource(soundPath);
                if (url != null) {
                    Media media = new Media(url.toExternalForm());
                    mediaPlayer = new MediaPlayer(media);
                } else {
                    System.err.println("Sound file not found: " + soundPath);
                }
            }
        } catch (Exception e) {
            System.err.println("Error initializing sound: " + e.getMessage());
        }
    }
    private void playSound() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.play();
            } catch (Exception e) {
                System.err.println("Error playing sound: " + e.getMessage());}}
    }
    private void refreshTables() {
        if (activeOrdersContainer == null || readyOrdersContainer == null) {
            System.err.println("FXML containers are null. Skipping refresh.");
            return;
        }
        activeOrdersContainer.getChildren().clear();
        readyOrdersContainer.getChildren().clear();
        List<RestaurantTable> allTables = tableDAO.getAllTables();
        int activeTableCount = 0;
        int readyTableCount = 0;
        Set<Integer> currentActiveOrderIds = new HashSet<>();
        for (RestaurantTable table : allTables) {
            Order order = orderDAO.getOrderByTableId(table.getTableId());
            if (order != null) {
                String orderStatus = order.getStatus();
                if (table.getStatus() == com.btl_oop.Model.Enum.TableStatus.ACTIVE_ORDERS) {
                    if ("Preparing".equalsIgnoreCase(orderStatus) || "Serving".equalsIgnoreCase(orderStatus)) {
                        addTableCard(activeOrdersContainer, table.getTableId(), table.getTableNumber(), order.getOrderId(), orderStatus);
                        activeTableCount++;
                        currentActiveOrderIds.add(order.getOrderId());
                    } else if ("Ready".equalsIgnoreCase(orderStatus)) {
                        addTableCard(readyOrdersContainer, table.getTableId(), table.getTableNumber(), order.getOrderId(), "Ready");
                        readyTableCount++;
                    }
                } else if (table.getStatus() == com.btl_oop.Model.Enum.TableStatus.READY_TO_SERVE) {
                }
            }
        }
        if (!isFirstLoad) {
            Set<Integer> newActiveOrders = new HashSet<>(currentActiveOrderIds);
            newActiveOrders.removeAll(knownActiveOrderIds);
            if (!newActiveOrders.isEmpty()) {
                playSound();
            }
        } else {
            isFirstLoad = false;
        }
        knownActiveOrderIds.clear();
        knownActiveOrderIds.addAll(currentActiveOrderIds);
        activeTitleLabel.setText("Active Orders (" + activeTableCount + ")");
        readyTitleLabel.setText("Ready to Serve (" + readyTableCount + ")");
        orderSummaryLabel.setText(activeTableCount + " active orders • " + readyTableCount + " ready to serve");
        activeOrdersPane.setVvalue(0.0);
        readyOrdersPane.setVvalue(0.0);
        showEmptyState(activeOrdersContainer, activeTableCount == 0, "⟳", "No active orders", "New orders will appear here");
        showEmptyState(readyOrdersContainer, readyTableCount == 0, "✓", "No orders ready", "Completed orders will appear here");
    }
    private void addTableCard(VBox container, int tableId, int tableNumber, int orderId, String status) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.PATH_ORDER_CARD));
            Node tableNode = loader.load();
            OrderCardController cardController = loader.getController();
            cardController.setData(tableId, tableNumber, orderId, status);
            cardController.setParentController(this);
            container.getChildren().add(tableNode);
        } catch (IOException e) {
            System.err.println("Lỗi load table-card: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void onPrepare(int tableId, int orderId) {
        orderDAO.updateOrderStatus(orderId, "Preparing");
        System.out.println("Table " + tableId + " Order " + orderId + " -> Preparing");
        refreshTables();
    }
    public void onDone(int tableId, int orderId) {
        boolean ok = false;
        try {
            ok = orderDAO.updateOrderStatus(orderId, "Ready");
        } catch (Exception e) {
            System.err.println("Failed to set order Ready: " + e.getMessage());
        }
        if (ok) {
            System.out.println("Table " + tableId + " Order " + orderId + " -> Ready");
            try {
                TableManager.getInstance().finishCooking(tableId);
            } catch (Exception e) {
                System.err.println("Failed to update table status to READY_TO_SERVE: " + e.getMessage());
            }
        }
        refreshTables();
    }
    public void onDoneImmediate(javafx.scene.Node cardRoot, int tableId, int orderId) {
        boolean ok = false;
        try {
            ok = orderDAO.updateOrderStatus(orderId, "Ready");
        } catch (Exception e) {
            System.err.println("Failed to set order Ready: " + e.getMessage());
        }
        if (ok) {
            if (cardRoot != null && cardRoot.getParent() == activeOrdersContainer) {
                activeOrdersContainer.getChildren().remove(cardRoot);
                readyOrdersContainer.getChildren().add(cardRoot);
                updateCounters();
            } else {
                refreshTables();
            }
        }
    }
    public void removeFromReady(javafx.scene.Node cardRoot, int tableId, int orderId) {
        try {
            tableDAO.updateTableStatus(tableId, com.btl_oop.Model.Enum.TableStatus.READY_TO_SERVE, "ORD" + orderId);
        } catch (Exception e) {
            System.err.println("Failed to update table status to READY_TO_SERVE: " + e.getMessage());
        }
        if (cardRoot != null && cardRoot.getParent() == readyOrdersContainer) {
            readyOrdersContainer.getChildren().remove(cardRoot);
            updateCounters();
        } else {
            refreshTables();
        }
    }
    private void updateCounters() {
        int activeTableCount = activeOrdersContainer.getChildren().size();
        int readyTableCount = readyOrdersContainer.getChildren().size();
        activeTitleLabel.setText("Active Orders (" + activeTableCount + ")");
        readyTitleLabel.setText("Ready to Serve (" + readyTableCount + ")");
        orderSummaryLabel.setText(activeTableCount + " active orders • " + readyTableCount + " ready to serve");
    }
    private void showEmptyState(VBox container, boolean isEmpty, String iconText, String title, String subtitle) {
        if (isEmpty) {
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.getStyleClass().add("empty-state");
            Text icon = new Text(iconText);
            icon.getStyleClass().add("empty-icon");
            Label titleLabel = new Label(title);
            titleLabel.getStyleClass().add("empty-title");
            Label subtitleLabel = new Label(subtitle);
            subtitleLabel.getStyleClass().add("empty-subtitle");
            emptyBox.getChildren().addAll(icon, titleLabel, subtitleLabel);
            container.getChildren().add(emptyBox);
        }
    }
    public void stopRefresh() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }
    }
    public void quit(ActionEvent actionEvent) throws IOException {
        stopRefresh();
        SceneUtils.switchTo(actionEvent, AppConfig.PATH_LOGIN_SCREEN);
    }
}