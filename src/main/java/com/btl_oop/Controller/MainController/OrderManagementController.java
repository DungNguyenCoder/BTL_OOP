package com.btl_oop.Controller.MainController;

import com.btl_oop.Controller.ComponentController.OrderItemController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class OrderManagementController {

    @FXML
    private VBox orderContainer;


    @FXML
    public void initialize() {
        System.out.println("DashboardController initialized!");
        System.out.println("orderContainer is null? " + (orderContainer == null));

        if (orderContainer != null) {
            loadSampleOrders();
        } else {
            System.err.println("ERROR: orderContainer is null! Check fx:id in FXML");
        }
    }

    private void loadSampleOrders() {
        addOrder("1001", "Delivered", "Doris Brown", 34.00, "/img/img_login.png");
        addOrder("1002", "Delivered", "DJ Don", 18.00, "/img/img_login.png");
        addOrder("1003", "In Progress", "Sara", 26.00, "/img/img_login.png");
        addOrder("1004", "Delivered", "Yumiko", 39.00, "/img/img_login.png");
        addOrder("1005", "In Progress", "Olivia", 21.00, "/img/img_login.png");
        addOrder("1006", "In Progress", "Tony", 48.00, "/img/img_login.png");
    }

    public void addOrder(String orderId, String status, String customerName,
                         double totalPrice, String imagePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/btl_oop/FXML/components/orderItem.fxml"));
            VBox orderItem = loader.load();

            OrderItemController controller = loader.getController();
            controller.setOrderData(orderId, status, customerName, totalPrice, imagePath);

            controller.setOnDetailsClick(() -> {
                System.out.println("Details clicked for Order: " + orderId);
                showOrderDetails(orderId);
            });

            orderContainer.getChildren().add(orderItem);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load OrderItem.fxml");
        }
    }

    public void clearOrders() {
        orderContainer.getChildren().clear();
    }

    public void removeOrder(int index) {
        if (index >= 0 && index < orderContainer.getChildren().size()) {
            orderContainer.getChildren().remove(index);
        }
    }

    private void showOrderDetails(String orderId) {
        System.out.println("Showing details for order: " + orderId);
    }

    public void addNewOrder(String orderId, String status, String customerName,
                            double totalPrice, String imagePath) {
        addOrder(orderId, status, customerName, totalPrice, imagePath);
    }
}
