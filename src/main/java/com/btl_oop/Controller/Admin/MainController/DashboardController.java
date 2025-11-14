package com.btl_oop.Controller.Admin.MainController;

import com.btl_oop.Controller.Admin.ComponentController.FeedbackCardController;
import com.btl_oop.Model.DAO.AdminDAO;
import com.btl_oop.Model.DAO.EmployeeDAO;
import com.btl_oop.Model.DAO.ReportDAO;
import com.btl_oop.Model.Data.CustomerFeedback;
import com.btl_oop.Model.Entity.Admin;
import com.btl_oop.Model.Entity.Employee;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DashboardController {

    @FXML
    private HBox reviewsContainer;

    @FXML
    private Label totalOrders;

    @FXML
    private Label totalDelivered;

    @FXML
    private Label totalCancelled;

    @FXML
    private Label totalRevenue;

    @FXML
    private Label date;

    @FXML
    private Label welcomeText;

    private List<CustomerFeedback> allFeedbacks = new ArrayList<>();
    private ReportDAO reportDAO = new ReportDAO();
    private AdminDAO adminDAO = new AdminDAO();
    private int currentStartIndex = 0;
    private final int REVIEWS_PER_PAGE = 3;

    @FXML
    public void initialize() {
        System.out.println("DashboardController initialized!");
        loadFeedbackData();
        displayReviews();
        setUpLabel();
    }

    private void setUpLabel() {
        totalOrders.setText(String.valueOf(reportDAO.getTotalOrders()));
        totalRevenue.setText(String.valueOf(reportDAO.getTotalRevenue()));
        totalDelivered.setText(String.valueOf(reportDAO.getCompletedOrdersToday()));
        // totalCancelled.setText(String.valueOf(reportDAO.getCancelledOrders()));
        String formatDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH));
        date.setText(formatDate);
        int adminId = Admin.getAdminId();
        Admin admin = adminDAO.getAdminById(adminId);
        welcomeText.setText(String.format("Hi, %s. Welcome back to Sedap Admin!",admin.getFullName() ));
    }
    private void loadFeedbackData() {
        // Sample feedback data
        allFeedbacks.add(new CustomerFeedback(
                "Jone Sena",
                "#ff9999",
                "2 days ago",
                "Lorem ipsum dolor sit amet consectetur adipisicing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                4.5,
                "/com/btl_oop/img/img/food_asian_bowl.png"
        ));

        allFeedbacks.add(new CustomerFeedback(
                "Sofia",
                "#99ccff",
                "1 day ago",
                "Consectetur adipisicing elit sed do eiusmod tempor incididunt ut labore et dolore magna aliqua ut enim ad minim.",
                4.0,
                "/com/btl_oop/img/img/food_macarons.png"
        ));

        allFeedbacks.add(new CustomerFeedback(
                "Anandreanvyah",
                "#333333",
                "5 days ago",
                "Lorem ipsum dolor sit amet consectetur adipisicing elit. Sed do eiusmod tempor incididunt.",
                4.5,
                "/com/btl_oop/img/img/food_noodles.png"
        ));

        allFeedbacks.add(new CustomerFeedback(
                "Michael Chen",
                "#66cc99",
                "3 days ago",
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo.",
                5.0,
                "/com/btl_oop/img/img/food_asian_bowl.png"
        ));

        allFeedbacks.add(new CustomerFeedback(
                "Emma Wilson",
                "#ffcc66",
                "4 days ago",
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
                4.0,
                "/com/btl_oop/img/img/food_macarons.png"
        ));

        allFeedbacks.add(new CustomerFeedback(
                "David Park",
                "#ff99cc",
                "6 days ago",
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                4.5,
                "/com/btl_oop/img/img/food_asian_bowl.png"
        ));
    }

    private void displayReviews() {
        if (reviewsContainer == null) {
            System.err.println("ERROR: reviewsContainer is null!");
            return;
        }

        reviewsContainer.getChildren().clear();

        int endIndex = Math.min(currentStartIndex + REVIEWS_PER_PAGE, allFeedbacks.size());

        for (int i = currentStartIndex; i < endIndex; i++) {
            CustomerFeedback feedback = allFeedbacks.get(i);
            addFeedbackCard(feedback);
        }
    }

    private void addFeedbackCard(CustomerFeedback feedback) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/btl_oop/FXML/Admin/components/feedback_card.fxml")
            );
            VBox card = loader.load();

            FeedbackCardController controller = loader.getController();
            controller.setData(feedback);

            reviewsContainer.getChildren().add(card);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading feedback card: " + e.getMessage());
        }
    }

    @FXML
    public void handlePreviousReviews() {
        if (currentStartIndex > 0) {
            currentStartIndex -= REVIEWS_PER_PAGE;
            if (currentStartIndex < 0) {
                currentStartIndex = 0;
            }
            displayReviews();
        }
    }

    @FXML
    public void handleNextReviews() {
        if (currentStartIndex + REVIEWS_PER_PAGE < allFeedbacks.size()) {
            currentStartIndex += REVIEWS_PER_PAGE;
            displayReviews();
        }
    }

    public void addNewFeedback(CustomerFeedback feedback) {
        allFeedbacks.add(0, feedback);
        currentStartIndex = 0;
        displayReviews();
    }
}