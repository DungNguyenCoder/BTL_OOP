package com.btl_oop.Controller.DeskManager;

import com.btl_oop.Model.Service.TableManager;
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
        // Simulate payment processing
        System.out.println("Processing payment...");

        // Change table status to CLEANING
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

        // Initialize with default values
        initializeDefaultValues();

        // Initialize with default mobile pay selection
        updatePaymentOptionStyles();
        updatePaymentMethodButtons();
        updateProcessPaymentButton();
        updatePaymentMethodSummary();

    }

    private void initializeDefaultValues() {
        // Set default payment amount
        paymentAmount = 40.67;
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

        // Set default table info
        tableNumberLabel.setText("#1");
        billIdLabel.setText("[New]");

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

    // Method to set table ID
    public void setTableId(int tableId) {
        this.tableId = tableId;
        System.out.println("PaymentController: Table ID set to " + tableId);

        // Update table number display
        if (tableNumberLabel != null) {
            tableNumberLabel.setText("#" + tableId);
        }
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
