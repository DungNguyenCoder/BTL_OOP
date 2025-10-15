package com.btl_oop.Controller;

import com.btl_oop.Model.Entity.Customer;
import com.btl_oop.Model.Entity.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDateTime;

public class CustomerDetailController {

    @FXML
    private Label customerNameLabel;

    @FXML
    private Label customerEmailLabel;

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

    private Customer currentCustomer;
    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        // Load sample customer data
        loadCustomerData();
    }

    private void loadCustomerData() {
        // Sample data - Replace with actual database call
        currentCustomer = new Customer(
                1,
                "Jane Cooper",
                "Cooper",
                "janecooper@gmail.com",
                "Female",
                "English",
                "/com/btl_oop/img/avatar_customer.jpg",
                true,
                LocalDateTime.now().minusMonths(1)
        );

        displayCustomerData(currentCustomer);
    }

    private void displayCustomerData(Customer customer) {
        customerNameLabel.setText(customer.getFullName());
        customerEmailLabel.setText(customer.getEmail());

        fullNameField.setText(customer.getFullName());
        nickNameField.setText(customer.getNickName());
        genderField.setText(customer.getGender());
        languageField.setText(customer.getLanguage());

        emailAddressLabel.setText(customer.getEmail());
        emailTimeLabel.setText(customer.getEmailTimeAgo());
    }

    @FXML
    private void handleEdit() {
        isEditMode = !isEditMode;

        fullNameField.setEditable(isEditMode);
        nickNameField.setEditable(isEditMode);
        genderField.setEditable(isEditMode);
        languageField.setEditable(isEditMode);

        if (isEditMode) {
            System.out.println("Edit mode enabled");
            // Change button text or show save/cancel buttons
        } else {
            // Save changes
            saveCustomerData();
        }
    }

    private void saveCustomerData() {
        currentCustomer.setFullName(fullNameField.getText());
        currentCustomer.setNickName(nickNameField.getText());
        currentCustomer.setGender(genderField.getText());
        currentCustomer.setLanguage(languageField.getText());

        // Update labels
        customerNameLabel.setText(currentCustomer.getFullName());

        // Show success message
        showAlert(AlertType.INFORMATION, "Success", "Customer data updated successfully!");

        System.out.println("Customer data saved");
        // TODO: Save to database
    }

    @FXML
    private void handleAddEmail() {
        System.out.println("Add email clicked");
        // TODO: Show dialog to add new email
        showAlert(AlertType.INFORMATION, "Add Email", "Add email functionality will be implemented here.");
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Public method to load specific customer
    public void loadCustomer(Customer customer) {
        this.currentCustomer = customer;
        displayCustomerData(customer);
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }
}