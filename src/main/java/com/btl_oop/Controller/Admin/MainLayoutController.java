package com.btl_oop.Controller.Admin;

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
    private String userRole; // Lưu role của user

    @FXML
    public void initialize() {
        // Lấy role từ đâu ó
        //userRole =

        // Cấu hình menu theo role
        configureMenuByRole();

        // Load Dashboard mặc định khi khởi động
        currentActiveButton = btnDashboard;
        loadDashboard();
    }


    /**
     * Cấu hình hiển thị menu dựa trên role
     * USER: chỉ hiển thị Dashboard, Inventory Management, Customer Feedback
     * ADMIN: hiển thị tất cả
     */
    private void configureMenuByRole() {
        if ("USER".equalsIgnoreCase(userRole)) {
            // Các menu hiển thị cho USER
            showButton(btnDashboard, true);
            showButton(btnInventoryManagement, true);
            showButton(btnCustomerFeedback, true);

            // Các menu ẩn đối với USER
            showButton(btnOrderManagement, false);
            showButton(btnSalesReports, false);
            showButton(btnCustomerDetail, false);

            System.out.println("Menu configured for USER role");
        } else if ("ADMIN".equalsIgnoreCase(userRole)) {
            // Admin hiển thị tất cả menu
            showButton(btnDashboard, true);
            showButton(btnInventoryManagement, true);
            showButton(btnCustomerFeedback, true);
            showButton(btnOrderManagement, true);
            showButton(btnSalesReports, true);
            showButton(btnCustomerDetail, true);

            System.out.println("Menu configured for ADMIN role");
        } else {
            // Mặc định hiển thị tất cả nếu chưa set role
            System.out.println("Warning: User role not set. Showing all menus.");
            showButton(btnDashboard, true);
            showButton(btnInventoryManagement, true);
            showButton(btnCustomerFeedback, true);
            showButton(btnOrderManagement, true);
            showButton(btnSalesReports, true);
            showButton(btnCustomerDetail, true);
        }
    }

    private void showButton(Button button, boolean show) {
        button.setVisible(show);
        button.setManaged(show); // setManaged(false) để không chiếm không gian trong layout
    }

    @FXML
    private void loadDashboard() {
        loadContent("/com/btl_oop/FXML/Admin/layout_inside/dashboard_content.fxml");
        setActiveButton(btnDashboard);
    }

    @FXML
    private void loadOrderManagement() {
        // Kiểm tra quyền truy cập
        if (!hasAccess()) {
            showAccessDenied();
            return;
        }
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
        // Kiểm tra quyền truy cập
        if (!hasAccess()) {
            showAccessDenied();
            return;
        }
        loadContent("/com/btl_oop/FXML/Admin/layout_inside/sales_reports.fxml");
        setActiveButton(btnSalesReports);
    }

    @FXML
    private void loadCustomerFeedback() {
        loadContent("/com/btl_oop/FXML/Admin/layout_inside/customer_feedback.fxml");
        setActiveButton(btnCustomerFeedback);
    }

    @FXML
    private void loadCustomerDetail() {
        // Kiểm tra quyền truy cập
        if (!hasAccess()) {
            showAccessDenied();
            return;
        }
        loadContent("/com/btl_oop/FXML/Admin/layout_inside/customer_detail.fxml");
        setActiveButton(btnCustomerDetail);
    }

    // Kiểm tra quyền
    private boolean hasAccess() {
        return !"USER".equalsIgnoreCase(userRole);
    }
    // Show log không có quyền truy cập
    private void showAccessDenied() {
        Label accessDeniedLabel = new Label("Access Denied!\nYou don't have permission to access this feature.");
        accessDeniedLabel.setStyle(
                "-fx-font-size: 18px; " +
                        "-fx-text-fill: #d32f2f; " +
                        "-fx-padding: 40; " +
                        "-fx-alignment: center; " +
                        "-fx-font-weight: bold;"
        );
        contentArea.setCenter(accessDeniedLabel);
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