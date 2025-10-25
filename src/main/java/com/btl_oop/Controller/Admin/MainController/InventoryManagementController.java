package com.btl_oop.Controller.Admin.MainController;

import com.btl_oop.Controller.Admin.ComponentController.ProductCardController;
import com.btl_oop.Model.Entity.Dish;
import com.btl_oop.Model.Enum.Category;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryManagementController {

    @FXML private FlowPane popularPane;
    @FXML private FlowPane allFoodsPane;
    @FXML private FlowPane categoriesPane;

    private List<Dish> allDishes;
    private Category selectedCategory = null;
    private Map<Category, VBox> categoryCards = new HashMap<>();

    @FXML
    private void initialize() {
        initializeDishes();

        initializeCategoryCards();

        refreshDishDisplay();
    }

    private void initializeDishes() {
        allDishes = new ArrayList<>();

        allDishes.add(new Dish("Chocolate Brownie", 15.00,
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
                10, Category.DESSERT.getDisplayName(), "/com/btl_oop/img/img/product_brownie.png"));

        allDishes.add(new Dish("Burger", 10.00,
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
                8, Category.MEAL.getDisplayName(), "/com/btl_oop/img/img/product_burger.png"));

        allDishes.add(new Dish("Macarons", 12.00,
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
                5, Category.DESSERT.getDisplayName(), "/com/btl_oop/img/img/product_macarons.png"));

        allDishes.add(new Dish("Strawberry Cake", 15.00,
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
                10, Category.DESSERT.getDisplayName(), "/com/btl_oop/img/img/product_brownie_pink.png"));

        allDishes.add(new Dish("Chocolate Cake", 10.00,
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
                12, Category.DESSERT.getDisplayName(), "/com/btl_oop/img/img/product_cake.png"));

        allDishes.add(new Dish("Mojito", 12.00,
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
                2, Category.DRINK.getDisplayName(), "/com/btl_oop/img/img/product_drink.png"));

        allDishes.add(new Dish("Nachos", 8.00,
                "Crispy tortilla chips with cheese and salsa.",
                5, Category.SNACK.getDisplayName(), "/com/btl_oop/img/img/product_brownie.png"));

        allDishes.add(new Dish("Vegan Salad", 11.00,
                "Fresh vegetable salad with olive oil.",
                7, Category.VEGAN.getDisplayName(), "/com/btl_oop/img/img/product_burger.png"));
    }

    private void initializeCategoryCards() {
        Map<Category, String> categoryImages = Map.of(
                Category.SNACK, "/com/btl_oop/img/category_item/category_snack.png",
                Category.MEAL, "/com/btl_oop/img/category_item/category_meal.png",
                Category.VEGAN, "/com/btl_oop/img/category_item/category_vegan.png",
                Category.DESSERT, "/com/btl_oop/img/category_item/category_dessert.png",
                Category.DRINK, "/com/btl_oop/img/category_item/category_drink.png"
        );

        for (Category category : Category.values()) {
            VBox categoryCard = createCategoryCard(category, categoryImages.get(category));
            categoryCards.put(category, categoryCard);
            categoriesPane.getChildren().add(categoryCard);
        }
    }

    private VBox createCategoryCard(Category category, String imagePath) {
        VBox card = new VBox();
        card.getStyleClass().add("category-card");

        ImageView imageView = new ImageView();
        imageView.setFitHeight(120);
        imageView.setFitWidth(120);
        imageView.setPreserveRatio(false);

        try {
            var is = getClass().getResourceAsStream(imagePath);
            if (is != null) {
                imageView.setImage(new Image(is));
            }
        } catch (Exception e) {
            System.err.println("Cannot load category image: " + imagePath);
        }

        Label label = new Label(category.getDisplayName());
        label.getStyleClass().add("category-name");

        card.getChildren().addAll(imageView, label);

        card.setOnMouseClicked(event -> handleCategoryClick(category));

        return card;
    }

    private void handleCategoryClick(Category category) {
        if (selectedCategory == category) {
            selectedCategory = null;
            clearCategorySelection();
        } else {
            selectedCategory = category;
            updateCategorySelection(category);
        }
        refreshDishDisplay();
    }

    private void updateCategorySelection(Category category) {
        categoryCards.values().forEach(card -> card.getStyleClass().remove("active"));
        VBox selectedCard = categoryCards.get(category);
        if (selectedCard != null && !selectedCard.getStyleClass().contains("active")) {
            selectedCard.getStyleClass().add("active");
        }
    }

    private void clearCategorySelection() {
        categoryCards.values().forEach(card -> card.getStyleClass().remove("active"));
    }

    private void refreshDishDisplay() {
        popularPane.getChildren().clear();
        allFoodsPane.getChildren().clear();

        List<Dish> filteredDishes;
        if (selectedCategory == null) {
            filteredDishes = allDishes;
        } else {
//            filteredDishes = allDishes.stream()
//                    .filter(dish -> dish.getCategory() == selectedCategory)
//                    .collect(Collectors.toList());
        }

//        filteredDishes.stream()
//                .filter(Dish::isPopular)
//                .forEach(dish -> addDishToPane(dish, popularPane));

//        filteredDishes.forEach(dish -> addDishToPane(dish, allFoodsPane));
    }

    @FXML
    private void handleAddNew() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/btl_oop/FXML/Admin/layout_inside/add_dish_dialog.fxml"));
            Parent root = loader.load();

            AddDishDialogController controller = loader.getController();

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Add New Inventory");
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            Dish created = controller.getCreatedDish();
            if (created != null) {
                allDishes.add(created);
                refreshDishDisplay();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addDishToPane(Dish dish, FlowPane targetPane) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/btl_oop/FXML/Admin/components/product_card.fxml"));
            VBox cardRoot = loader.load();
            ProductCardController controller = loader.getController();
            controller.setData(dish);
            targetPane.getChildren().add(cardRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}