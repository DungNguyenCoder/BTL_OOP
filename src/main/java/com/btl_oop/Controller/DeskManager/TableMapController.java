package com.btl_oop.Controller.DeskManager;

import com.btl_oop.Model.Entity.RestaurantTable;
import com.btl_oop.Model.DAO.OrderDAO;
import com.btl_oop.Model.Enum.TableStatus;
import com.btl_oop.Model.Service.TableManager;
import com.btl_oop.Utils.AppConfig;
import com.btl_oop.Utils.TableDataInitializer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TableMapController {

    private TableManager tableManager;
    private List<RestaurantTable> tables;
    private final OrderDAO orderDAO = new OrderDAO();

    @FXML
    private GridPane tableGridPane;

    @FXML
    private Label countAvailableTableLabel;

    @FXML
    private Label countOccupiedTableLabel;

    @FXML
    private Label countCleaningTableLabel;

    @FXML
    private Label countActivateOrdersTableLabel;

    @FXML
    private Label countTotalOrdersTableLabel;

    @FXML
    private Button refreshButton;

    @FXML
    private Button logoutButton;

    // Handle table action (main button click)
    @FXML
    private void handleTableAction(javafx.event.ActionEvent event) {
        Button button = (Button) event.getSource();
        String tableIdStr = (String) button.getUserData();
        int tableNumber = Integer.parseInt(tableIdStr.replace("Table ", ""));

        System.out.println("Looking for table number: " + tableNumber);
        System.out.println("TableManager instance: " + (tableManager != null ? "OK" : "NULL"));

        // Dữ liệu này được đọc từ cache, đã nhất quán
        RestaurantTable table = tableManager.getTableByNumber(tableNumber);
        System.out.println("Found table: " + (table != null ? table.toString() : "NULL"));

        if (table == null) {
            System.err.println("Table not found in TableManager cache!");
            showErrorDialog("Error", "Table not found! Please refresh the page.");
            return;
        }

        TableStatus currentStatus = table.getStatus();
        System.out.println("Table action for: " + tableNumber + " - Status: " + currentStatus);

        switch (currentStatus) {
            case AVAILABLE:
                showConfirmationDialog("Confirm Table Occupation",
                        "Are you sure you want to seat guests at " + tableIdStr + "?",
                        () -> seatGuests(table.getTableId()));
                break;
            case OCCUPIED:
                // Navigate to OrderDetails for occupied table
                viewOrderDetails(table.getTableId());
                break;
            case ACTIVE_ORDERS:
                showConfirmationDialog("Confirm Order Ready",
                        "Is the order for " + tableIdStr + " ready to serve?",
                        () -> finishCooking(table.getTableId()));
                break;
            case READY_TO_SERVE:
                // Navigate to PaymentProcessing for ready to serve table
                navigateToPaymentProcessing(table.getTableId());
                break;
            case CLEANING:
                showConfirmationDialog("Confirm Cleaning",
                        "Mark " + tableIdStr + " as cleaned?",
                        () -> markCleaned(table.getTableId()));
                break;
        }
    }



    // Handle refresh button
    @FXML
    private void handleRefresh() {
        System.out.println("Refreshing table data...");
        refreshUI();
    }

    // Handle logout button
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.PATH_LOGIN_SCREEN));
            Parent root = loader.load();

            Stage stage = (Stage) tableGridPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Restaurant POS - Login");
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load Login screen: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Error", "Failed to logout: " + e.getMessage());
        }
    }

    // Show confirmation dialog
    private void showConfirmationDialog(String title, String message, Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK && onConfirm != null) {
            onConfirm.run();
        }
    }

    // Show error dialog
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Show success dialog
    private void showSuccessDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Table action methods
    private void seatGuests(int tableId) {
        boolean success = tableManager.seatGuests(tableId);
        if (success) {
            showSuccessDialog("Success", "Table " + tableId + " is now occupied");
            refreshUI();
        } else {
            showErrorDialog("Error", "Failed to update table status");
        }
    }

    private void finishCooking(int tableId) {
        boolean success = tableManager.finishCooking(tableId);
        if (success) {
            showSuccessDialog("Success", "Table " + tableId + " order is ready to serve");
            refreshUI();
        } else {
            showErrorDialog("Error", "Failed to update table status");
        }
    }

    private void finishServing(int tableId) {
        boolean success = tableManager.finishServing(tableId);
        if (success) {
            showSuccessDialog("Success", "Table " + tableId + " is ready for cleaning");
            refreshUI();
        } else {
            showErrorDialog("Error", "Failed to update table status");
        }
    }

    private void markCleaned(int tableId) {
        boolean success = tableManager.markCleaned(tableId);
        if (success) {
            showSuccessDialog("Success", "Table " + tableId + " is now available");
            refreshUI();
        } else {
            showErrorDialog("Error", "Failed to update table status");
        }
    }

    private void viewOrderDetails(int tableId) {
        System.out.println("Viewing order details for: " + tableId);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.PATH_ORDER_DETAILS));
            Parent root = loader.load();

            // Get the controller and set table ID
            OrderDetailsController controller = loader.getController();
            controller.setTableId(tableId);

            Stage stage = (Stage) tableGridPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Restaurant POS - Order Details for Table " + tableId);
            stage.show();

        } catch (IOException e) {
            System.err.println("Failed to load OrderDetails.fxml");
            e.printStackTrace();
            showErrorDialog("Error", "Failed to load order details");
        }
    }

    private void navigateToPaymentProcessing(int tableId) {
        System.out.println("Navigating to PaymentProcessing for table: " + tableId);

        try {
            System.out.println("Loading PaymentProcessing.fxml from: " + AppConfig.PATH_PAYMENT_PROCESSING);

            // Check if resource exists
            java.net.URL resourceUrl = getClass().getResource(AppConfig.PATH_PAYMENT_PROCESSING);
            if (resourceUrl == null) {
                System.err.println("Resource not found: " + AppConfig.PATH_PAYMENT_PROCESSING);
                showErrorDialog("Error", "Resource not found: " + AppConfig.PATH_PAYMENT_PROCESSING);
                return;
            }
            System.out.println("Resource URL: " + resourceUrl);

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            System.out.println("FXML Loader created successfully");

            Parent root = loader.load();
            System.out.println("FXML loaded successfully");

            // Get the controller and set table/order context
            PaymentController controller = loader.getController();
            System.out.println("Controller loaded successfully: " + controller.getClass().getName());
            String orderIdStr = null;
            if (tableManager != null) {
                var table = tableManager.getTable(tableId);
                if (table != null) {
                    orderIdStr = table.getCurrentOrderId();
                }
            }
            int orderId = 0;
            if (orderIdStr != null && orderIdStr.startsWith("ORD")) {
                try { orderId = Integer.parseInt(orderIdStr.replace("ORD", "")); } catch (Exception ignore) {}
            }
            controller.setOrderContext(tableId, orderId);

            Stage stage = (Stage) tableGridPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Restaurant POS - Payment Processing for Table " + tableId);
            stage.show();
            System.out.println("PaymentProcessing window shown successfully");

        } catch (IOException e) {
            System.err.println("Failed to load PaymentProcessing.fxml: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Error", "Failed to load payment processing: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error loading PaymentProcessing: " + e.getMessage());
            e.printStackTrace();
            showErrorDialog("Error", "Unexpected error: " + e.getMessage());
        }
    }

    private void refreshUI() {
        // Refresh table data trong cache
        tableManager.refreshTables();
        // Lấy dữ liệu từ cache, không phải từ DB
        tables = tableManager.getAllCachedTables();

        // Update status counts (hàm này đã dùng cache)
        updateStatusCounts();

        // Refresh table grid (hàm này dùng biến 'tables' giờ đã lấy từ cache)
        refreshTableGrid();
    }

    private void updateStatusCounts() {
        // Hàm này đã nhất quán vì nó đọc từ cache
        Map<TableStatus, Integer> counts = tableManager.getTableStatusCounts();

        countAvailableTableLabel.setText(String.valueOf(counts.get(TableStatus.AVAILABLE)));
        countOccupiedTableLabel.setText(String.valueOf(counts.get(TableStatus.OCCUPIED)));
        countCleaningTableLabel.setText(String.valueOf(counts.get(TableStatus.CLEANING)));
        countActivateOrdersTableLabel.setText(String.valueOf(counts.get(TableStatus.ACTIVE_ORDERS)));

        // Total orders today = số đơn đã hoàn thành trong ngày (Status = Paid, theo CheckoutTime)
        try {
            int paidToday = orderDAO.countPaidOrdersToday();
            countTotalOrdersTableLabel.setText(String.valueOf(paidToday));
        } catch (Exception ex) {
            System.err.println("Failed to load today's orders count: " + ex.getMessage());
            countTotalOrdersTableLabel.setText("-");
        }
    }

    private void refreshTableGrid() {
        // Clear existing table nodes
        tableGridPane.getChildren().clear();

        // Always display exactly 8 tables in 2x4 grid
        // Create table nodes for positions 1-8
        for (int i = 1; i <= 8; i++) {
            // Hàm này giờ đọc từ biến 'tables' (lấy từ cache)
            RestaurantTable table = findTableByNumber(i);
            VBox tableNode = createTableNode(table);

            // Calculate grid position (0-based)
            int col = (i - 1) % 4;
            int row = (i - 1) / 4;

            tableGridPane.add(tableNode, col, row);
        }
    }

    private RestaurantTable findTableByNumber(int tableNumber) {
        // Biến 'tables' này giờ đã được lấy từ cache, nên đã nhất quán
        if (tables != null) {
            for (RestaurantTable table : tables) {
                if (table.getTableNumber() == tableNumber) {
                    return table;
                }
            }
        }

        // Return a default table if not found
        return new RestaurantTable(tableNumber, tableNumber, 4, TableStatus.AVAILABLE);
    }

    private VBox createTableNode(RestaurantTable table) {
        VBox tableBox = new VBox();
        tableBox.setAlignment(javafx.geometry.Pos.CENTER);
        tableBox.setSpacing(10);
        tableBox.getStyleClass().addAll("table-box", table.getStatusStyleClass());

        // Table title
        Label tableTitle = new Label("Table " + table.getTableNumber());
        tableTitle.getStyleClass().add("table-title");

        // Table icon
        Label tableIcon = new Label(table.getStatusIcon());
        tableIcon.getStyleClass().add("table-icon");

        // Main action button - show status for OCCUPIED, action for others
        String mainButtonText = (table.getStatus() == TableStatus.OCCUPIED) ?
                "Occupied" : table.getStatus().getActionText();
        Button actionButton = new Button(mainButtonText);
        actionButton.getStyleClass().add(table.getStatus().getButtonStyleClass());
        actionButton.setUserData("Table " + table.getTableNumber());

        // Add action for all tables
        actionButton.setOnAction(this::handleTableAction);

        tableBox.getChildren().addAll(tableTitle, tableIcon, actionButton);

        // Add additional buttons based on status
        if (table.getStatus() == TableStatus.ACTIVE_ORDERS || table.getStatus() == TableStatus.READY_TO_SERVE) {
            // Add order info if available
            if (table.getCurrentOrderId() != null) {
                Label orderInfo = new Label("Order: #" + table.getCurrentOrderId());
                orderInfo.getStyleClass().add("order-info");
                tableBox.getChildren().add(orderInfo);
            }

            // Add time info
            Label timeInfo = new Label("🕒 " + tableManager.getTimeSinceStatusChange(table.getTableId()));
            timeInfo.getStyleClass().add("time-info");
            tableBox.getChildren().add(timeInfo);
        }

        return tableBox;
    }

    // Initialize method
    public void initialize() {
        System.out.println("TableMapController initialized");

        try {
            tableManager = TableManager.getInstance();
            // Lấy dữ liệu từ cache, không phải từ DB
            tables = tableManager.getAllCachedTables();

            System.out.println("Loaded " + (tables != null ? tables.size() : 0) + " tables");

            // Only initialize sample data if no tables exist
            if (tables == null || tables.isEmpty()) {
                System.out.println("No tables found, initializing sample data...");
                TableDataInitializer.initializeTables();
                // Reload tables sau khi khởi tạo
                tableManager.refreshTables();
                // Lấy lại dữ liệu từ cache
                tables = tableManager.getAllCachedTables();
            }

            // Initialize UI
            updateStatusCounts();
            refreshTableGrid();

        } catch (Exception e) {
            System.err.println("Error initializing TableMapController: " + e.getMessage());
            e.printStackTrace();

            // Show error dialog
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Initialization Error");
            errorAlert.setHeaderText("Failed to load table data");
            errorAlert.setContentText("Please check database connection and try again.");
            errorAlert.showAndWait();
        }
    }
}