package com.btl_oop.Controller.Waiter;

import com.btl_oop.Model.DAO.OrderDAO;
import com.btl_oop.Model.DAO.OrderItemDAO;
import com.btl_oop.Model.DAO.RestaurantTableDAO;
import com.btl_oop.Model.DAO.DishDAO;
import com.btl_oop.Model.DAO.EmployeeDAO;
import com.btl_oop.Model.Entity.RestaurantTable;
import com.btl_oop.Model.Entity.Dish;
import com.btl_oop.Model.Entity.Employee;
import com.btl_oop.Model.Entity.Order;
import com.btl_oop.Model.Entity.OrderItem;
import com.btl_oop.Model.Service.NotificationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderSummaryController {
    @FXML private TextField tableNumberField;
    @FXML private VBox orderItemsList;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private Button reviewOrderButton;
    @FXML private ImageView closeButton;
    private final List<OrderItem> items = new ArrayList<>();
    private final Map<String, OrderItemUI> itemUIMap = new HashMap<>(); // Key = dishId (String)
    private double subtotal = 0;
    private double tax = 0;
    private double total = 0;
    private int currentOrderId = 0;
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final RestaurantTableDAO tableDAO = new RestaurantTableDAO();
    private final DishDAO dishDAO = new DishDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final NotificationService notificationService = NotificationService.getInstance();
    private WaiterMainController parentController;
    public void setParentController(WaiterMainController controller) {
        this.parentController = controller;
    }
    private Dish getDishFromOrderItem(OrderItem item) {
        return dishDAO.getDishById(item.getDishId());
    }
    @FXML
    private void initialize() {
        System.out.println("OrderSummaryController initialized");

        if (closeButton != null) {
            closeButton.setOnMouseEntered(e -> closeButton.setOpacity(0.7));
            closeButton.setOnMouseExited(e -> closeButton.setOpacity(1.0));
        }
    }
    public void addDish(Dish dish, int quantity) {
        System.out.println("OrderSummaryController.addDish() called: " + dish.getName() + " x " + quantity);

        OrderItem existingItem = null;
        for (OrderItem i : items) {
            if (i.getDishId() == dish.getDishId()) {
                existingItem = i;
                break;
            }
        }
        if (existingItem != null) {
            System.out.println("Item exists, updating quantity from " + existingItem.getQuantity() + " to " + (existingItem.getQuantity() + quantity));
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            updateItemUI(existingItem);
        } else {
            System.out.println("New item, creating UI");
            OrderItem newItem = new OrderItem(0, 0, dish.getDishId(), quantity, dish.getPrice());
            items.add(newItem);
            createItemUI(newItem);
        }

        updateTotals();
    }

    private void createItemUI(OrderItem item) {
        // Lấy Dish từ database
        Dish dish = getDishFromOrderItem(item);
        if (dish == null) {
            System.err.println("ERROR: Cannot create UI for item with invalid DishID: " + item.getDishId());
            return;
        }

        VBox itemBox = new VBox(5);
        itemBox.setStyle(
                "-fx-background-color: #f8f8f8; " +
                        "-fx-padding: 10; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-width: 1;"
        );

        Label nameLabel = new Label(dish.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1a1a1a;");
        nameLabel.setWrapText(true);

        Label priceEachLabel = new Label(String.format("$%.2f × %d",
                dish.getPrice(),
                item.getQuantity()
        ));
        priceEachLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        HBox controlBox = new HBox(10);
        controlBox.setAlignment(Pos.CENTER_LEFT);
        controlBox.setStyle("-fx-padding: 5 0 0 0;");

        Button minusBtn = new Button("−");
        minusBtn.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4; " +
                        "-fx-background-radius: 4; " +
                        "-fx-padding: 5 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold;"
        );
        minusBtn.setOnAction(e -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                updateItemUI(item);
                updateTotals();
            } else {
                removeItem(item);
            }
        });

        Label qtyLabel = new Label(String.valueOf(item.getQuantity()));
        qtyLabel.setStyle(
                "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px; " +
                        "-fx-min-width: 25; " +
                        "-fx-alignment: center;"
        );

        Button plusBtn = new Button("+");
        plusBtn.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4; " +
                        "-fx-background-radius: 4; " +
                        "-fx-padding: 5 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold;"
        );
        plusBtn.setOnAction(e -> {
            item.setQuantity(item.getQuantity() + 1);
            updateItemUI(item);
            updateTotals();
        });

        Label totalPriceLabel = new Label(String.format("$%.2f",
                dish.getPrice() * item.getQuantity()
        ));
        totalPriceLabel.setStyle(
                "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: #1a1a1a;"
        );
        HBox.setHgrow(totalPriceLabel, Priority.ALWAYS);
        totalPriceLabel.setMaxWidth(Double.MAX_VALUE);
        totalPriceLabel.setAlignment(Pos.CENTER_RIGHT);

        controlBox.getChildren().addAll(minusBtn, qtyLabel, plusBtn, totalPriceLabel);
        itemBox.getChildren().addAll(nameLabel, priceEachLabel, controlBox);

        OrderItemUI itemUI = new OrderItemUI(itemBox, qtyLabel, priceEachLabel, totalPriceLabel);
        itemUIMap.put(String.valueOf(item.getDishId()), itemUI);

        orderItemsList.getChildren().add(itemBox);

        System.out.println("Item UI created and added to orderList for: " + dish.getName());
    }
    private void updateItemUI(OrderItem item) {
        // Lấy Dish từ database
        Dish dish = getDishFromOrderItem(item);
        if (dish == null) {
            System.err.println("ERROR: Cannot update UI for item with invalid DishID: " + item.getDishId());
            return;
        }

        String key = String.valueOf(item.getDishId());
        OrderItemUI itemUI = itemUIMap.get(key);

        if (itemUI != null) {
            itemUI.quantityLabel.setText(String.valueOf(item.getQuantity()));
            itemUI.priceEachLabel.setText(String.format("$%.2f × %d",
                    dish.getPrice(),
                    item.getQuantity()
            ));
            itemUI.totalPriceLabel.setText(String.format("$%.2f",
                    dish.getPrice() * item.getQuantity()
            ));
            System.out.println("Updated UI for: " + dish.getName() + ", new quantity: " + item.getQuantity());
        } else {
            System.err.println("WARNING: Could not find UI for dish ID: " + item.getDishId());
        }
    }
    private void removeItem(OrderItem item) {
        String key = String.valueOf(item.getDishId());
        OrderItemUI itemUI = itemUIMap.get(key);

        if (itemUI != null) {
            orderItemsList.getChildren().remove(itemUI.itemBox);
            itemUIMap.remove(key);
        }

        items.remove(item);
        updateTotals();

        Dish dish = getDishFromOrderItem(item);
        String dishName = dish != null ? dish.getName() : "DishID " + item.getDishId();
        System.out.println("Item removed: " + dishName);
    }
    private void updateTotals() {
        subtotal = 0;

        for (OrderItem item : items) {
            // Lấy Dish từ database theo dishId
            Dish dish = getDishFromOrderItem(item);
            if (dish != null) {
                subtotal += dish.getPrice() * item.getQuantity();
            } else {
                System.err.println("WARNING: Could not find dish with ID: " + item.getDishId());
            }
        }
        tax = subtotal * 0.1;
        total = subtotal + tax;

        subtotalLabel.setText(String.format("$%.2f", subtotal));
        taxLabel.setText(String.format("$%.2f", tax));
        totalLabel.setText(String.format("$%.2f", total));

        System.out.println("Totals updated - Items: " + items.size() +
                ", Subtotal: $" + String.format("%.2f", subtotal) +
                ", Tax: $" + String.format("%.2f", tax) +
                ", Total: $" + String.format("%.2f", total));
    }

    @FXML
    private void closeSummary() {
        System.out.println("Closing Order Summary");

        if (parentController != null) {
            parentController.hideOrderSummary();
        } else {
            orderItemsList.getScene().getWindow().hide();
        }
    }
    @FXML
    public void clearAll() {
        items.clear();
        itemUIMap.clear();
        orderItemsList.getChildren().clear();
        updateTotals();
        System.out.println("All items cleared!");
    }

    @FXML
    private void confirmOrder() {
        if (items.isEmpty()) {
            showAlert("Error", "Please add at least one item before confirming!");
            return;
        }

        try {
            int tableNumber;
            try {
                String tableText = tableNumberField.getText().trim();
                if (tableText.isEmpty()) {
                    showAlert("Input error", "Please enter table number!");
                    return;
                }
                tableNumber = Integer.parseInt(tableText);
            } catch (NumberFormatException e) {
                showAlert("Input error", "Table number must be an integer!");
                return;
            }
            int employeeId = Employee.getEmployeeId();
            if (employeeId == 0) {
                showAlert("Error", "No current staff found!");
                return;
            }

            Employee employee = employeeDAO.getEmployeeById(employeeId);
            if (employee == null) {
                showAlert("Error", "Employee does not exist in the system!");
                return;
            }

            int tableId = tableDAO.getTableIdByNumber(tableNumber);
            if (tableId == 0) {
                showAlert("Invalid table", "Table number " + tableNumber + " does not exist!");
                return;
            }

            // Chỉ cho phép Order khi bàn đang OCCUPIED (đã có khách ngồi)
            RestaurantTable table = tableDAO.getTableById(tableId);
            if (table == null) {
                showAlert("Error", "Table information not found!");
                return;
            }
            String status = table.getStatus().name();
            if ("AVAILABLE".equals(status)) {
                showAlert("Unable to create order", "Table is Available. Please seat guests first.");
                return;
            }

            Order order = new Order(
                    0,
                    tableId,
                    employeeId,
                    null,
                    "Serving"
            );

            boolean orderSaved = orderDAO.insertOrder(order);
            if (!orderSaved || order.getOrderId() <= 0) {
                showAlert("Error", "Unable to save order to database!");
                return;
            }

            for (OrderItem item : items) {
                item.setOrderId(order.getOrderId());
                boolean itemSaved = orderItemDAO.insertOrderItem(item);
                if (!itemSaved) {
                    Dish dish = dishDAO.getDishById(item.getDishId());
                    String dishName = dish != null ? dish.getName() : "ID " + item.getDishId();
                    showAlert("Error", "Unable to save item: " + dishName);
                    return;
                }
            }

            currentOrderId = order.getOrderId();

            // Gửi thông báo cho Manager
            notificationService.sendNewOrderNotification(tableId, order.getOrderId());

            showAlert("Sent", "The order has been saved and sent to the Manager for confirmation.");

            clearAll();
            tableNumberField.clear();

        } catch (Exception e) {
            System.err.println("Error confirming order: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "An error occurred while saving the order: " + e.getMessage());
        }
    }


    public void loadFromOrderItem(String orderId) {
        Platform.runLater(() -> {
            clearAll();

            List<OrderItem> orderItemArrayList;
            orderItemArrayList = orderItemDAO.getOrderItemsByOrderId(Integer.parseInt(orderId));
            for(OrderItem orderItem : orderItemArrayList) {
                addDish(getDishFromOrderItem(orderItem), orderItem.getQuantity());
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class OrderItemUI {
        VBox itemBox;
        Label quantityLabel;
        Label priceEachLabel;
        Label totalPriceLabel;

        OrderItemUI(VBox itemBox, Label quantityLabel, Label priceEachLabel, Label totalPriceLabel) {
            this.itemBox = itemBox;
            this.quantityLabel = quantityLabel;
            this.priceEachLabel = priceEachLabel;
            this.totalPriceLabel = totalPriceLabel;
        }
    }
}