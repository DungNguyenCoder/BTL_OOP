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
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class KitchenMainController {

    @FXML private VBox activeOrdersContainer;
    @FXML private VBox readyOrdersContainer;
    @FXML private Label orderSummaryLabel;
    @FXML private Label activeTitleLabel;
    @FXML private Label readyTitleLabel;
    @FXML private ScrollPane activeOrdersPane;
    @FXML private ScrollPane readyOrdersPane;
    @FXML private Button btn_quit;

    private final OrderDAO orderDAO = new OrderDAO();
    private final RestaurantTableDAO tableDAO = new RestaurantTableDAO();
    private Timeline refreshTimeline;

    @FXML
    private void initialize() {
        System.out.println("KitchenMainController initialized");
        if (activeOrdersContainer != null && readyOrdersContainer != null) {
            refreshTables();
        } else {
            System.err.println("FXML fields not properly injected. Check fx:ids in kitchen-main.fxml");
        }
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> refreshTables()));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
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

        for (RestaurantTable table : allTables) {
            Order order = orderDAO.getOrderByTableId(table.getTableId());

            if (order != null) {
                String orderStatus = order.getStatus();
                if (table.getStatus() == com.btl_oop.Model.Enum.TableStatus.ACTIVE_ORDERS) {
                    if ("Preparing".equalsIgnoreCase(orderStatus) || "Serving".equalsIgnoreCase(orderStatus)) {
                        addTableCard(activeOrdersContainer, table.getTableId(), table.getTableNumber(), order.getOrderId(), orderStatus);
                        activeTableCount++;
                    } else if ("Ready".equalsIgnoreCase(orderStatus)) {
                        // Khi bàn vẫn ACTIVE_ORDERS và order đã Ready, hiển thị ở Ready to Serve
                        addTableCard(readyOrdersContainer, table.getTableId(), table.getTableNumber(), order.getOrderId(), "Ready");
                        readyTableCount++;
                    }
                } else if (table.getStatus() == com.btl_oop.Model.Enum.TableStatus.READY_TO_SERVE) {
                    // Sau khi Kitchen bấm Done (Ready list), bàn chuyển READY_TO_SERVE, không còn hiển thị ở Kitchen
                    // do Manager sẽ xử lý thanh toán ở màn khác
                }
            }
        }

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/btl_oop/FXML/Kitchen/order-card.fxml"));
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

    // Move card to Ready without requiring manual refresh
    public void onDoneImmediate(javafx.scene.Node cardRoot, int tableId, int orderId) {
        boolean ok = false;
        try {
            ok = orderDAO.updateOrderStatus(orderId, "Ready");
        } catch (Exception e) {
            System.err.println("Failed to set order Ready: " + e.getMessage());
        }
        if (ok) {
            // Move UI node from active -> ready list
            if (cardRoot != null && cardRoot.getParent() == activeOrdersContainer) {
                activeOrdersContainer.getChildren().remove(cardRoot);
                readyOrdersContainer.getChildren().add(cardRoot);
                updateCounters();
            } else {
                // Fallback to refresh if parent not matched
                refreshTables();
            }
        }
    }

    public void removeFromReady(javafx.scene.Node cardRoot, int tableId, int orderId) {
        try {
            // Cập nhật trực tiếp DB để tránh cache lỗi
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

    public void onServe(int tableId, int orderId) {
        orderDAO.updateOrderStatus(orderId, "Served");
        System.out.println("Table " + tableId + " Order " + orderId + " -> Served");
        refreshTables();
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
    }

    public void quit(ActionEvent actionEvent) throws IOException {
        SceneUtils.switchTo(actionEvent, AppConfig.PATH_LOGIN_SCREEN);
    }
}