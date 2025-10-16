package com.btl_oop.Controller.Admin.MainController;

import com.btl_oop.Controller.Admin.ComponentController.CustomerDetailDialogController;
import com.btl_oop.Controller.Admin.ComponentController.CustomerListItemController;
import com.btl_oop.Model.Entity.Customer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerDetailController {

    @FXML
    private Label totalCustomersLabel;

    @FXML
    private Label membersLabel;

    @FXML
    private Label activeNowLabel;

    @FXML
    private TextField searchField;

    @FXML
    private VBox customerListContainer;

    @FXML
    private Label paginationLabel;

    @FXML
    private HBox paginationButtonsContainer;

    private List<Customer> allCustomers = new ArrayList<>();
    private List<Customer> filteredCustomers = new ArrayList<>();
    private Customer selectedCustomer;
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 8;

    @FXML
    public void initialize() {
        loadCustomerData();
        updateStats();
        displayCustomers();
        setupSearchListener();
    }

    private void loadCustomerData() {
        // Sample data using your Customer class
        allCustomers.add(new Customer(
                1, "Jane Cooper", "Cooper", "jane@microsoft.com",
                "Female",
                true, LocalDateTime.now().minusMonths(1)
        ));

        allCustomers.add(new Customer(
                2, "Floyd Miles", "Floyd", "floyd@yahoo.com",
                "Male",
                false, LocalDateTime.now().minusMonths(2)
        ));

        allCustomers.add(new Customer(
                3, "Ronald Richards", "Ron", "ronald@adobe.com",
                "Male",
                false, LocalDateTime.now().minusWeeks(3)
        ));

        allCustomers.add(new Customer(
                4, "Marvin McKinney", "Marvin", "marvin@tesla.com",
                "Male",
                true, LocalDateTime.now().minusDays(15)
        ));

        allCustomers.add(new Customer(
                5, "Jerome Bell", "Jerry", "jerome@google.com",
                "Male",
                true, LocalDateTime.now().minusDays(10)
        ));

        allCustomers.add(new Customer(
                6, "Kathryn Murphy", "Kathy", "kathryn@microsoft.com",
                "Female",
                true, LocalDateTime.now().minusWeeks(2)
        ));

        allCustomers.add(new Customer(
                7, "Jacob Jones", "Jake", "jacob@yahoo.com",
                "Male",
                true, LocalDateTime.now().minusDays(5)
        ));

        allCustomers.add(new Customer(
                8, "Kristin Watson", "Kris", "kristin@facebook.com",
                "Female",
                false, LocalDateTime.now().minusMonths(3)
        ));

        for (int i = 9; i <= 256; i++) {
            allCustomers.add(new Customer(
                    i, "Customer " + i, "Nick" + i, "customer" + i + "@example.com",
                    i % 2 == 0 ? "Male" : "Female",
                    i % 3 != 0, LocalDateTime.now().minusDays(i % 30)
            ));
        }

        filteredCustomers = new ArrayList<>(allCustomers);
    }

    private void updateStats() {
        totalCustomersLabel.setText(String.valueOf(allCustomers.size()));

        long activeCount = allCustomers.stream().filter(Customer::isActive).count();
        membersLabel.setText(String.valueOf(activeCount));

        // Active now - random number for demo
        activeNowLabel.setText(String.valueOf((int)(activeCount * 0.1)));
    }

    private void displayCustomers() {
        customerListContainer.getChildren().clear();

        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, filteredCustomers.size());

        for (int i = startIndex; i < endIndex; i++) {
            Customer customer = filteredCustomers.get(i);
            addCustomerRow(customer);
        }

        updatePaginationLabel();
        updatePaginationButtons();
    }

    private void addCustomerRow(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/btl_oop/FXML/Admin/components/customer_list_item.fxml")
            );
            HBox customerRow = loader.load();

            CustomerListItemController controller = loader.getController();
            controller.setData(customer, () -> handleCustomerClick(customer));

            customerListContainer.getChildren().add(customerRow);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading customer row: " + e.getMessage());
        }
    }

    private void handleCustomerClick(Customer customer) {
        this.selectedCustomer = customer;
        System.out.println("Customer selected: " + customer.getFullName());

        // Open customer detail dialog
        showCustomerDetailDialog(customer);
    }

    private void showCustomerDetailDialog(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/btl_oop/FXML/Admin/layout_inside/customer_detail_dialog.fxml")
            );
            Parent root = loader.load();

            CustomerDetailDialogController controller = loader.getController();
            controller.loadCustomer(customer);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Customer Details - " + customer.getFullName());
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            // Fallback to simple alert
            showSimpleCustomerDetail(customer);
        }
    }

    private void showSimpleCustomerDetail(Customer customer) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Customer Details");
        alert.setHeaderText(customer.getFullName());
        alert.setContentText(
                "Full Name: " + customer.getFullName() + "\n" +
                        "Nick Name: " + customer.getNickName() + "\n" +
                        "Email: " + customer.getEmail() + "\n" +
                        "Gender: " + customer.getGender() + "\n" +
                        "Language: " + customer.getLanguage() + "\n" +
                        "Status: " + (customer.isActive() ? "Active" : "Inactive") + "\n" +
                        "Email Added: " + customer.getEmailTimeAgo()
        );
        alert.showAndWait();
    }

    private void setupSearchListener() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterCustomers(newValue);
            });
        }
    }

    private void filterCustomers(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            filteredCustomers = new ArrayList<>(allCustomers);
        } else {
            String searchLower = searchText.toLowerCase();
            filteredCustomers = allCustomers.stream()
                    .filter(c ->
                            c.getFullName().toLowerCase().contains(searchLower) ||
                                    c.getEmail().toLowerCase().contains(searchLower) ||
                                    c.getLanguage().toLowerCase().contains(searchLower)
                    )
                    .toList();
        }

        currentPage = 1;
        displayCustomers();
    }

    private void updatePaginationLabel() {
        int startIndex = (currentPage - 1) * ITEMS_PER_PAGE + 1;
        int endIndex = Math.min(currentPage * ITEMS_PER_PAGE, filteredCustomers.size());

        if (filteredCustomers.isEmpty()) {
            paginationLabel.setText("No entries found");
        } else {
            paginationLabel.setText(
                    String.format("Showing data %d to %d of %d entries",
                            startIndex, endIndex, filteredCustomers.size())
            );
        }
    }

    private void updatePaginationButtons() {
        if (paginationButtonsContainer == null) return;

        paginationButtonsContainer.getChildren().clear();

        int totalPages = (int) Math.ceil((double) filteredCustomers.size() / ITEMS_PER_PAGE);

        if (totalPages <= 1) return;

        Button prevButton = createPaginationButton("‹", false);
        prevButton.setOnAction(e -> handlePreviousPage());
        prevButton.setDisable(currentPage == 1);
        paginationButtonsContainer.getChildren().add(prevButton);

        int maxVisiblePages = 5;
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

        if (endPage - startPage < maxVisiblePages - 1) {
            startPage = Math.max(1, endPage - maxVisiblePages + 1);
        }

        if (startPage > 1) {
            Button firstButton = createPaginationButton("1", false);
            firstButton.setOnAction(e -> goToPage(1));
            paginationButtonsContainer.getChildren().add(firstButton);

            if (startPage > 2) {
                Label dots = new Label("...");
                dots.getStyleClass().add("page-dots");
                paginationButtonsContainer.getChildren().add(dots);
            }
        }

        for (int i = startPage; i <= endPage; i++) {
            final int pageNum = i;
            Button pageButton = createPaginationButton(String.valueOf(i), i == currentPage);
            pageButton.setOnAction(e -> goToPage(pageNum));
            paginationButtonsContainer.getChildren().add(pageButton);
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                Label dots = new Label("...");
                dots.getStyleClass().add("page-dots");
                paginationButtonsContainer.getChildren().add(dots);
            }

            Button lastButton = createPaginationButton(String.valueOf(totalPages), false);
            lastButton.setOnAction(e -> goToPage(totalPages));
            paginationButtonsContainer.getChildren().add(lastButton);
        }

        Button nextButton = createPaginationButton("›", false);
        nextButton.setOnAction(e -> handleNextPage());
        nextButton.setDisable(currentPage == totalPages);
        paginationButtonsContainer.getChildren().add(nextButton);
    }

    private Button createPaginationButton(String text, boolean isActive) {
        Button button = new Button(text);
        button.getStyleClass().add("page-button");
        if (isActive) {
            button.getStyleClass().add("active");
        }
        return button;
    }

    private void goToPage(int page) {
        int totalPages = (int) Math.ceil((double) filteredCustomers.size() / ITEMS_PER_PAGE);
        if (page >= 1 && page <= totalPages) {
            currentPage = page;
            displayCustomers();
        }
    }

    @FXML
    private void handleAddCustomer() {
        System.out.println("Add customer clicked");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add Customer");
        alert.setHeaderText("Add New Customer");
        alert.setContentText("This feature will allow you to add a new customer.");
        alert.showAndWait();
    }

    @FXML
    private void handlePreviousPage() {
        if (currentPage > 1) {
            currentPage--;
            displayCustomers();
        }
    }

    @FXML
    private void handleNextPage() {
        int totalPages = (int) Math.ceil((double) filteredCustomers.size() / ITEMS_PER_PAGE);
        if (currentPage < totalPages) {
            currentPage++;
            displayCustomers();
        }
    }

    public void refreshCustomerList() {
        loadCustomerData();
        updateStats();
        displayCustomers();
    }

    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }
}