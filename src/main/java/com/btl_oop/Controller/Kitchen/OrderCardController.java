package com.btl_oop.Controller.Kitchen;

import com.btl_oop.Model.DAO.DishDAO;
import com.btl_oop.Model.DAO.OrderDAO;
import com.btl_oop.Model.DAO.OrderItemDAO;
import com.btl_oop.Model.Entity.OrderItem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.List;

public class OrderCardController {

    @FXML private javafx.scene.layout.VBox root;
    @FXML private Label tableNumberLabel;
    @FXML private Label orderIdLabel;
    @FXML private Label statusLabel;
    @FXML private GridPane itemsGrid;
    @FXML private Button tickBtn;
    private int tableId;
    private int tableNumber;
    private int orderId;
    private String status;
    private KitchenMainController parentController;
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final DishDAO dishDAO = new DishDAO();
    public enum OrderStatus {
        PENDING("Pending", "Prepare"),
        PREPARING("Preparing", "Done"),
        READY("Ready", "Done");
        private final String displayName;
        private final String buttonText;
        OrderStatus(String displayName, String buttonText) {
            this.displayName = displayName;
            this.buttonText = buttonText;
        }
        public String getDisplayName() {
            return displayName;
        }
        public String getButtonText() {
            return buttonText;
        }
        public static OrderStatus fromString(String status) {
            if (status == null) return PENDING;
            String key = status.toUpperCase().replace(" ", "_");
            if ("SERVING".equals(key)) {
                return PREPARING; // treat Serving as active in kitchen card
            }
            try {
                return OrderStatus.valueOf(key);
            } catch (IllegalArgumentException e) {
                return PENDING;
            }
        }
    }
    public void setData(int tableId, int tableNumber, int orderId, String status) {
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.orderId = orderId;
        this.status = status;
        tableNumberLabel.setText("Table #" + tableNumber);
        orderIdLabel.setText("Order #" + orderId);
        statusLabel.setText(status);
        loadItems();
        updateButtonState();
    }
    public void setParentController(KitchenMainController controller) {
        this.parentController = controller;
    }
    private void updateButtonState() {
        OrderStatus currentStatus = OrderStatus.fromString(status);
        tickBtn.setText(currentStatus.getButtonText());
        tickBtn.setStyle("-fx-font-size: 12px;");
    }
    private void loadItems() {
        itemsGrid.getChildren().clear();
        List<OrderItem> items = orderItemDAO.getOrderItemsByOrderId(orderId);
        if (items.isEmpty()) {
            Label emptyLabel = new Label("No items");
            emptyLabel.getStyleClass().add("empty-items");
            itemsGrid.add(emptyLabel, 0, 0);
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            Label nameLabel = new Label(dishDAO.getDishById(item.getDishId()).getName());
            nameLabel.getStyleClass().add("item-name");
            itemsGrid.add(nameLabel, 0, i);
            Label qtyLabel = new Label("x" + item.getQuantity());
            qtyLabel.getStyleClass().add("item-quantity");
            itemsGrid.add(qtyLabel, 1, i);
        }
    }
    @FXML
    private void tickDone() {
        OrderStatus currentStatus = OrderStatus.fromString(status);
        switch (currentStatus) {
            case PENDING:
                if (parentController != null) {
                    parentController.onPrepare(tableId, orderId);
                }
                updateStatus("Preparing");
                break;
            case PREPARING:
                if (parentController != null) {
                    parentController.onDoneImmediate(root, tableId, orderId);
                }
                updateStatus("Ready");
                break;
            case READY:
                // Order đã sẵn sàng phục vụ; Kitchen xác nhận đã đưa ra quầy
                if (parentController != null) {
                    parentController.removeFromReady(root, tableId, orderId);
                }
                disableButton();
                break;
        }
    }

    public void updateStatus(String newStatus) {
        this.status = newStatus;
        statusLabel.setText(newStatus);
        updateButtonState();
    }

    private void disableButton() {
        tickBtn.setDisable(true);
        tickBtn.setStyle("-fx-opacity: 0.5; -fx-cursor: default;");
    }

    public void resetCard() {
        this.status = "Pending";
        statusLabel.setText("Pending");
        tickBtn.setDisable(false);
        tickBtn.setStyle("-fx-font-size: 12px;");
        updateButtonState();
    }
}