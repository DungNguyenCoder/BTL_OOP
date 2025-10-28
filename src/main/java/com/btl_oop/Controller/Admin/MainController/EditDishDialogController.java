package com.btl_oop.Controller.Admin.MainController;

import com.btl_oop.Model.Entity.Dish;
import com.btl_oop.Model.Enum.Category;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class EditDishDialogController {

    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField priceField;
    @FXML private TextArea descriptionArea;
    @FXML private ImageView previewImage;

    private Dish dishToEdit;
    private Dish updatedDish;
    private String selectedImageUrl;

    public Dish getUpdatedDish() {
        return updatedDish;
    }

    public void setDish(Dish dish) {
        this.dishToEdit = dish;
        populateFields();
    }

    @FXML
    private void initialize() {
        descriptionArea.setPromptText("");
    }

    private void populateFields() {
        if (dishToEdit == null) return;

        nameField.setText(dishToEdit.getName());
        categoryField.setText(dishToEdit.getCategory());
        priceField.setText(String.valueOf(dishToEdit.getPrice()));
        descriptionArea.setText(dishToEdit.getDescription());

        selectedImageUrl = dishToEdit.getImageUrl();
        loadImage(selectedImageUrl);
    }

    private void loadImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            previewImage.setImage(null);
            return;
        }

        try {
            if (imageUrl.startsWith("file:") ||
                    imageUrl.startsWith("http://") ||
                    imageUrl.startsWith("https://")) {
                previewImage.setImage(new Image(imageUrl, false));
                return;
            }

            String resourcePath = imageUrl.startsWith("/") ? imageUrl : "/" + imageUrl;
            var inputStream = getClass().getResourceAsStream(resourcePath);

            if (inputStream != null) {
                previewImage.setImage(new Image(inputStream));
            } else {
                previewImage.setImage(null);
            }
        } catch (Exception e) {
            System.err.println("✗ Error loading image: " + imageUrl);
            e.printStackTrace();
            previewImage.setImage(null);
        }
    }

    @FXML
    private void handleSelectImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select dish image");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        File file = chooser.showOpenDialog(getStage());
        if (file != null) {
            selectedImageUrl = file.toURI().toString();
            previewImage.setImage(new Image(selectedImageUrl, false));
            System.out.println("Selected new image: " + selectedImageUrl);
        }
    }

    @FXML
    private void handleUpdate() {
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String category = categoryField.getText() != null ? categoryField.getText().trim() : "";
        String priceText = priceField.getText() != null ? priceField.getText().trim() : "";
        String description = descriptionArea.getText() != null ? descriptionArea.getText().trim() : "";

        if (name.isEmpty()) {
            showError("Please enter dish name.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price < 0) throw new NumberFormatException("negative");
        } catch (Exception e) {
            showError("Price is invalid. Please enter a valid number.");
            return;
        }

        String choice;
        switch (category.toLowerCase()) {
            case "snack" -> choice = Category.SNACK.getDisplayName();
            case "meal" -> choice = Category.MEAL.getDisplayName();
            case "vegan" -> choice = Category.VEGAN.getDisplayName();
            case "dessert" -> choice = Category.DESSERT.getDisplayName();
            case "drink" -> choice = Category.DRINK.getDisplayName();
            default -> choice = category; // Keep original if not matched
        }

        // Create updated dish with same ID
        updatedDish = new Dish(
                dishToEdit.getDishId(),
                name,
                price,
                description,
                dishToEdit.getPrepareTime(),
                choice,
                selectedImageUrl,
                dishToEdit.isPopular()
        );

        closeSelf();
    }

    @FXML
    private void handleCancel() {
        updatedDish = null;
        closeSelf();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setHeaderText(null);
        alert.setTitle("Validation Error");
        alert.initOwner(getStage());
        alert.showAndWait();
    }

    private void closeSelf() {
        Stage stage = getStage();
        if (stage != null) stage.close();
    }

    private Stage getStage() {
        if (nameField == null || nameField.getScene() == null) return null;
        return (Stage) nameField.getScene().getWindow();
    }
}