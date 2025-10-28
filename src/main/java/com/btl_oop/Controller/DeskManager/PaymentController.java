package com.btl_oop.Controller.DeskManager;

import com.btl_oop.Model.Service.TableManager;
import com.btl_oop.Model.DAO.OrderDAO;
import com.btl_oop.Model.DAO.OrderItemDAO;
import com.btl_oop.Model.Entity.Order;
import com.btl_oop.Utils.AppConfig;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class PaymentController {

    @FXML
    private Button cashButton;

    @FXML
    private Button cardButton;

    @FXML
    private Button mobilePayButton;

    @FXML
    private Button processPaymentButton;

    @FXML
    private Button backButton;

    @FXML
    private VBox successMessageContainer;

    @FXML
    private Label successIconLabel;

    @FXML
    private Label successTitleLabel;

    @FXML
    private Label successSubtitleLabel;

    @FXML
    private HBox cardPaymentOption;

    @FXML
    private HBox cashPaymentOption;

    @FXML
    private HBox mobilePaymentOption;

    // Cash payment details removed for cleaner UI


    @FXML
    private Label tableNumberLabel;

    @FXML
    private Label billIdLabel;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label taxLabel;

    @FXML
    private Label totalLabel;


    @FXML
    private Label selectedMethodIcon;

    @FXML
    private Label selectedMethodLabel;

    private String selectedPaymentMethod = "mobile"; // Default to mobile pay
    private double paymentAmount = 0.0;
    private double cashReceived = 0.0;
    private int tableId = 0; // Track current table ID
    private int orderId = 0; // Current order id
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();

    @FXML
    private void handlePaymentMethod(javafx.event.ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String paymentMethod = (String) clickedButton.getUserData();

        // Update selected payment method
        selectedPaymentMethod = paymentMethod;

        // Update payment option styles
        updatePaymentOptionStyles();

        // Update button styles
        updatePaymentMethodButtons();

        // Update process payment button text
        updateProcessPaymentButton();

        // Show/hide cash payment details
        updateCashPaymentVisibility();

        // Update payment method summary
        updatePaymentMethodSummary();

        System.out.println("Selected payment method: " + paymentMethod);
    }

    // Cash payment handling removed for cleaner UI


    @FXML
    private void handleBack() {
        System.out.println("Back button clicked - returning to TableMap");
        navigateToTableMap();
    }

    @FXML
    private void handleProcessPayment() {
        System.out.println("Processing payment via: " + selectedPaymentMethod);

        // Cash payment validation removed for cleaner UI

        // Show confirmation dialog
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Payment");
        confirmationAlert.setHeaderText(null);

        String confirmationText = "Are you sure you want to process the payment of " +
            String.format("$%.2f", paymentAmount) + " via " + selectedPaymentMethod.toUpperCase() + "?";

        confirmationAlert.setContentText(confirmationText);

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            processPayment();
        }
    }

    private void processPayment() {
        // Update order totals and mark Paid
        try {
            if (orderId > 0) {
                boolean ok = orderDAO.processPayment(orderId, 0.10);
                if (!ok) {
                    showError("Failed to finalize order totals. Please try again.");
                    return;
                }
            }
        } catch (Exception ex) {
            System.err.println("Error processing order payment: " + ex.getMessage());
            ex.printStackTrace();
            showError("Payment failed: " + ex.getMessage());
            return;
        }

        // Change table status to CLEANING and clear current order id
        if (tableId > 0) {
            try {
                TableManager tableManager = TableManager.getInstance();
                boolean success = tableManager.finishServing(tableId);
                if (success) {
                    System.out.println("Table " + tableId + " marked for cleaning successfully");
                } else {
                    System.out.println("Failed to mark table " + tableId + " for cleaning");
                }
            } catch (Exception e) {
                System.err.println("Error changing table status to CLEANING: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Show success message
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Payment Successful");
        successAlert.setHeaderText(null);
        successAlert.setContentText("Payment of " + String.format("$%.2f", paymentAmount) +
            " has been processed successfully via " + selectedPaymentMethod.toUpperCase() + "!\n" +
            "Table has been marked for cleaning.");

        successAlert.showAndWait();

        // Navigate back to table map
        navigateToTableMap();
    }

    private void updatePaymentOptionStyles() {
        // Reset all payment options
        cardPaymentOption.getStyleClass().removeAll("selected");
        cashPaymentOption.getStyleClass().removeAll("selected");
        mobilePaymentOption.getStyleClass().removeAll("selected");

        // Add selected style to current option
        switch (selectedPaymentMethod) {
            case "card":
                cardPaymentOption.getStyleClass().add("selected");
                break;
            case "cash":
                cashPaymentOption.getStyleClass().add("selected");
                break;
            case "mobile":
                mobilePaymentOption.getStyleClass().add("selected");
                break;
        }
    }

    private void updatePaymentMethodButtons() {
        // Reset all buttons to normal style
        cashButton.getStyleClass().removeAll("selected");
        cardButton.getStyleClass().removeAll("selected");
        mobilePayButton.getStyleClass().removeAll("selected");

        // Update button text and style
        switch (selectedPaymentMethod) {
            case "cash":
                cashButton.getStyleClass().add("selected");
                cashButton.setText("Selected");
                cardButton.setText("Select");
                mobilePayButton.setText("Select");
                break;
            case "card":
                cardButton.getStyleClass().add("selected");
                cardButton.setText("Selected");
                cashButton.setText("Select");
                mobilePayButton.setText("Select");
                break;
            case "mobile":
                mobilePayButton.getStyleClass().add("selected");
                mobilePayButton.setText("Selected");
                cashButton.setText("Select");
                cardButton.setText("Select");
                break;
        }
    }

    // Cash payment methods removed for cleaner UI
    private void updateCashPaymentVisibility() {
        // Since cash payment details were removed for cleaner UI,
        // this method is kept for compatibility but does nothing
        // In a full implementation, this would show/hide cash-specific UI elements
    }


    private void updatePaymentMethodSummary() {
        String icon = getPaymentMethodIcon(selectedPaymentMethod);
        String methodName = getPaymentMethodDisplayName(selectedPaymentMethod);

        selectedMethodIcon.setText(icon);
        selectedMethodLabel.setText(methodName);
    }

    private String getPaymentMethodIcon(String method) {
        switch (method.toLowerCase()) {
            case "cash":
                return "💵";
            case "card":
                return "💳";
            case "mobile":
                return "📱";
            default:
                return "💳";
        }
    }


    private void updateProcessPaymentButton() {
        String buttonText = "Process " + selectedPaymentMethod.toUpperCase() + " Payment";
        processPaymentButton.setText(buttonText);
    }

    private void navigateToTableMap() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.PATH_TABLE_MAP));
            Parent root = loader.load();

            Stage stage = (Stage) processPaymentButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Restaurant POS - Table Map");
            stage.show();

        } catch (IOException e) {
            System.err.println("Failed to load TableMap.fxml");
            e.printStackTrace();
        }
    }

    public void initialize() {
        System.out.println("PaymentController initialized");

        // UI init but values will be set from context via setOrderContext
        initializeDefaultValues();

        // Initialize with default mobile pay selection
        updatePaymentOptionStyles();
        updatePaymentMethodButtons();
        updateProcessPaymentButton();
        updatePaymentMethodSummary();

    }

    private void initializeDefaultValues() {
        // Defaults until setOrderContext is called
        paymentAmount = 0.0;
        // amountValueLabel.setText("$40.67"); // Field not present in FXML
        subtotalLabel.setText("$0.0");
        taxLabel.setText("$0.0");
        totalLabel.setText("$0.0");

        // Set default payment method
        selectedPaymentMethod = "mobile";
        // paymentMethodLabel.setText("Via Mobile"); // Field not present in FXML

        // Set default success message
        successIconLabel.setText("✓");
        successTitleLabel.setText("Invoice Printed Successfully");
        successSubtitleLabel.setText("Customer has reviewed the order");

        // Default placeholders
        tableNumberLabel.setText("#-");
        billIdLabel.setText("[--]");

    }

    // Method to set payment amount dynamically
    public void setPaymentAmount(double amount) {
        this.paymentAmount = amount;
        // amountValueLabel.setText(String.format("$%.2f", amount)); // Field not present in FXML
    }

    // Method to set payment method dynamically
    public void setPaymentMethod(String method) {
        this.selectedPaymentMethod = method;
        updatePaymentMethodButtons();
        updateProcessPaymentButton();
        updatePaymentMethodLabel();
    }

    // Method to set success message dynamically
    public void setSuccessMessage(String icon, String title, String subtitle) {
        successIconLabel.setText(icon);
        successTitleLabel.setText(title);
        successSubtitleLabel.setText(subtitle);
    }

    // Set full context: table + order; compute and display real totals
    public void setOrderContext(int tableId, int orderId) {
        this.tableId = tableId;
        this.orderId = orderId;
        System.out.println("PaymentController: Context set table=" + tableId + ", order=" + orderId);

        if (tableNumberLabel != null) {
            tableNumberLabel.setText("#" + tableId);
        }

        if (orderId > 0) {
            try {
                Order order = orderDAO.getOrderById(orderId);
                if (order != null) {
                    billIdLabel.setText("ORD" + order.getOrderId());
                    // If subtotal/tax/total not computed yet, compute from items
                    double subtotal = order.getSubtotal();
                    double tax = order.getTax();
                    double total = order.getTotal();
                    if (total <= 0.0001) {
                        // compute using items
                        String calcSqlNotice = "Calculating totals from items";
                        System.out.println(calcSqlNotice);
                        // Reuse DAO method by calling processPayment with 0 tax to get subtotal, then UI compute
                        // But safer: mimic formula here
                        var items = orderItemDAO.getOrderItemsByOrderId(orderId);
                        subtotal = 0.0;
                        for (var it : items) {
                            var dish = new com.btl_oop.Model.DAO.DishDAO().getDishById(it.getDishId());
                            if (dish != null) subtotal += dish.getPrice() * it.getQuantity();
                        }
                        tax = subtotal * 0.10;
                        total = subtotal + tax;
                    }
                    this.paymentAmount = total;
                    subtotalLabel.setText(String.format("$%.2f", subtotal));
                    taxLabel.setText(String.format("$%.2f", tax));
                    totalLabel.setText(String.format("$%.2f", total));
                }
            } catch (Exception ex) {
                System.err.println("Failed to load order context: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void updatePaymentMethodLabel() {
        String methodText = "Via " + getPaymentMethodDisplayName(selectedPaymentMethod);
        // paymentMethodLabel.setText(methodText); // Field not present in FXML
    }

    private String getPaymentMethodDisplayName(String method) {
        switch (method.toLowerCase()) {
            case "cash":
                return "Cash";
            case "card":
                return "Card";
            case "mobile":
                return "Mobile";
            default:
                return "Unknown";
        }
    }
}
