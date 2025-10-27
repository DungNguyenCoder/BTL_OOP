package com.btl_oop.Controller.Admin.ComponentController;

import com.btl_oop.Controller.Order.OrderSummaryController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import kotlin.comparisons.UComparisonsKt;

import javax.swing.*;
import java.io.IOException;

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
    private String orderIdValue;
    private String statusValue;
    private String customerNameValue;
    private double totalPriceValue;
    private String imagePathValue;

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

        if (status.equalsIgnoreCase("Serving")) {

            statusLabel.setStyle(
                    "-fx-background-color: #E8F5E9;" +
                            "-fx-text-fill: #2E7D32;" +
                            "-fx-padding: 4 10;" +
                            "-fx-background-radius: 6;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;"
            );
        } else if (status.equalsIgnoreCase("Paid")) {
            statusLabel.setStyle(
                    "-fx-background-color: #FFEBEE;" +
                            "-fx-text-fill: #C62828;" +
                            "-fx-padding: 4 10;" +
                            "-fx-background-radius: 6;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;"
            );
        } else if (status.equalsIgnoreCase("Cancel")) {

            statusLabel.setStyle(
                    "-fx-background-color: #FFF8E1;" +
                            "-fx-text-fill: #F57F17;" +
                            "-fx-padding: 4 10;" +
                            "-fx-background-radius: 6;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;"
            );
        } else {

            statusLabel.setStyle(
                    "-fx-background-color: #ECEFF1;" +
                            "-fx-text-fill: #546E7A;" +
                            "-fx-padding: 4 10;" +
                            "-fx-background-radius: 6;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;"
            );
        }
    }




    public void setDetailButtonClick() {
        detailsButton.setOnAction(e -> {
            try {
                // Đường dẫn tới FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/btl_oop/FXML/Order/order-summary.fxml"));
                Parent root = loader.load();

                // Lấy controller và truyền dữ liệu
                com.btl_oop.Controller.Order.OrderSummaryController summaryController = loader.getController();

                if (summaryController != null) {
                    summaryController.loadFromOrderItem(orderIdValue, statusValue, customerNameValue, totalPriceValue);
                }

                // Mở cửa sổ mới
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Order Summary - " + (orderIdValue != null ? orderIdValue : ""));
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException ioEx) {
                System.err.println("Failed to open Order Summary: " + ioEx.getMessage());
                ioEx.printStackTrace();
            } catch (Exception ex) {
                System.err.println("Error while opening Order Summary: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
}