package com.btl_oop.Controller.Order;

import com.btl_oop.Model.Entity.Dish;
import com.btl_oop.Model.Entity.Order;
import com.btl_oop.Model.Entity.OrderItem;
import com.btl_oop.Utils.AppConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderSummaryController {
    @FXML private TextField tableNumberField;
    @FXML private VBox orderItemsList;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private Button reviewOrderButton;
    @FXML private ImageView closeButton;

    private final List<OrderItem> items = new ArrayList<>();
    private final Map<String, OrderItemUI> itemUIMap = new HashMap<>(); // Key = dish name

    private double subtotal = 0;
    private double tax = 0;
    private double total = 0;

    private com.btl_oop.Controller.Order.ChooseDishesController parentController;

    public void setParentController(com.btl_oop.Controller.Order.ChooseDishesController controller) {
        this.parentController = controller;
    }

    @FXML
    private void initialize() {
        System.out.println("OrderSummaryController initialized");

        if (closeButton != null) {
            closeButton.setOnMouseEntered(e -> closeButton.setOpacity(0.7));
            closeButton.setOnMouseExited(e -> closeButton.setOpacity(1.0));
        }
    }

    public void addDish(Dish dish, int quantity) {
        System.out.println("OrderSummaryController.addDish() called: " + dish.getName() + " x " + quantity);

        // Find existing item by name
        OrderItem existingItem = null;
        for (OrderItem i : items) {
            if (i.getDish().getName().equals(dish.getName())) { // So sánh bằng name
                existingItem = i;
                break;
            }
        }

        if (existingItem != null) {
            System.out.println("Item exists, updating quantity from " + existingItem.getQuantity() + " to " + (existingItem.getQuantity() + quantity));
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            updateItemUI(existingItem);
        } else {
            System.out.println("New item, creating UI");
            OrderItem newItem = new OrderItem(dish, quantity);
            items.add(newItem);
            createItemUI(newItem);
        }

        updateTotals();
    }

    private void createItemUI(OrderItem item) {
        VBox itemBox = new VBox(5);
        itemBox.setStyle(
                "-fx-background-color: #f8f8f8; " +
                        "-fx-padding: 10; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 8; " +
                        "-fx-border-width: 1;"
        );

        Label nameLabel = new Label(item.getDish().getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1a1a1a;");
        nameLabel.setWrapText(true);

        Label priceEachLabel = new Label(String.format("$%.2f × %d",
                item.getDish().getPrice(),
                item.getQuantity()
        ));
        priceEachLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        HBox controlBox = new HBox(10);
        controlBox.setAlignment(Pos.CENTER_LEFT);
        controlBox.setStyle("-fx-padding: 5 0 0 0;");

        Button minusBtn = new Button("−");
        minusBtn.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4; " +
                        "-fx-background-radius: 4; " +
                        "-fx-padding: 5 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold;"
        );
        minusBtn.setOnAction(e -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                updateItemUI(item);
                updateTotals();
            } else {
                removeItem(item);
            }
        });

        Label qtyLabel = new Label(String.valueOf(item.getQuantity()));
        qtyLabel.setStyle(
                "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px; " +
                        "-fx-min-width: 25; " +
                        "-fx-alignment: center;"
        );

        Button plusBtn = new Button("+");
        plusBtn.setStyle(
                "-fx-background-color: white; " +
                        "-fx-border-color: #e0e0e0; " +
                        "-fx-border-radius: 4; " +
                        "-fx-background-radius: 4; " +
                        "-fx-padding: 5 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold;"
        );
        plusBtn.setOnAction(e -> {
            item.setQuantity(item.getQuantity() + 1);
            updateItemUI(item);
            updateTotals();
        });

        Label totalPriceLabel = new Label(String.format("$%.2f",
                item.getDish().getPrice() * item.getQuantity()
        ));
        totalPriceLabel.setStyle(
                "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: #1a1a1a;"
        );
        HBox.setHgrow(totalPriceLabel, Priority.ALWAYS);
        totalPriceLabel.setMaxWidth(Double.MAX_VALUE);
        totalPriceLabel.setAlignment(Pos.CENTER_RIGHT);

        controlBox.getChildren().addAll(minusBtn, qtyLabel, plusBtn, totalPriceLabel);

        itemBox.getChildren().addAll(nameLabel, priceEachLabel, controlBox);

        OrderItemUI itemUI = new OrderItemUI(itemBox, qtyLabel, priceEachLabel, totalPriceLabel);
        itemUIMap.put(item.getDish().getName(), itemUI); // Key = name

        orderItemsList.getChildren().add(itemBox);

        System.out.println("Item UI created and added to orderList for: " + item.getDish().getName());
    }

    private void updateItemUI(OrderItem item) {
        String dishName = item.getDish().getName();
        OrderItemUI itemUI = itemUIMap.get(dishName);

        if (itemUI != null) {
            itemUI.quantityLabel.setText(String.valueOf(item.getQuantity()));
            itemUI.priceEachLabel.setText(String.format("$%.2f × %d",
                    item.getDish().getPrice(),
                    item.getQuantity()
            ));
            itemUI.totalPriceLabel.setText(String.format("$%.2f",
                    item.getDish().getPrice() * item.getQuantity()
            ));
            System.out.println("Updated UI for: " + dishName + ", new quantity: " + item.getQuantity());
        } else {
            System.err.println("WARNING: Could not find UI for dish: " + dishName);
        }
    }

    private void removeItem(OrderItem item) {
        String dishName = item.getDish().getName();
        OrderItemUI itemUI = itemUIMap.get(dishName);

        if (itemUI != null) {
            orderItemsList.getChildren().remove(itemUI.itemBox);
            itemUIMap.remove(dishName);
        }

        items.remove(item);
        updateTotals();

        System.out.println("Item removed: " + dishName);
    }

    private void updateTotals() {
        subtotal = 0;
        for (OrderItem item : items) {
            subtotal += item.getDish().getPrice() * item.getQuantity();
        }

        tax = subtotal * 0.1;
        total = subtotal + tax;

        subtotalLabel.setText(String.format("$%.2f", subtotal));
        taxLabel.setText(String.format("$%.2f", tax));
        totalLabel.setText(String.format("$%.2f", total));

        System.out.println("Totals updated - Items: " + items.size() +
                ", Subtotal: $" + String.format("%.2f", subtotal) +
                ", Tax: $" + String.format("%.2f", tax) +
                ", Total: $" + String.format("%.2f", total));
    }

    @FXML
    private void closeSummary() {
        System.out.println("Closing Order Summary");

        if (parentController != null) {
            parentController.hideOrderSummary();
        } else {
            orderItemsList.getScene().getWindow().hide();
        }
    }

    @FXML
    public void clearAll() {
        items.clear();
        itemUIMap.clear();
        orderItemsList.getChildren().clear();
        updateTotals();
        System.out.println("All items cleared!");
    }

    @FXML
    private void confirmOrder() {
        if (items.isEmpty()) {
            System.out.println("No items in order!");
            // TODO: Show alert to user
            return;
        }

        try {
            int tableNumber;
            try {
                String tableText = tableNumberField.getText().trim();
                if (tableText.isEmpty()) {
                    System.out.println("Please enter a table number!");
                    // TODO: Show alert to user
                    return;
                }
                tableNumber = Integer.parseInt(tableText);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid table number!");
                // TODO: Show alert to user
                return;
            }

            String timestamp = java.time.LocalDateTime.now().toString();

            Order order = new Order(tableNumber, new ArrayList<>(items), subtotal, tax, total, timestamp);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String orderJson = gson.toJson(order);

            System.out.println("Order packaged:");
            System.out.println(orderJson);

            sendToManager(orderJson);

            clearAll();
            tableNumberField.clear();

            System.out.println("Order confirmed and cleared!");
            // TODO: Show success alert to user

        } catch (Exception e) {
            System.err.println("Error confirming order: " + e.getMessage());
            e.printStackTrace();
            // TODO: Show error alert to user
        }
    }

    private void sendToManager(String orderJson) {
        try {
            File file = new File(AppConfig.PATH_ORDERS_DATA);

            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                System.out.println("Created directories: " + created);
            }

            if (!file.exists()) {
                boolean created = file.createNewFile();
                System.out.println("Created file: " + created);
            }

            FileWriter fw = new FileWriter(file, true);
            fw.write(orderJson + "\n");
            fw.write("---\n");
            fw.close();

            System.out.println("Order saved to: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class OrderItemUI {
        VBox itemBox;
        Label quantityLabel;
        Label priceEachLabel;
        Label totalPriceLabel;

        OrderItemUI(VBox itemBox, Label quantityLabel, Label priceEachLabel, Label totalPriceLabel) {
            this.itemBox = itemBox;
            this.quantityLabel = quantityLabel;
            this.priceEachLabel = priceEachLabel;
            this.totalPriceLabel = totalPriceLabel;
        }
    }
}