package com.btl_oop.Controller.Admin.ComponentController;

import com.btl_oop.Model.CustomerFeedback;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class FeedbackCardController {

    @FXML
    private VBox feedbackCard;

    @FXML
    private Circle avatarCircle;

    @FXML
    private Label nameLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label reviewLabel;

    @FXML
    private HBox starsContainer;

    @FXML
    private ImageView foodImage;

    private CustomerFeedback feedback;
    private boolean isSelected = false;

    public void setData(CustomerFeedback feedback) {
        this.feedback = feedback;

        // Set avatar color
        avatarCircle.setFill(Color.web(feedback.getAvatarColor()));

        // Set customer info
        nameLabel.setText(feedback.getCustomerName());
        timeLabel.setText(feedback.getTimeAgo());
        reviewLabel.setText(feedback.getReview());

        // Set star rating
        createStarRating(feedback.getRating());

        // Set food image
        try {
            Image image = new Image(getClass().getResourceAsStream(feedback.getFoodImageUrl()));
            foodImage.setImage(image);
        } catch (Exception e) {
            System.err.println("Error loading image: " + feedback.getFoodImageUrl());
        }
    }

    private void createStarRating(double rating) {
        starsContainer.getChildren().clear();

        int fullStars = (int) rating;
        boolean hasHalfStar = (rating - fullStars) >= 0.5;

        // Add filled stars
        for (int i = 0; i < fullStars; i++) {
            Label star = new Label("★");
            star.getStyleClass().addAll("star", "filled");
            starsContainer.getChildren().add(star);
        }

        // Add half star if needed
        if (hasHalfStar) {
            Label star = new Label("★");
            star.getStyleClass().addAll("star", "filled");
            starsContainer.getChildren().add(star);
        }

        // Add empty stars
        int totalStars = hasHalfStar ? fullStars + 1 : fullStars;
        for (int i = totalStars; i < 5; i++) {
            Label star = new Label("★");
            star.getStyleClass().addAll("star", "empty");
            starsContainer.getChildren().add(star);
        }

        // Add rating value
        Label ratingValue = new Label(String.format("%.1f", rating));
        ratingValue.getStyleClass().add("rating-value");
        starsContainer.getChildren().add(ratingValue);
    }

    @FXML
    private void initialize() {
        // Add click handler for selection
        if (feedbackCard != null) {
            feedbackCard.setOnMouseClicked(event -> toggleSelection());
        }
    }

    private void toggleSelection() {
        isSelected = !isSelected;
        if (isSelected) {
            feedbackCard.getStyleClass().add("selected");
        } else {
            feedbackCard.getStyleClass().remove("selected");
        }
    }

    public CustomerFeedback getFeedback() {
        return feedback;
    }

    public boolean isSelected() {
        return isSelected;
    }
}