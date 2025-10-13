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
    @FXML private Button addButton;
    @FXML private Label quantityLabel;
    @FXML private HBox addBox;
    @FXML private HBox quantityBox;

    private ChooseDishesController parentController;

    private Dish dish;
    private int quantity = 1;

    public void setData(Dish dish) {
        this.dish = dish;
        dishName.setText(dish.getName());
        dishPrice.setText(String.format("$%.2f", dish.getPrice()));
        dishDescription.setText(dish.getDescription());
        dishPrepareTime.setText(String.format("%d", dish.getPrepareTime()));
    }
    @FXML
    private void onAddToOrder(ActionEvent actionEvent) {
        addBox.setVisible(false);
        addBox.setManaged(false);
        quantityBox.setVisible(true);
        quantityBox.setManaged(true);
    }
    @FXML
    private void decreaseQuantity(ActionEvent actionEvent) {
        if (quantity > 1) {
            quantity--;
            quantityLabel.setText(String.valueOf(quantity));
        }
        else {
            quantity = 1;
            quantityLabel.setText("1");
            quantityBox.setVisible(false);
            quantityBox.setManaged(false);
            addBox.setVisible(true);
            addBox.setManaged(true);
            addButton.setDisable(false);
        }
    }
    @FXML
    private void increaseQuantity(ActionEvent actionEvent) {
        quantity++;
        quantityLabel.setText(String.valueOf(quantity));
    }
    @FXML
    private void confirmQuantity(ActionEvent actionEvent) {
        if (parentController != null) {
            parentController.addToOrder(dish, quantity);
        }
        System.out.println("Added to order: " + dish.getName() + " x" + quantity);

        quantity = 1;
        quantityLabel.setText("1");
        quantityBox.setVisible(false);
        quantityBox.setManaged(false);
        addBox.setVisible(true);
        addBox.setManaged(true);
        addButton.setDisable(false);
    }

    public void setParentController(ChooseDishesController controller) {
        this.parentController = controller;
    }
}
