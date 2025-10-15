package com.btl_oop.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;

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
    private Button btnCustomerDetail;

    private Button currentActiveButton;

    @FXML
    public void initialize() {
        // Load Dashboard mặc định khi khởi động
        currentActiveButton = btnDashboard;
        loadDashboard();
    }

    @FXML
    private void loadDashboard() {
        loadContent("/com/btl_oop/FXML/layout_inside/dashboard_content.fxml");
        setActiveButton(btnDashboard);
    }

    @FXML
    private void loadOrderManagement() {
        loadContent("/com/btl_oop/FXML/layout_inside/order_management.fxml");
        setActiveButton(btnOrderManagement);
    }

    @FXML
    private void loadInventoryManagement() {
        loadContent("/com/btl_oop/FXML/layout_inside/inventory_management.fxml");
        setActiveButton(btnInventoryManagement);
    }

    @FXML
    private void loadSalesReports() {
        loadContent("/com/btl_oop/FXML/layout_inside/sales_reports.fxml");
        setActiveButton(btnSalesReports);
    }

    @FXML
    private void loadCustomerFeedback() {
        loadContent("/com/btl_oop/FXML/layout_inside/customer_feedback.fxml");
        setActiveButton(btnCustomerFeedback);
    }

    @FXML
    private void loadCustomerDetail() {
        loadContent("/com/btl_oop/FXML/layout_inside/customer_detail.fxml");
        setActiveButton(btnCustomerDetail);
    }

    private void loadContent(String fxmlPath) {
        try {
            // Kiểm tra xem file có tồn tại không
            URL resource = getClass().getResource(fxmlPath);

            if (resource == null) {
                System.err.println("FXML file not found: " + fxmlPath);
                System.err.println("Please create the file at: src/main/resources" + fxmlPath);

                // Tạm thời hiển thị message lỗi
                Label errorLabel = new Label("File not found: " + fxmlPath);
                errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-padding: 20;");
                contentArea.setCenter(errorLabel);
                return;
            }

            FXMLLoader loader = new FXMLLoader(resource);

            // Load và lấy root node (có thể là VBox, BorderPane, hay bất kỳ Node nào)
            Node content = loader.load();

            // Set vào center của BorderPane
            contentArea.setCenter(content);

            System.out.println("Successfully loaded: " + fxmlPath);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + fxmlPath);
            System.err.println("Error message: " + e.getMessage());

            // Hiển thị lỗi trong UI
            Label errorLabel = new Label("Error loading page: " + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red; -fx-padding: 20;");
            contentArea.setCenter(errorLabel);
        }
    }

    private void setActiveButton(Button activeButton) {
        // Remove active style from previous button
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("menu-button-active");
        }

        // Add active style to new button
        if (!activeButton.getStyleClass().contains("menu-button-active")) {
            activeButton.getStyleClass().add("menu-button-active");
        }

        currentActiveButton = activeButton;
    }
}