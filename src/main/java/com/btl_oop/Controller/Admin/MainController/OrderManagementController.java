package com.btl_oop.Controller.Admin.MainController;

import com.btl_oop.Controller.Admin.ComponentController.OrderItemController;
import com.btl_oop.Model.DAO.EmployeeDAO;
import com.btl_oop.Model.DAO.OrderDAO;
import com.btl_oop.Model.DAO.ReportDAO;
import com.btl_oop.Model.Entity.Employee;
import com.btl_oop.Model.Entity.Order;
import com.btl_oop.Model.Entity.OrderTotals;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class OrderManagementController {

    @FXML
    private VBox orderContainer;

    @FXML
    private Label date;

    @FXML
    private Label welcomeText;

    @FXML
    private Label totalOrders;

    @FXML
    private Label totalDelivered;

    @FXML
    private Label totalCancelled;

    @FXML
    private Label totalRevenue;

    private OrderDAO orderDAO ;
    private OrderTotals orderTotals;
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private ReportDAO reportDAO = new ReportDAO();
    private List<Order> allOrders ;
    private final String img_account = "/com/btl_oop/img/ic_item/account.png";
    @FXML
    public void initialize() {
        System.out.println("DashboardController initialized!");
        System.out.println("orderContainer is null? " + (orderContainer == null));
        orderDAO = new OrderDAO();
        loadSampleOrders();
        setUpLabel();
    }

    private void setUpLabel() {
        totalOrders.setText(String.valueOf(reportDAO.getTotalOrders()));
        totalRevenue.setText(String.valueOf(reportDAO.getTotalRevenue()));
        totalDelivered.setText(String.valueOf(reportDAO.getCompletedOrdersToday()));
        String formatDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH));
        date.setText(formatDate);
        int employeeId = Employee.getEmployeeId();
        Employee employee = employeeDAO.getEmployeeById(employeeId);
        welcomeText.setText(String.format("Hi, %s. Welcome back to Admin!",employee.getFullName() ));
    }

    private void loadSampleOrders() {
        try {
            allOrders = orderDAO.getAllOrders();
            allOrders = allOrders.reversed();
        }
        catch (Exception e )
        {
            System.out.println("Fail upload()");
        }
        for (Order order : allOrders) {
            try {
                OrderTotals totals = orderDAO.getOrderTotalsById(order.getOrderId());

                double total = (totals != null) ? totals.getTotal() : 0.0;

                addOrder(
                        String.valueOf(order.getOrderId()),
                        order.getStatus(),
                        "CustomerName",
                        total,
                        img_account
                );
            } catch (Exception e) {
                System.err.println("Error loading order #" + order.getOrderId() + ": " + e.getMessage());
            }
        }

    }

    public void addOrder(String orderId, String status, String customerName,
                         double totalPrice, String imagePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/btl_oop/FXML/Admin/components/orderItem.fxml"));
            VBox orderItem = loader.load();

            OrderItemController controller = loader.getController();
            controller.setOrderData(orderId, status, customerName, totalPrice, imagePath);

            controller.setDetailButtonClick();

            orderContainer.getChildren().add(orderItem);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not load OrderItem.fxml");
        }
    }

    public void clearOrders() {
        orderContainer.getChildren().clear();
    }

    public void removeOrder(int index) {
        if (index >= 0 && index < orderContainer.getChildren().size()) {
            orderContainer.getChildren().remove(index);
        }
    }

    private void showOrderDetails(String orderId) {
        System.out.println("Showing details for order: " + orderId);
    }

    public void addNewOrder(String orderId, String status, String customerName,
                            double totalPrice, String imagePath) {
        addOrder(orderId, status, customerName, totalPrice, imagePath);
    }
}
