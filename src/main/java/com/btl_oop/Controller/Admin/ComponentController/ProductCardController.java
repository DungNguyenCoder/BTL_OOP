package com.btl_oop.Controller.Admin.ComponentController;

import com.btl_oop.Controller.Admin.MainController.EditDishDialogController;
import com.btl_oop.Model.DAO.DishDAO;
import com.btl_oop.Model.Entity.Dish;
import com.btl_oop.Utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ProductCardController {

    @FXML private ImageView imageView;
    @FXML private Label nameLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label priceLabel;

    private Dish dish;
    private DishDAO dishDAO = new DishDAO();
    private Runnable onDishUpdatedCallback;

    public void setData(Dish dish) {
        this.dish = dish;
        nameLabel.setText(dish.getName());
        descriptionLabel.setText(dish.getDescription());
        priceLabel.setText(String.format("$%.2f", dish.getPrice()));
        loadImage(dish.getImageUrl());
    }

    public void setOnDishUpdatedCallback(Runnable callback) {
        this.onDishUpdatedCallback = callback;
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
        if (dish == null) {
            System.err.println("No dish to edit");
            return;
        }

        System.out.println("Edit dish: " + dish.getName());

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/btl_oop/FXML/Admin/layout_inside/edit_dish_dialog.fxml")
            );
            Parent root = loader.load();

            EditDishDialogController controller = loader.getController();
            controller.setDish(dish);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Dish - " + dish.getName());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            dialogStage.showAndWait();

            Dish updatedDish = controller.getUpdatedDish();
            if (updatedDish != null) {
                SessionManager session = SessionManager.getInstance();
                int adminId = session.getCurrentAdmin().getAdminId();
                boolean success = dishDAO.updateDish(updatedDish, adminId);
                if (success) {
                    System.out.println("✓ Dish updated successfully: " + updatedDish.getName());

                    this.dish = updatedDish;
                    setData(updatedDish);

                    showSuccess("Dish updated successfully!");

                    if (onDishUpdatedCallback != null) {
                        onDishUpdatedCallback.run();
                    }
                } else {
                    showError("Failed to update dish in database.");
                }
            }

        } catch (IOException e) {
            System.err.println("✗ Failed to load edit dialog");
            e.printStackTrace();
            showError("Failed to open edit dialog: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("✗ Error during edit");
            e.printStackTrace();
            showError("Error: " + e.getMessage());
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}