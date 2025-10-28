package com.btl_oop.Controller.Admin.ComponentController;

import com.btl_oop.Model.Entity.Employee;
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

    private Employee customer;
    private Runnable onClickCallback;

    @FXML
    public void initialize() {
        // Will be set up after setData is called
    }

    public void setData(Employee customer, Runnable onClickCallback) {
        this.customer = customer;
        this.onClickCallback = onClickCallback;

        // Set customer data
        nameLabel.setText(customer.getFullName());
        phoneLabel.setText(customer.getEmail()); // Using email as phone since Customer doesn't have phone
        emailLabel.setText(customer.getEmail());
       // languageLabel.setText(customer.getLanguage());

        // Set status
        statusLabel.setText(customer.getStatus());
        statusLabel.getStyleClass().removeAll("active", "inactive");
        if (customer.getStatus().equals("Active")) {
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

    public Employee getCustomer() {
        return customer;
    }
}