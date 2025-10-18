package com.btl_oop.Controller.Order;

import com.btl_oop.Model.Entity.Dish;
import com.btl_oop.Model.Enum.Category;
import com.btl_oop.Utils.AppConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.lang.reflect.Type;

public class ChooseDishesController {

    @FXML private TilePane contentArea;
    @FXML private Label title;
    @FXML private Label availableDish;
    @FXML private AnchorPane orderPanel;
    @FXML private OrderSummaryController orderSummaryController;

    @FXML private Button btnSnack, btnMeal, btnVegan, btnDessert, btnDrink;

    private Button selectedButton = null;

    @FXML
    private void initialize() {
        handleCategoryClick(btnSnack, "Snack");
        btnSnack.setOnAction(e -> handleCategoryClick(btnSnack, "Snack"));
        btnMeal.setOnAction(e -> handleCategoryClick(btnMeal, "Meal"));
        btnVegan.setOnAction(e -> handleCategoryClick(btnVegan, "Vegan"));
        btnDessert.setOnAction(e -> handleCategoryClick(btnDessert, "Dessert"));
        btnDrink.setOnAction(e -> handleCategoryClick(btnDrink, "Drink"));
    }

    private void handleCategoryClick(Button clickedButton, String categoryName) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("category-item-selected");
        }

        if (!clickedButton.getStyleClass().contains("category-item-selected")) {
            clickedButton.getStyleClass().add("category-item-selected");
        }

        selectedButton = clickedButton;

        title.setText(categoryName);

        loadCategory(categoryName);
    }

    private void loadCategory(String categoryName) {
        System.out.println("Loading " + categoryName + "...");

        contentArea.getChildren().clear();
        List<Dish> allDishes = loadDishesFromJson();

        List<Dish> filtered = allDishes.stream()
                .filter(d -> {
                    String cat = "Snack";
                    if (d.getCategory() != null) {
                        cat = d.getCategory().getDisplayName(); // enum -> String
                    }
                    return categoryName.equalsIgnoreCase(cat);
                })
                .toList();
        String tmp = filtered + " items available";
        availableDish.setText(tmp);
        for (Dish dish : filtered) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.PATH_DISH_ITEM_PANEL));
                AnchorPane dishNode = loader.load();

                DishItemController controller = loader.getController();
                controller.setData(dish);
                controller.setParentController(this);

                contentArea.getChildren().add(dishNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        availableDish.setText(filtered.size() + " items available");
    }

    private List<Dish> loadDishesFromJson() {
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(AppConfig.PATH_DISHES_DATA), StandardCharsets.UTF_8)) {

            Type listType = new TypeToken<List<Dish>>(){}.getType();
            return new Gson().fromJson(reader, listType);

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void addToOrder(Dish dish, int quantity) {
        orderPanel.setVisible(true);
        orderPanel.setManaged(true);
        orderSummaryController.addDish(dish, quantity);
    }
}
