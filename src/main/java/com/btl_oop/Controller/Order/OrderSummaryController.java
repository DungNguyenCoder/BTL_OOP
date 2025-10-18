package com.btl_oop.Controller.Order;

import com.btl_oop.Model.DAO.OrderDAO;
import com.btl_oop.Model.DAO.OrderItemDAO;
import com.btl_oop.Model.DAO.RestaurantTableDAO;
import com.btl_oop.Model.DAO.DishDAO;
import com.btl_oop.Model.DAO.EmployeeDAO;
import com.btl_oop.Model.Entity.Dish;
import com.btl_oop.Model.Entity.Order;
import com.btl_oop.Model.Entity.OrderItem;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class OrderSummaryController {
    @FXML private TextField tableNumberField;
    @FXML private TextField employeeNameField;
    @FXML private VBox orderList;
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;

    private final List<OrderItem> items = new ArrayList<>();
    private double subtotal = 0;
    private double tax = 0;
    private double total = 0;
    private int currentOrderId = 0;

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final RestaurantTableDAO tableDAO = new RestaurantTableDAO();
    private final DishDAO dishDAO = new DishDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public void addDish(Dish dish, int quantity) {
        if (quantity <= 0) {
            showAlert("Lỗi nhập liệu", "Số lượng phải lớn hơn 0!");
            return;
        }
        if (dishDAO.getDishById(dish.getDishId()) == null) {
            showAlert("Lỗi", "Món ăn không tồn tại trong cơ sở dữ liệu!");
            return;
        }

        for (OrderItem i : items) {
            if (i.getDishId() == dish.getDishId()) {
                i.setQuantity(i.getQuantity() + quantity);
                updateUI();
                return;
            }
        }
        items.add(new OrderItem(0, 0, dish.getDishId(), quantity));
        updateUI();
    }

    public void clearAll() {
        items.clear();
        currentOrderId = 0;
        subtotal = 0;
        tax = 0;
        total = 0;
        tableNumberField.clear();
        employeeNameField.clear();
        updateUI();
    }

    private void updateUI() {
        orderList.getChildren().clear();
        subtotal = 0;

        for (OrderItem item : items) {
            Dish dish = dishDAO.getDishById(item.getDishId());
            if (dish == null) {
                showAlert("Lỗi", "Không tìm thấy món với ID: " + item.getDishId());
                continue;
            }
            Label label = new Label(dish.getName() + " × " + item.getQuantity() +
                    " ($" + String.format("%.2f", dish.getPrice() * item.getQuantity()) + ")");
            orderList.getChildren().add(label);
            subtotal += dish.getPrice() * item.getQuantity();
        }

        tax = subtotal * 0.1;
        total = subtotal + tax;

        subtotalLabel.setText(String.format("$%.2f", subtotal));
        taxLabel.setText(String.format("$%.2f", tax));
        totalLabel.setText(String.format("$%.2f", total));
    }

    @FXML
    private void confirmOrder() {
        try {
            if (items.isEmpty()) {
                showAlert("Lỗi", "Vui lòng thêm ít nhất một món trước khi xác nhận!");
                return;
            }

            int tableNumber;
            try {
                tableNumber = Integer.parseInt(tableNumberField.getText().trim());
            } catch (NumberFormatException e) {
                showAlert("Lỗi nhập liệu", "Vui lòng nhập số bàn hợp lệ!");
                return;
            }

//            String employeeInput = employeeNameField.getText().trim();
            String employeeInput = "1";
            if (employeeInput.isEmpty()) {
                showAlert("Lỗi nhập liệu", "Vui lòng nhập tên hoặc ID nhân viên!");
                return;
            }

            int employeeId;
            try {
                employeeId = Integer.parseInt(employeeInput);
                // Kiểm tra ID có tồn tại trong CSDL
                employeeId = employeeDAO.getEmployeeIdById(employeeId);
                if (employeeId == 0) {
                    showAlert("Nhân viên không hợp lệ", "ID nhân viên " + employeeInput + " không tồn tại!");
                    return;
                }
            } catch (NumberFormatException e) {
                // Nếu không phải số, tìm theo tên
                employeeId = employeeDAO.getEmployeeIdByName(employeeInput);
                if (employeeId == 0) {
                    showAlert("Nhân viên không hợp lệ", "Nhân viên " + employeeInput + " không tồn tại!");
                    return;
                }
            }

            int tableId = tableDAO.getTableIdByNumber(tableNumber);
            if (tableId == 0) {
                showAlert("Bàn không hợp lệ", "Số bàn " + tableNumber + " không tồn tại!");
                return;
            }

            Order order = new Order(
                    0, // OrderID do CSDL gán
                    tableId,
                    employeeId,
                    null, // CheckoutTime
                    "Serving",
                    0, // Subtotal
                    0, // Tax
                    0  // Total
            );

            boolean orderSaved = orderDAO.insertOrder(order);
            if (!orderSaved || order.getOrderId() <= 0) {
                showAlert("Lỗi", "Không thể lưu đơn hàng vào cơ sở dữ liệu!");
                return;
            }

            for (OrderItem item : items) {
                item.setOrderId(order.getOrderId());
                boolean itemSaved = orderItemDAO.insertOrderItem(item);
                if (!itemSaved) {
                    Dish dish = dishDAO.getDishById(item.getDishId());
                    String dishName = dish != null ? dish.getName() : "ID " + item.getDishId();
                    showAlert("Lỗi", "Không thể lưu món: " + dishName);
                    return;
                }
            }

            currentOrderId = order.getOrderId();
//            saveToJson(order, items);
            showAlert("Thành công", "Đơn hàng đã được lưu thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Có lỗi xảy ra khi lưu đơn hàng: " + e.getMessage());
        }
    }

    @FXML
    private void processPayment() {
        try {
            if (currentOrderId == 0) {
                showAlert("Lỗi", "Chưa có đơn hàng nào được xác nhận để thanh toán!");
                return;
            }

            boolean paymentProcessed = orderDAO.processPayment(currentOrderId, 0.1);
            if (paymentProcessed) {
                Order updatedOrder = orderDAO.getOrderById(currentOrderId);
                if (updatedOrder != null) {
                    subtotal = updatedOrder.getSubtotal();
                    tax = updatedOrder.getTax();
                    total = updatedOrder.getTotal();
                    subtotalLabel.setText(String.format("$%.2f", subtotal));
                    taxLabel.setText(String.format("$%.2f", tax));
                    totalLabel.setText(String.format("$%.2f", total));
                }
                showAlert("Thành công", "Thanh toán đơn hàng thành công!");
                clearAll();
            } else {
                showAlert("Lỗi", "Không thể xử lý thanh toán cho đơn hàng!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Lỗi", "Có lỗi xảy ra khi xử lý thanh toán: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class OrderJsonWrapper {
        private final Order order;
        private final List<OrderItem> items;

        public OrderJsonWrapper(Order order, List<OrderItem> items) {
            this.order = order;
            this.items = items;
        }
    }
}