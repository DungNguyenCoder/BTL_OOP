package com.btl_oop.Controller.Order;

import com.btl_oop.Model.Entity.Dish;
import com.btl_oop.Model.Entity.Order;
import com.btl_oop.Model.Entity.OrderItem;
import com.btl_oop.Utils.AppConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OrderSummaryController {
    @FXML private TextField tableNumberField;
    @FXML private VBox orderList;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;

    private final List<OrderItem> items = new ArrayList<>();
    private double subtotal = 0;
    private double tax = 0;
    private double total = 0;

    public void addDish(Dish dish, int quantity) {

        for (OrderItem i : items) {
            if (i.getDish().getName().equals(dish.getName())) {
                i.setQuantity(i.getQuantity() + quantity);
                updateUI();
                return;
            }
        }
        items.add(new OrderItem(dish, quantity));
        updateUI();
    }

    public void clearAll() {
        items.clear();
        updateUI();
    }

    private void updateUI() {
        orderList.getChildren().clear();

        subtotal = 0;
        for (OrderItem item : items) {
            Label label = new Label(item.getDish().getName() + " × " + item.getQuantity() +
                    " ($" + String.format("%.2f", item.getDish().getPrice() * item.getQuantity()) + ")");
            orderList.getChildren().add(label);
            subtotal += item.getDish().getPrice() * item.getQuantity();
        }

        tax = subtotal * 0.1;
        total = subtotal + tax;

        subtotalLabel.setText(String.format("$%.2f", subtotal));
        taxLabel.setText(String.format("$%.2f", tax));
        totalLabel.setText(String.format("$%.2f", total));
    }

    @FXML
    private void confirmOrder() {
        try {
            int tableNumber;
            try {
                tableNumber = Integer.parseInt(tableNumberField.getText().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid table number!");
                return;
            }

            String timestamp = java.time.LocalDateTime.now().toString();

            Order order = new Order(tableNumber, new ArrayList<>(items), subtotal, tax, total, timestamp);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String orderJson = gson.toJson(order);

            System.out.println("Order packaged:");
            System.out.println(orderJson);

            sendToManager(orderJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToManager(String orderJson) {
        try {
            File file = new File(AppConfig.PATH_ORDERS_DATA);

            if (!file.exists()) file.createNewFile();

            FileWriter fw = new FileWriter(file, false);
            fw.write(orderJson + "\n");
            fw.close();

            System.out.println("Order saved to orders.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
