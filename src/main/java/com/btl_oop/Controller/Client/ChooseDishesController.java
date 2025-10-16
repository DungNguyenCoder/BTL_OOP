package com.btl_oop.Controller.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Insets;

public class ChooseDishesController {
    @FXML
    private VBox appetizersPane;
    @FXML
    private VBox soupsPane;
    @FXML
    private VBox saladsPane;
    @FXML
    private Button btnAppetizers, btnSoups, btnSalads, logoutBtn;
    @FXML
    TilePane contentArea, contentArea1, contentArea2;

    @FXML
    private void initialize() {
        appetizersPane.setVisible(false);
        soupsPane.setVisible(false);
        saladsPane.setVisible(false);
        btnAppetizers.setOnAction(e -> loadAppetizers());
        btnSoups.setOnAction(e -> loadSoups());
        btnSalads.setOnAction(e -> loadSalads());
        logoutBtn.setOnAction(e -> exitApp());
    }

    private void exitApp() {
        System.out.println("EXIT SUCCESSFULLY");
        System.exit(0);
    }

    private void loadAppetizers() {
        resetAllButtonStyles();
        showPane(appetizersPane);
        System.out.println("Loading appetizers");
        contentArea.getChildren().clear();
        for (int i = 1; i <= 6; i++) {
            VBox itemBox = createItem("Appetizer " + i, "12000đ");
            contentArea.getChildren().add(itemBox);
        }
        btnAppetizers.setStyle("-fx-background-color: #000000; -fx-text-fill: white;");
    }

    private void loadSoups() {
        resetAllButtonStyles();
        showPane(soupsPane);
        System.out.println("Loading soups");
        contentArea1.getChildren().clear();
        for (int i = 1; i <= 6; i++) {
            contentArea1.getChildren().add(createItem("Soup " + i, "15000đ"));
        }
        btnSoups.setStyle("-fx-background-color: #000000; -fx-text-fill: white;");
    }

    private void loadSalads() {
        resetAllButtonStyles();
        showPane(saladsPane);
        System.out.println("Loading salads");
        contentArea2.getChildren().clear();
        for (int i = 1; i <= 6; i++) {
            contentArea2.getChildren().add(createItem("Salad " + i, "10000đ"));
        }
        btnSalads.setStyle("-fx-background-color: #000000; -fx-text-fill: white;");
    }

    private VBox createItem(String name, String price) {
        VBox itemBox = new VBox(10);
        itemBox.setMaxWidth(Double.MAX_VALUE);
        itemBox.setStyle(
                "-fx-background-color: #f9f9f9;" +
                        "-fx-border-color: #ccc;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-padding: 20;" +
                        "-fx-alignment: center;" +
                        "-fx-border-radius: 10px;"
        );
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        Label priceLabel = new Label(price);
        priceLabel.setStyle("-fx-text-fill: #2a8a2a; -fx-font-size: 14;");

        Button priceButton = new Button("+ Add to order");
        priceButton.getStyleClass().add("center-tiles-item-button");
        itemBox.getChildren().addAll(nameLabel, priceLabel, priceButton);

        TilePane.setMargin(itemBox, new Insets(10));

        return itemBox;
    }


    private void resetAllButtonStyles() {
        btnAppetizers.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: black;");
        btnSoups.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: black;");
        btnSalads.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: black;");
    }

    private void showPane(VBox pane) {
        appetizersPane.setVisible(false);
        soupsPane.setVisible(false);
        saladsPane.setVisible(false);
        pane.setVisible(true);
    }
}
