package com.btl_oop.Controller.Admin.ComponentController;

import com.btl_oop.Model.Entity.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class CustomerDetailDialogController {

    @FXML
    private Circle avatarCircle;

    @FXML
    private Label customerNameLabel;

    @FXML
    private Label customerEmailLabel;

    @FXML
    private Label statusBadge;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField nickNameField;

    @FXML
    private TextField genderField;

    @FXML
    private TextField languageField;

    @FXML
    private Label emailAddressLabel;

    @FXML
    private Label emailTimeLabel;

    @FXML
    private Button editButton;

    @FXML
    private Button saveButton;

    private Customer currentCustomer;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        // Initial setup
    }

    public void loadCustomer(Customer customer) {
        this.currentCustomer = customer;
        displayCustomerData(customer);
    }

    private void displayCustomerData(Customer customer) {
        // Set header info
        customerNameLabel.setText(customer.getFullName());
        customerEmailLabel.setText(customer.getEmail());

        // Set status badge
        statusBadge.setText(customer.isActive() ? "Active" : "Inactive");
        statusBadge.getStyleClass().removeAll("active", "inactive");
        if (customer.isActive()) {
            statusBadge.getStyleClass().add("active");
        } else {
            statusBadge.getStyleClass().add("inactive");
        }

        // Set avatar color (random color based on name)
        avatarCircle.setFill(getColorForName(customer.getFullName()));

        // Set form fields
        fullNameField.setText(customer.getFullName());
        nickNameField.setText(customer.getNickName());
        genderField.setText(customer.getGender());
        languageField.setText(customer.getLanguage());

        // Set email info
        emailAddressLabel.setText(customer.getEmail());
        emailTimeLabel.setText(customer.getEmailTimeAgo());
    }

    private Color getColorForName(String name) {
        // Generate color based on name hash
        int hash = name.hashCode();
        double hue = (hash % 360);
        return Color.hsb(hue, 0.6, 0.9);
    }

    @FXML
    private void handleEdit() {
        isEditMode = !isEditMode;

        // Toggle editable state
        fullNameField.setEditable(isEditMode);
        nickNameField.setEditable(isEditMode);
        genderField.setEditable(isEditMode);
        languageField.setEditable(isEditMode);

        // Toggle button visibility
        if (isEditMode) {
            editButton.setText("Cancel");
            saveButton.setVisible(true);
        } else {
            editButton.setText("Edit");
            saveButton.setVisible(false);
            // Restore original data
            displayCustomerData(currentCustomer);
        }
    }

    @FXML
    private void handleSave() {
        // Update customer data
        currentCustomer.setFullName(fullNameField.getText());
        currentCustomer.setNickName(nickNameField.getText());
        currentCustomer.setGender(genderField.getText());
        currentCustomer.setLanguage(languageField.getText());

        // Update display
        displayCustomerData(currentCustomer);

        // Exit edit mode
        isEditMode = false;
        editButton.setText("Edit");
        saveButton.setVisible(false);

        // Show success message
        showAlert(Alert.AlertType.INFORMATION, "Success", "Customer data updated successfully!");

        // TODO: Save to database
        System.out.println("Customer data saved: " + currentCustomer.getFullName());
    }

    @FXML
    private void handleAddEmail() {
        showAlert(Alert.AlertType.INFORMATION, "Add Email",
                "Add email functionality will be implemented here.");
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) customerNameLabel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }
}