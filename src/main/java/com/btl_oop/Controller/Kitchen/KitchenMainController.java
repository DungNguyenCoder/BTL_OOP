package com.btl_oop.Controller.Kitchen;

import com.btl_oop.Model.DAO.OrderDAO;
import com.btl_oop.Model.DAO.RestaurantTableDAO;
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
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(10), e -> refreshTables()));
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
                if ("Serving".equalsIgnoreCase(orderStatus) || "Preparing".equalsIgnoreCase(orderStatus)) {
                    addTableCard(activeOrdersContainer, table.getTableId(), table.getTableNumber(), order.getOrderId(), orderStatus);
                    activeTableCount++;
                } else if ("Paid".equalsIgnoreCase(orderStatus)) {
                    addTableCard(readyOrdersContainer, table.getTableId(), table.getTableNumber(), order.getOrderId(), "Ready");
                    readyTableCount++;
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
        orderDAO.updateOrderStatus(orderId, "Ready");
        System.out.println("Table " + tableId + " Order " + orderId + " -> Ready");
        refreshTables();
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