package com.btl_oop.Controller.Admin.ComponentController;

import com.btl_oop.Model.Entity.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CustomerListItemController {

    @FXML
    private HBox customerRow;

    @FXML
    private Label nameLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label languageLabel;

    @FXML
    private Label statusLabel;

    private Customer customer;
    private Runnable onClickCallback;

    @FXML
    public void initialize() {
        // Will be set up after setData is called
    }

    public void setData(Customer customer, Runnable onClickCallback) {
        this.customer = customer;
        this.onClickCallback = onClickCallback;

        // Set customer data
        nameLabel.setText(customer.getFullName());
        phoneLabel.setText(customer.getEmail()); // Using email as phone since Customer doesn't have phone
        emailLabel.setText(customer.getEmail());
        languageLabel.setText(customer.getLanguage());

        // Set status
        statusLabel.setText(customer.isActive() ? "Active" : "Inactive");
        statusLabel.getStyleClass().removeAll("active", "inactive");
        if (customer.isActive()) {
            statusLabel.getStyleClass().add("active");
        } else {
            statusLabel.getStyleClass().add("inactive");
        }

        // Add click handler
        setupClickHandler();
    }

    private void setupClickHandler() {
        if (customerRow != null) {
            customerRow.setOnMouseClicked(event -> {
                if (onClickCallback != null) {
                    onClickCallback.run();
                }
            });
        }
    }

    public Customer getCustomer() {
        return customer;
    }
}