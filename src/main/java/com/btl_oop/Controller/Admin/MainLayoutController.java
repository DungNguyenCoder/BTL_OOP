package com.btl_oop.Controller.Admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MainLayoutController {

    @FXML
    private BorderPane contentArea;

    @FXML
    private Button btnDashboard;

    @FXML
    private Button btnOrderManagement;

    @FXML
    private Button btnInventoryManagement;

    @FXML
    private Button btnSalesReports;

    @FXML
    private Button btnCustomerFeedback;

    @FXML
    private Button btnEmployeeDetail;

    @FXML
    private Button btnLogout;

    private Button currentActiveButton;

    @FXML
    public void initialize() {
        currentActiveButton = btnDashboard;
        loadDashboard();
    }

    @FXML
    private void loadDashboard() {
        loadContent("/com/btl_oop/FXML/Admin/layout_inside/dashboard_content.fxml");
        setActiveButton(btnDashboard);
    }

    @FXML
    private void loadOrderManagement() {
        loadContent("/com/btl_oop/FXML/Admin/layout_inside/order_management.fxml");
        setActiveButton(btnOrderManagement);
    }

    @FXML
    private void loadInventoryManagement() {
        loadContent("/com/btl_oop/FXML/Admin/layout_inside/inventory_management.fxml");
        setActiveButton(btnInventoryManagement);
    }

    @FXML
    private void loadSalesReports() {
        loadContent("/com/btl_oop/FXML/Admin/layout_inside/sales_reports.fxml");
        setActiveButton(btnSalesReports);
    }

    @FXML
    private void loadCustomerFeedback() {
        loadContent("/com/btl_oop/FXML/Admin/layout_inside/customer_feedback.fxml");
        setActiveButton(btnCustomerFeedback);
    }

    @FXML
    private void loadEmployeeDetail() {
        loadContent("/com/btl_oop/FXML/Admin/layout_inside/employee_detail.fxml");
        setActiveButton(btnEmployeeDetail);
    }

    @FXML
    private void loadLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/btl_oop/FXML/Login_Registration/login-screen.fxml"));
            Parent loginScreen = loader.load();

            Stage currentStage = (Stage) btnLogout.getScene().getWindow();

            Scene loginScene = new Scene(loginScreen);

            currentStage.setScene(loginScene);
            currentStage.setTitle("Login");

            System.out.println("Logged out successfully!");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading login screen: " + e.getMessage());
        }
    }


    private void loadContent(String fxmlPath) {
        try {
            URL resource = getClass().getResource(fxmlPath);

            if (resource == null) {
                System.err.println("FXML file not found: " + fxmlPath);
                System.err.println("Please create the file at: src/main/resources" + fxmlPath);

                Label errorLabel = new Label("File not found: " + fxmlPath);
                errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-padding: 20;");
                contentArea.setCenter(errorLabel);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);

            Node content = loader.load();

            contentArea.setCenter(content);

            System.out.println("Successfully loaded: " + fxmlPath);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + fxmlPath);
            System.err.println("Error message: " + e.getMessage());

            Label errorLabel = new Label("Error loading page: " + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-padding: 20;");
            contentArea.setCenter(errorLabel);
        }
    }

    private void setActiveButton(Button activeButton) {
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("menu-button-active");
        }

        if (!activeButton.getStyleClass().contains("menu-button-active")) {
            activeButton.getStyleClass().add("menu-button-active");
        }

        currentActiveButton = activeButton;
    }
}