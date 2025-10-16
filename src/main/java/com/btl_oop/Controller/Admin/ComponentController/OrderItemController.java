package com.btl_oop.Controller.Admin.ComponentController;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class OrderItemController {
    @FXML
    private ImageView avatarImage;

    @FXML
    private Label orderIdLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Label customerNameLabel;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Button detailsButton;

    public void setOrderData(String orderId, String status, String customName, double totalPrice, String imagePath) {
        orderIdLabel.setText("Order id: " + orderId);
        customerNameLabel.setText(customName);
        totalPriceLabel.setText(String.format("Total : $%.2f", totalPrice));

        setStatus(status);

        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            avatarImage.setImage(image);
        } catch (Exception e) {
            System.err.println("Could not load image: " + imagePath);
        }
    }

    public void setStatus(String status) {
        statusLabel.setText(status);

        if (status.equalsIgnoreCase("Delivered")) {
            statusLabel.setStyle("-fx-background-color: #c8f7dc; -fx-text-fill: #00a86b; " +
                    "-fx-padding: 4 10; -fx-background-radius: 5; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else if (status.equalsIgnoreCase("In Progress")) {
            statusLabel.setStyle("-fx-background-color: #ffe4d6; -fx-text-fill: #ff8c00; " +
                    "-fx-padding: 4 10; -fx-background-radius: 5; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else {
            statusLabel.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #666; " +
                    "-fx-padding: 4 10; -fx-background-radius: 5; -fx-font-size: 11px; -fx-font-weight: bold;");
        }
    }

    public void setOnDetailsClick(Runnable action) {
        detailsButton.setOnAction(e -> action.run());
    }
}