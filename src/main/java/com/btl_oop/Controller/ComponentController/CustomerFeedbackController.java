package com.btl_oop.Controller.ComponentController;

import com.btl_oop.Model.CustomerFeedback;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomerFeedbackController {

    @FXML
    private FlowPane feedbackContainer;

    private List<FeedbackCardController> cardControllers = new ArrayList<>();

    @FXML
    public void initialize() {
        loadFeedbackData();
    }

    private void loadFeedbackData() {
        // Sample data
        List<CustomerFeedback> feedbacks = new ArrayList<>();

        feedbacks.add(new CustomerFeedback(
                "Jone Sena",
                "#ff9999",
                "2 days ago",
                "Very good!",
                4.5,
                "/com/btl_oop/img/img/food_asian_bowl.png"
        ));

        feedbacks.add(new CustomerFeedback(
                "Sofia",
                "#99ccff",
                "1 days ago",
                "Yummy",
                4.0,
                "/com/btl_oop/img/img/food_asian_bowl.png"
        ));

        feedbacks.add(new CustomerFeedback(
                "Anandreanvyah",
                "#333333",
                "5 days ago",
                "Not real bad",
                4.5,
                "/com/btl_oop/img/img/food_asian_bowl.png"
        ));

        feedbacks.add(new CustomerFeedback(
                "Anandreanvyah",
                "#666666",
                "3 days ago",
                "This best!",
                4.5,
                "/com/btl_oop/img/img/food_asian_bowl.png"
        ));

        feedbacks.add(new CustomerFeedback(
                "Anandreanvyah",
                "#ffcc66",
                "4 days ago",
                "omg",
                4.5,
                "/com/btl_oop/img/img/food_macarons.png"
        ));

        feedbacks.add(new CustomerFeedback(
                "Anandreanvyah",
                "#999999",
                "2 days ago",
                "Express",
                4.5,
                "/com/btl_oop/img/img/food_macarons.png"
        ));

        // Load cards
        for (CustomerFeedback feedback : feedbacks) {
            addFeedbackCard(feedback);
        }
    }

    private void addFeedbackCard(CustomerFeedback feedback) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/btl_oop/FXML/components/feedback_card.fxml")
            );
            VBox card = loader.load();

            FeedbackCardController controller = loader.getController();
            controller.setData(feedback);

            cardControllers.add(controller);
            feedbackContainer.getChildren().add(card);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading feedback card: " + e.getMessage());
        }
    }

    // Method to add new feedback
    public void addNewFeedback(CustomerFeedback feedback) {
        addFeedbackCard(feedback);
    }

    // Get all selected feedbacks
    public List<CustomerFeedback> getSelectedFeedbacks() {
        List<CustomerFeedback> selected = new ArrayList<>();
        for (FeedbackCardController controller : cardControllers) {
            if (controller.isSelected()) {
                selected.add(controller.getFeedback());
            }
        }
        return selected;
    }
}