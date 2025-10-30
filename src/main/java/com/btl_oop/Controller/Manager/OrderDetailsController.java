package com.btl_oop.Controller.Manager;

import com.btl_oop.Model.Service.TableManager;
import com.btl_oop.Model.Service.NotificationService;
import com.btl_oop.Model.Entity.RestaurantTable;
import com.btl_oop.Model.Entity.Order;
import com.btl_oop.Model.Entity.OrderItem;
import com.btl_oop.Model.Entity.Dish;
import com.btl_oop.Model.DAO.OrderDAO;
import com.btl_oop.Model.DAO.OrderItemDAO;
import com.btl_oop.Model.DAO.DishDAO;
import com.btl_oop.Model.DAO.RestaurantTableDAO;
import com.btl_oop.Model.DAO.EmployeeDAO;
import com.btl_oop.Model.Entity.Employee;
import com.btl_oop.Model.Enum.TableStatus;
import com.btl_oop.Utils.AppConfig;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OrderDetailsController {

    @FXML
    private Label orderIdLabel;

    @FXML
    private ImageView customerAvatar;

    @FXML
    private Label orderTimeLabel;

    @FXML
    private Label orderStatusLabel;

    @FXML
    private Label customerNameLabel;

    @FXML
    private Label customerPhoneLabel;

    @FXML
    private Label customerEmailLabel;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label taxLabel;

    @FXML
    private Label taxLabelText;

    @FXML
    private Label totalLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button orderButton;

    @FXML
    private VBox orderItemsContainer;

    @FXML
    private Label prepTimeLabel;

    @FXML
    private Label orderNumberInfoLabel;

    @FXML
    private Label instructionsTextLabel;

    // Table information
    private int currentTableId;
    private int currentOrderId;
    private TableManager tableManager;
    private final NotificationService notificationService = NotificationService.getInstance();

    // DAO instances
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private DishDAO dishDAO;
    private RestaurantTableDAO tableDAO;
    private EmployeeDAO employeeDAO;

    @FXML
    private void handleBack() {
        System.out.println("Back button clicked");
        navigateToTableMap();
    }

    @FXML
    private void handleOrder() {
        System.out.println("Order button clicked");
        confirmOrder();
    }


    private void navigateToTableMap() {
        try {
            System.out.println("Navigating back to TableMap...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.PATH_TABLE_MAP));
            Parent root = loader.load();

            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Restaurant POS - Table Map");
            stage.show();

            System.out.println("Successfully navigated to TableMap");

        } catch (IOException e) {
            System.err.println("Failed to load TableMap.fxml: " + e.getMessage());
            e.printStackTrace();

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Navigation Error");
            alert.setHeaderText("Failed to load Table Map");
            alert.setContentText("Unable to navigate back to table map. Please try again.");
            alert.showAndWait();
        }
    }


    private void confirmOrder() {
        if (currentTableId == 0) {
            showErrorDialog("Error", "No table selected!");
            return;
        }

        // Nếu đã có orderId (được tạo từ OrderSummary), cho phép xác nhận để cập nhật trạng thái bàn

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Order");
        confirmAlert.setHeaderText("Confirm Order Placement");
        confirmAlert.setContentText("Are you sure you want to place this order and send it to the kitchen?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (tableManager == null) {
                    tableManager = TableManager.getInstance();
                }

                // Nếu đã có currentOrderId, chỉ cập nhật trạng thái bàn
                if (currentOrderId > 0) {
                    try { orderDAO.updateOrderStatus(currentOrderId, "Preparing"); } catch (Exception ignore) {}
                    boolean success = tableManager.startOrder(currentTableId, "ORD" + currentOrderId);
                    if (success) {
                        // Gửi thông báo xác nhận đơn hàng
                        notificationService.sendOrderConfirmedNotification(currentTableId, currentOrderId);
                        showSuccessDialog("Order Confirmed", "Order #" + currentOrderId + " has been sent to the kitchen!");
                        navigateToTableMap();
                    } else {
                        showErrorDialog("Error", "Failed to update table status. Please try again.");
                    }
                    return;
                }

                // Trường hợp chưa có orderId (vào thẳng từ bản đồ bàn)
                Order newOrder = createNewOrder(currentTableId);
                if (newOrder != null) {
                    this.currentOrderId = newOrder.getOrderId();
                    boolean success = tableManager.startOrder(currentTableId, "ORD" + newOrder.getOrderId());
                    if (success) {
                        // Gửi thông báo xác nhận đơn hàng
                        notificationService.sendOrderConfirmedNotification(currentTableId, newOrder.getOrderId());
                        showSuccessDialog("Order Confirmed", "Order #" + newOrder.getOrderId() + " has been sent to the kitchen!");
                        navigateToTableMap();
                    } else {
                        showErrorDialog("Error", "Failed to update table status. Please try again.");
                    }
                } else {
                    showErrorDialog("Error", "Failed to create order. Please try again.");
                }

            } catch (Exception e) {
                System.err.println("Error confirming order: " + e.getMessage());
                e.printStackTrace();
                showErrorDialog("Error", "An error occurred while confirming the order.");
            }
        }
    }

    private Order createNewOrder(int tableId) {
        try {
            RestaurantTable table = tableDAO.getTableById(tableId);
            if (table == null) {
                System.err.println("Table not found with ID: " + tableId);
                return null;
            }

            Order newOrder = new Order(
                    0,
                    tableId,
                    1,
                    null,
                    "Preparing",
                    0.0,
                    0.0,
                    0.0
            );

            boolean success = orderDAO.insertOrder(newOrder);
            if (success && newOrder.getOrderId() > 0) {
                System.out.println("Order created successfully with ID: " + newOrder.getOrderId());
                return newOrder;
            } else {
                System.err.println("Failed to create order in database");
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error creating new order: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void showSuccessDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void initialize() {
        System.out.println("OrderDetailsController initialized");

        // Initialize table manager
        tableManager = TableManager.getInstance();

        // Initialize DAOs
        orderDAO = new OrderDAO();
        orderItemDAO = new OrderItemDAO();
        dishDAO = new DishDAO();
        tableDAO = new RestaurantTableDAO();
        employeeDAO = new EmployeeDAO();

        initializeNewOrderScreen();
    }

    public void setTableId(int tableId) {
        this.currentTableId = tableId;
        System.out.println("OrderDetailsController: Table ID set to " + tableId);

        RestaurantTable table = tableManager.getTable(tableId);

        if (table == null) {
            showErrorDialog("Error", "Table " + tableId + " not found!");
            initializeNewOrderScreen();
            return;
        }

        TableStatus status = table.getStatus();

        if (status == TableStatus.OCCUPIED) {
            // Bàn mới có khách (Order role chưa gửi), Manager không được tạo Order tại đây
            System.out.println("Table is OCCUPIED. Waiting for order from Order role.");
            initializeNewOrderScreen();
            orderIdLabel.setText("Order Id : [Waiting for Order]");
            this.currentOrderId = 0;

            // Thử tải đơn mới nhất ở trạng thái Serving cho bàn này
            List<Order> orders = orderDAO.getOrdersByTableId(tableId);
            Order pending = null;
            if (orders != null) {
                for (Order o : orders) {
                    if ("Serving".equals(o.getStatus())) {
                        if (pending == null || o.getOrderId() > pending.getOrderId()) {
                            pending = o;
                        }
                    }
                }
            }
            if (pending != null) {
                this.currentOrderId = pending.getOrderId();
                loadOrderDataById(this.currentOrderId);
                orderButton.setDisable(false); // Có đơn chờ, cho phép Manager xác nhận
            } else {
                orderButton.setDisable(true); // Chưa có đơn gửi tới
            }

        } else if (status == TableStatus.ACTIVE_ORDERS || status == TableStatus.READY_TO_SERVE) {
            System.out.println("Table has active order. Loading order...");
            String orderIdString = table.getCurrentOrderId();

            if (orderIdString != null && !orderIdString.isEmpty()) {
                try {
                    int orderId = Integer.parseInt(orderIdString.replace("ORD", ""));
                    this.currentOrderId = orderId;
                    loadOrderDataById(orderId); // Tải dữ liệu đơn hàng
                    orderButton.setDisable(true); // Đã order rồi, không cho nhấn nữa
                } catch (NumberFormatException e) {
                    showErrorDialog("Error", "Invalid order ID format: " + orderIdString);
                    initializeNewOrderScreen();
                }
            } else {
                // Lỗi: Bàn active mà không có Order ID
                showErrorDialog("Error", "Table status is active but no Order ID is associated!");
                initializeNewOrderScreen();
            }
        } else {
            // Lỗi: Không nên điều hướng tới đây từ trạng thái AVAILABLE hoặc CLEANING
            showErrorDialog("Error", "Invalid table state for viewing orders: " + status);
            initializeNewOrderScreen();
        }
    }

    public void setOrderId(int orderId) {
        this.currentOrderId = orderId;
        System.out.println("OrderDetailsController: Order ID set to " + orderId);

        loadOrderDataById(orderId);
    }

    // Khởi tạo khi được điều hướng từ OrderSummaryController
    public void initWithOrder(int tableId, int orderId) {
        this.currentTableId = tableId;
        this.currentOrderId = orderId;
        orderButton.setDisable(false);
        orderIdLabel.setText("Order Id : " + orderId);
        loadOrderDataById(orderId);
    }

    private void initializeNewOrderScreen() {
        orderIdLabel.setText("Order Id : Loading...");
        orderTimeLabel.setText("");
        orderStatusLabel.setText("");
        customerNameLabel.setText("");
        customerPhoneLabel.setText("");
        customerEmailLabel.setText("");

        // Set default order totals
        subtotalLabel.setText("$0.00");
        taxLabel.setText("$0.00");
        taxLabelText.setText("Tax:");
        totalLabel.setText("$0.00");

        // Set default order info
        if (prepTimeLabel != null) prepTimeLabel.setText("Estimated prep time: N/A");
        if (orderNumberInfoLabel != null) orderNumberInfoLabel.setText("Order # [New]");
        if (instructionsTextLabel != null) instructionsTextLabel.setText("No special instructions.");

        // Clear order items container
        if (orderItemsContainer != null) {
            orderItemsContainer.getChildren().clear();
        }

        // Set default avatar or placeholder
        try {
            // Try to load a default avatar or placeholder
            Image avatarImage = new Image(getClass().getResourceAsStream("/com/btl_oop/img/ic_item/account.png"));
            customerAvatar.setImage(avatarImage);
        } catch (Exception e) {
            System.out.println("Could not load default avatar image");
            // Set a placeholder or default image
        }
    }

    // Method to set order data dynamically
    public void setOrderData(String orderId, String orderTime, String orderStatus,
                             String customerName, String customerPhone, String customerEmail,
                             double subtotal, double tax, double total) {
        // Update order information
        orderIdLabel.setText("Order Id : " + orderId);
        orderTimeLabel.setText(orderTime);
        orderStatusLabel.setText(orderStatus);

        // Update customer information
        customerNameLabel.setText(customerName);
        customerPhoneLabel.setText(customerPhone);
        customerEmailLabel.setText(customerEmail);

        // Update order totals
        subtotalLabel.setText(String.format("$%.2f", subtotal));
        taxLabel.setText(String.format("$%.2f", tax));
        totalLabel.setText(String.format("$%.2f", total));

        // Update tax label text based on tax rate
        if (tax > 0 && subtotal > 0) {
            double taxRate = (tax / subtotal) * 100;
            taxLabelText.setText(String.format("Tax (%.0f%%):", taxRate));
        } else {
            taxLabelText.setText("Tax:");
        }

        // Update order info
        if (prepTimeLabel != null) prepTimeLabel.setText("Estimated prep time: 15 min");
        if (orderNumberInfoLabel != null) orderNumberInfoLabel.setText("Order #" + orderId);
        if (instructionsTextLabel != null) instructionsTextLabel.setText("No special instructions."); // Cần lấy từ Order
    }


    private void loadOrderDataById(int orderId) {
        try {
            Order order = orderDAO.getOrderById(orderId);
            if (order != null) {
                String employeeName = "Unknown";
                String employeePhone = "";
                String employeeEmail = "";
                try {
                    if (order.getEmployeeId() > 0) {
                        Employee emp = employeeDAO.getEmployeeById(order.getEmployeeId());
                        if (emp != null) {
                            employeeName = emp.getFullName();
                            employeePhone = emp.getPhoneNumber() != null ? emp.getPhoneNumber() : "";
                            employeeEmail = emp.getEmail() != null ? emp.getEmail() : "";
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Failed to load employee for order: " + ex.getMessage());
                }
                // Update order information
                setOrderData(
                        String.valueOf(order.getOrderId()),
                        order.getCheckoutTime() != null ? order.getCheckoutTime().toString() : "No time set",
                        order.getStatus(),
                        employeeName,
                        employeePhone,
                        employeeEmail,
                        order.getSubtotal(),
                        order.getTax(),
                        order.getTotal()
                );

                // Load order items
                loadOrderItems(orderId);
            } else {
                showErrorDialog("Error", "Order not found with ID: " + orderId);
            }
        } catch (Exception e) {
            System.err.println("Error loading order data for ID " + orderId + ": " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Error", "Failed to load order data: " + e.getMessage());
        }
    }

    // Load order items for the given order ID
    private void loadOrderItems(int orderId) {
        try {
            // Clear existing items
            orderItemsContainer.getChildren().clear();

            // Get order items from database
            List<OrderItem> orderItems = orderItemDAO.getOrderItemsByOrderId(orderId);

            if (orderItems.isEmpty()) {
                // Show no items message
                Label noItemsLabel = new Label("No items in this order");
                noItemsLabel.getStyleClass().add("no-items-message");
                orderItemsContainer.getChildren().add(noItemsLabel);
                return;
            }

            // Create UI for each order item
            for (int i = 0; i < orderItems.size(); i++) {
                OrderItem orderItem = orderItems.get(i);
                Dish dish = dishDAO.getDishById(orderItem.getDishId());

                if (dish != null) {
                    // Create order item row
                    HBox itemRow = createOrderItemRow(dish, orderItem);
                    orderItemsContainer.getChildren().add(itemRow);

                    // Add separator line between items (except for last item)
                    if (i < orderItems.size() - 1) {
                        Line separator = new Line();
                        separator.getStyleClass().add("item-separator");
                        orderItemsContainer.getChildren().add(separator);
                    }
                } else {
                    System.err.println("Dish not found for ID: " + orderItem.getDishId());
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading order items for order " + orderId + ": " + e.getMessage());
            e.printStackTrace();

            // Show error message
            Label errorLabel = new Label("Error loading order items");
            errorLabel.getStyleClass().add("error-message");
            orderItemsContainer.getChildren().add(errorLabel);
        }
    }

    // Create a single order item row
    private HBox createOrderItemRow(Dish dish, OrderItem orderItem) {
        HBox itemRow = new HBox();
        itemRow.getStyleClass().add("order-item-row");

        // Item image
        ImageView itemImage = new ImageView();
        itemImage.getStyleClass().add("item-image");
        itemImage.setFitWidth(80);
        itemImage.setFitHeight(80);

        System.out.println("Dish ImageUrl: " + dish.getImageUrl());

        // Try to load dish image
        try {
            if (dish.getImageUrl() != null && !dish.getImageUrl().isEmpty()) {
                Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(dish.getImageUrl())));
                itemImage.setImage(image);
            } else {
                // Load default image
                Image defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/btl_oop/img/img/product_macarons.png")));
                itemImage.setImage(defaultImage);
            }
        } catch (Exception e) {
            // Use default image if loading fails
            try {
                Image defaultImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/btl_oop/img/img/product_macarons.png")));
                itemImage.setImage(defaultImage);
            } catch (Exception ex) {
                System.out.println("Could not load default dish image");
            }
        }

        // Item details
        VBox itemDetails = new VBox();
        itemDetails.setSpacing(5);
        HBox.setHgrow(itemDetails, javafx.scene.layout.Priority.ALWAYS);

        Label itemName = new Label(dish.getName());
        itemName.getStyleClass().add("item-name");

        double itemPrice = dish.getPrice();
        int quantity = orderItem.getQuantity();
        Label itemPriceLabel = new Label(String.format("%d pc x $%.2f", quantity, itemPrice));
        itemPriceLabel.getStyleClass().add("item-details");

        itemDetails.getChildren().addAll(itemName, itemPriceLabel);

        // Item totals
        VBox itemTotals = new VBox();
        itemTotals.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        itemTotals.setSpacing(5);

        Label quantityLabel = new Label(quantity + " items");
        quantityLabel.getStyleClass().add("item-quantity");

        double totalPrice = itemPrice * quantity;
        Label totalLabel = new Label(String.format("$%.2f", totalPrice));
        totalLabel.getStyleClass().add("item-total");

        itemTotals.getChildren().addAll(quantityLabel, totalLabel);

        // Add all components to the row
        itemRow.getChildren().addAll(itemImage, itemDetails, itemTotals);

        return itemRow;
    }

    // Method to add order items dynamically
    public void addOrderItem(String itemName, String itemPrice, int quantity, String imagePath) {
        if (orderItemsContainer == null) return;

        // Create order item UI components
        HBox itemBox = new HBox();
        itemBox.setSpacing(15);
        itemBox.getStyleClass().add("order-item");

        // Item image
        ImageView itemImage = new ImageView();
        itemImage.getStyleClass().add("item-image");
        itemImage.setFitWidth(80);
        itemImage.setFitHeight(80);

        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            itemImage.setImage(image);
        } catch (Exception e) {
            System.out.println("Could not load item image: " + imagePath);
        }

        // Item details
        VBox itemDetails = new VBox();
        itemDetails.setSpacing(5);

        Label itemNameLabel = new Label(itemName);
        itemNameLabel.getStyleClass().add("item-name");

        Label itemPriceLabel = new Label(itemPrice);
        itemPriceLabel.getStyleClass().add("item-price");

        itemDetails.getChildren().addAll(itemNameLabel, itemPriceLabel);

        // Item totals
        VBox itemTotals = new VBox();
        itemTotals.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        itemTotals.setSpacing(5);

        Label itemQuantityLabel = new Label(quantity + " items");
        itemQuantityLabel.getStyleClass().add("item-quantity");

        double unitPrice = Double.parseDouble(itemPrice.replace("$", ""));
        double totalPrice = unitPrice * quantity;
        Label itemTotalLabel = new Label(String.format("$%.2f", totalPrice));
        itemTotalLabel.getStyleClass().add("item-total");

        itemTotals.getChildren().addAll(itemQuantityLabel, itemTotalLabel);

        // Add all components to item box
        itemBox.getChildren().addAll(itemImage, itemDetails, itemTotals);

        // Add to container
        orderItemsContainer.getChildren().add(itemBox);
    }
}