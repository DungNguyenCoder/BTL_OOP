package com.btl_oop.Controller.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ChooseDishesController {
    @FXML private VBox appetizersPane;
    @FXML private VBox soupsPane;
    @FXML private VBox saladsPane;
    @FXML private VBox burgersPane;
    @FXML private VBox steaksPane;
    @FXML private VBox chickenPane;
    @FXML private VBox seafoodPane;
    @FXML private VBox pastaPane;
    @FXML private VBox vegetarianPane;
    @FXML private VBox sidesPane;
    @FXML private VBox dessertsPane;

    @FXML private Button btnAppetizers, btnSoups, btnSalads, btnBurgers, btnSteaks, btnChicken,
            btnSeafood, btnPasta, btnVegetarian, btnSides, btnDessert;

    private Button selectedButton = null;

    private void hideAll() {
        appetizersPane.setVisible(false);
        soupsPane.setVisible(false);
        saladsPane.setVisible(false);
        burgersPane.setVisible(false);
        steaksPane.setVisible(false);
        chickenPane.setVisible(false);
        seafoodPane.setVisible(false);
        pastaPane.setVisible(false);
        vegetarianPane.setVisible(false);
        sidesPane.setVisible(false);
        dessertsPane.setVisible(false);
    }

    @FXML
    private void handleCategoryClick(ActionEvent event) {
        Button clicked = (Button) event.getSource();

        if (selectedButton != null)
            selectedButton.getStyleClass().remove("category-item-selected");

        if (!clicked.getStyleClass().contains("category-item-selected"))
            clicked.getStyleClass().add("category-item-selected");

        selectedButton = clicked;


        hideAll();
        if (clicked == btnAppetizers)      appetizersPane.setVisible(true);
        else if (clicked == btnSoups)       soupsPane.setVisible(true);
        else if (clicked == btnSalads)      saladsPane.setVisible(true);
        else if (clicked == btnBurgers)    burgersPane.setVisible(true);
        else if (clicked == btnSteaks)     steaksPane.setVisible(true);
        else if (clicked == btnChicken)    chickenPane.setVisible(true);
        else if (clicked == btnSeafood)    seafoodPane.setVisible(true);
        else if (clicked == btnPasta)      pastaPane.setVisible(true);
        else if (clicked == btnVegetarian) vegetarianPane.setVisible(true);
        else if (clicked == btnSides)      sidesPane.setVisible(true);
        else if (clicked == btnDessert)   dessertsPane.setVisible(true);
    }

    @FXML
    private void initialize() {
        hideAll();
        appetizersPane.setVisible(true);
        selectedButton = btnAppetizers;
        selectedButton.getStyleClass().add("category-item-selected");
    }
}
