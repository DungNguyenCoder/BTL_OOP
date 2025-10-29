package com.btl_oop.Controller.Order;

import com.btl_oop.Model.Entity.Dish;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class DishItemController {

    @FXML private Label dishName;
    @FXML private Label dishPrice;
    @FXML private Label dishDescription;
    @FXML private Label dishPrepareTime;
    @FXML private Label popularTag;
    @FXML private Label spicyLevel;
    @FXML private Label allergyInfo;
    @FXML private Label quantityLabel;
    @FXML private Label quantityPrice;

    @FXML private Button addButton;

    @FXML private HBox addBox;
    @FXML private HBox quantityBox;
    @FXML private HBox confirmBox;
    @FXML private HBox spicyBox;
    @FXML private HBox allergyBox;

    private ChooseDishesController parentController;
    private Dish dish;
    private int quantity = 1;

    public void setData(Dish dish) {
        this.dish = dish;
        if (dish != null) {
            dishName.setText(dish.getName());
            dishPrice.setText(String.format("$%.2f", dish.getPrice()));
            dishDescription.setText(dish.getDescription());
            dishPrepareTime.setText(String.format("%dmin", dish.getPrepareTime()));

        }
    }

    public void setParentController(ChooseDishesController controller) {
        this.parentController = controller;
    }

    @FXML
    private void onAddToOrder(ActionEvent event) {
        System.out.println("Add button clicked for: " + dish.getName());

        // Hide Add button
        addBox.setVisible(false);
        addBox.setManaged(false);

        // Show quantity controls
        quantityBox.setVisible(true);
        quantityBox.setManaged(true);

        confirmBox.setVisible(true);
        confirmBox.setManaged(true);

        // Reset quantity
        quantity = 1;
        updateQuantityDisplay();
    }

    @FXML
    private void decreaseQuantity(ActionEvent event) {
        if (quantity > 1) {
            quantity--;
            updateQuantityDisplay();
        } else {
            // Cancel and go back to Add button
            resetToAddButton();
        }
    }

    @FXML
    private void increaseQuantity(ActionEvent event) {
        quantity++;
        updateQuantityDisplay();
    }

    @FXML
    private void confirmQuantity(ActionEvent event) {
        System.out.println("Confirm clicked! Adding " + quantity + "x " + dish.getName());

        if (parentController != null && dish != null) {
            parentController.addToOrder(dish, quantity);
            System.out.println("Successfully called parentController.addToOrder()");
        } else {
            System.err.println("ERROR: parentController or dish is null!");
        }

        // Reset to initial state
        resetToAddButton();
    }

    private void updateQuantityDisplay() {
        quantityLabel.setText(String.valueOf(quantity));
        quantityPrice.setText(String.format("$%.2f", dish.getPrice() * quantity));
    }

    private void resetToAddButton() {
        quantity = 1;
        updateQuantityDisplay();

        quantityBox.setVisible(false);
        quantityBox.setManaged(false);

        confirmBox.setVisible(false);
        confirmBox.setManaged(false);

        addBox.setVisible(true);
        addBox.setManaged(true);
    }
}