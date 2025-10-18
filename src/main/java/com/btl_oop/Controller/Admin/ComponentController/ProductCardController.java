package com.btl_oop.Controller.Admin.ComponentController;

import com.btl_oop.Model.Entity.Dish;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProductCardController {

    @FXML private ImageView imageView;
    @FXML private Label nameLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label priceLabel;

    private Dish dish;

    public void setData(Dish dish) {
        this.dish = dish;
        nameLabel.setText(dish.getName());
        descriptionLabel.setText(dish.getDescription());
        priceLabel.setText(String.format("$%.2f", dish.getPrice()));
        loadImage(dish.getImageUrl());
    }

    private void loadImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImage(null);
            return;
        }

        try {
            if (imageUrl.startsWith("file:")) {
                imageView.setImage(new Image(imageUrl, false));
                return;
            }

            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                imageView.setImage(new Image(imageUrl, false));
                return;
            }

            String resourcePath = imageUrl.startsWith("/") ? imageUrl : "/" + imageUrl;

            var inputStream = getClass().getResourceAsStream(resourcePath);

            if (inputStream != null) {
                imageView.setImage(new Image(inputStream));
                System.out.println("✓ Loaded image: " + resourcePath);
            } else {
                System.err.println("✗ Cannot find image: " + resourcePath);
                imageView.setImage(null);
            }

        } catch (Exception e) {
            System.err.println("✗ Error loading image: " + imageUrl);
            e.printStackTrace();
            imageView.setImage(null);
        }
    }

    @FXML
    private void handleEdit() {
        System.out.println("Edit dish: " + (dish != null ? dish.getName() : "unknown"));
    }
}