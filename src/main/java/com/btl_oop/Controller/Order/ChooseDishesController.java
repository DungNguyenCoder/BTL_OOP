package com.btl_oop.Controller.Order;

import com.btl_oop.Model.DAO.DishDAO;
import com.btl_oop.Model.Entity.Dish;
import com.btl_oop.Model.Enum.Category;
import com.btl_oop.Utils.AppConfig;
import com.btl_oop.Utils.CategoryDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import java.util.HashMap;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.lang.reflect.Type;

public class ChooseDishesController {

    // Header
    @FXML private Label tableLabel;
    @FXML private TextField searchField;
    @FXML private Button btnBack;

    // Category buttons
    @FXML private Button btnSnack;
    @FXML private Button btnMeal;
    @FXML private Button btnVegan;
    @FXML private Button btnDessert;
    @FXML private Button btnDrink;

    // Category counts
    @FXML private Label lblAppetizersCount;
    @FXML private Label lblSoupsCount;
    @FXML private Label lblSaladsCount;
    @FXML private Label lblBurgersCount;
    @FXML private Label lblSteaksCount;

    // Center content
    @FXML private ImageView currentCategoryIcon;
    @FXML private Label categoryTitle;
    @FXML private Label itemsAvailable;
    @FXML private GridPane dishesGrid;

    // Order Summary
    @FXML private AnchorPane orderSummary;
    @FXML private OrderSummaryController orderSummaryController;

    private Button selectedButton = null;
    private List<Dish> allDishes;
    private Map<String, CategoryInfo> categoryMap = new HashMap<>();

    @FXML
    private void initialize() {
        System.out.println("ChooseDishesController initialized");

        // Load hết dish từ Json
        allDishes = loadDishesFromJson();
        System.out.println("Loaded " + allDishes.size() + " dishes");

        // Init logo Category theo btn mình chuyển
        initializeCategoryMap();

        // Update số lượng món theo Category
        updateCategoryCounts();

        // Màn hình default khi vào order là Snack
        handleCategoryClick(btnSnack, "Snack", "/com/btl_oop/img/ic_item/ic_snack.png");

        // Thanh tìm kiếm
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDishes(newValue);
        });

        if (orderSummaryController != null) {
            orderSummaryController.setParentController(this);
            System.out.println("OrderSummaryController successfully injected!");
        } else {
            System.err.println("WARNING: OrderSummaryController is NULL!");
        }
    }

    @FXML
    public void hideOrderSummary() {
        orderSummary.setVisible(false);
        orderSummary.setManaged(false);
        System.out.println("Order Summary hidden");
    }

    private void initializeCategoryMap() {
        categoryMap.put("Snack", new CategoryInfo("Snack", "/com/btl_oop/img/ic_item/ic_snack.png", lblAppetizersCount));
        categoryMap.put("Meal", new CategoryInfo("Meal", "/com/btl_oop/img/ic_item/ic_meal.png", lblSoupsCount));
        categoryMap.put("Vegan", new CategoryInfo("Vegan", "/com/btl_oop/img/ic_item/ic_vegan.png", lblSaladsCount));
        categoryMap.put("Dessert", new CategoryInfo("Dessert", "/com/btl_oop/img/ic_item/ic_dessert.png", lblBurgersCount));
        categoryMap.put("Drink", new CategoryInfo("Drink", "/com/btl_oop/img/ic_item/ic_drink.png", lblSteaksCount));
    }

    private void updateCategoryCounts() {
        for (Map.Entry<String, CategoryInfo> entry : categoryMap.entrySet()) {
            String category = entry.getKey();
            CategoryInfo info = entry.getValue();

            long count = allDishes.stream()
                    .filter(d -> {
                        if (d.getCategory() == null) return false;
                        return category.equalsIgnoreCase(d.getCategory());
                    })
                    .count();

            info.countLabel.setText(count + " items");
        }
    }

    @FXML
    private void filterByCategory(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String categoryName = clickedButton.getId().replace("btn", ""); // btnSnack -> Snack

        CategoryInfo info = categoryMap.get(categoryName);
        if (info != null) {
            handleCategoryClick(clickedButton, categoryName, info.iconPath);
        }
    }

    private void handleCategoryClick(Button clickedButton, String categoryName, String iconPath) {
        if (selectedButton != null) {
            selectedButton.getStyleClass().remove("category-active");
        }

        if (!clickedButton.getStyleClass().contains("category-active")) {
            clickedButton.getStyleClass().add("category-active");
        }

        selectedButton = clickedButton;

        // Update header
        CategoryInfo info = categoryMap.get(categoryName);
        if (info != null) {
            categoryTitle.setText(info.displayName);

            // Update current category icon
            try {
                Image image = new Image(getClass().getResourceAsStream(iconPath));
                currentCategoryIcon.setImage(image);
            } catch (Exception e) {
                System.err.println("Failed to load icon: " + iconPath);
                e.printStackTrace();
            }
        }

        loadCategory(categoryName);
    }

    private void loadCategory(String categoryName) {
        System.out.println("Loading category: " + categoryName);

        dishesGrid.getChildren().clear();

        List<Dish> filtered = allDishes.stream()
                .filter(d -> {
                    if (d.getCategory() == null) return false;
                    return categoryName.equalsIgnoreCase(d.getCategory());
                })
                .toList();

        System.out.println("Found " + filtered.size() + " dishes in category " + categoryName);

        itemsAvailable.setText(filtered.size() + " items available");

        int row = 0;
        int col = 0;
        for (Dish dish : filtered) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.PATH_DISH_ITEM_PANEL));
                Node dishNode = loader.load();

                DishItemController controller = loader.getController();
                controller.setData(dish);
                controller.setParentController(this);

                dishesGrid.add(dishNode, col, row);

                col++;
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            } catch (IOException e) {
                System.err.println("Error loading dish item: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void searchDishes(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            if (selectedButton != null) {
                String categoryName = selectedButton.getId().replace("btn", "");
                loadCategory(categoryName);
            }
            return;
        }

        dishesGrid.getChildren().clear();

        String searchLower = searchText.toLowerCase().trim();
        List<Dish> filtered = allDishes.stream()
                .filter(d -> d.getName().toLowerCase().contains(searchLower)
                        || d.getDescription().toLowerCase().contains(searchLower))
                .toList();

        itemsAvailable.setText(filtered.size() + " items found");

        int row = 0;
        int col = 0;
        for (Dish dish : filtered) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(AppConfig.PATH_DISH_ITEM_PANEL));
                Node dishNode = loader.load();

                DishItemController controller = loader.getController();
                controller.setData(dish);
                controller.setParentController(this);

                dishesGrid.add(dishNode, col, row);

                col++;
                if (col >= 3) {
                    col = 0;
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Dish> loadDishesFromJson() {
        System.out.println("=== LOADING DISHES ===");
        System.out.println("Path: " + AppConfig.PATH_DISHES_DATA);

        try {
            java.net.URL resourceUrl = getClass().getResource(AppConfig.PATH_DISHES_DATA);
            if (resourceUrl == null) {
                System.err.println("ERROR: File not found at path: " + AppConfig.PATH_DISHES_DATA);
                return List.of();
            }

            System.out.println("Resource URL found: " + resourceUrl);

            try (InputStreamReader reader = new InputStreamReader(
                    getClass().getResourceAsStream(AppConfig.PATH_DISHES_DATA),
                    StandardCharsets.UTF_8)) {

                // Tạo Gson với CategoryDeserializer
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Category.class, new CategoryDeserializer())
                        .setPrettyPrinting()
                        .create();

                Type listType = new TypeToken<List<Dish>>(){}.getType();
                List<Dish> dishes = gson.fromJson(reader, listType);

                if (dishes == null || dishes.isEmpty()) {
                    System.err.println("⚠WARNING: File loaded but contains no dishes");
                    return List.of();
                }

                System.out.println("Successfully loaded " + dishes.size() + " dishes");

                System.out.println("\n📋 Sample dishes:");
                for (int i = 0; i < Math.min(5, dishes.size()); i++) {
                    Dish dish = dishes.get(i);
                    System.out.println(String.format("  %d. %s | %s | $%.2f | %dmin",
                            i + 1,
                            dish.getName(),
                            dish.getCategory() != null ? dish.getCategory() : "NULL",
                            dish.getPrice(),
                            dish.getPrepareTime()
                    ));
                }
                System.out.println();

                return dishes;

            } catch (JsonSyntaxException e) {
                System.err.println("ERROR parsing JSON: " + e.getMessage());
                e.printStackTrace();
                return List.of();
            }
        } catch (Exception e) {
            System.err.println("ERROR loading dishes: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public void addToOrder(Dish dish, int quantity) {
        System.out.println("addToOrder called! Dish: " + dish.getName() + ", Qty: " + quantity);

        if (!orderSummary.isVisible()) {
            orderSummary.setVisible(true);
            orderSummary.setManaged(true);
            System.out.println("Order Summary is now visible!");
        }

        if (orderSummaryController != null) {
            orderSummaryController.addDish(dish, quantity);
            System.out.println("Dish added to OrderSummaryController");
        } else {
            System.err.println("ERROR: OrderSummaryController is NULL!");
        }
    }

    @FXML
    private void goBack() throws IOException {
        System.out.println("Going back...");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/btl_oop/FXML/layout_login/login-screen.fxml"));
        Parent loginScreen = loader.load();

        Stage currentStage = (Stage)btnBack.getScene().getWindow();

        Scene loginScene = new Scene(loginScreen);

        currentStage.setScene(loginScene);
    }

    private static class CategoryInfo {
        String displayName;
        String iconPath;
        Label countLabel;

        CategoryInfo(String displayName, String iconPath, Label countLabel) {
            this.displayName = displayName;
            this.iconPath = iconPath;
            this.countLabel = countLabel;
        }
    }

}

