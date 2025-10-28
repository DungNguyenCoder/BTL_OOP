package com.btl_oop.Model.DAO;

import com.btl_oop.Model.Entity.Order;
import com.btl_oop.Utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final RestaurantTableDAO tableDAO;

    public OrderDAO() {
        this.tableDAO = new RestaurantTableDAO();
        ensureTable();
    }

    private void ensureTable() {
        String sql = "CREATE TABLE IF NOT EXISTS `Order` (" +
                "OrderID INT AUTO_INCREMENT PRIMARY KEY, " +
                "TableID INT NOT NULL, " +
                "EmployeeID INT, " +
                "CheckoutTime DATETIME, " +
                "Status ENUM('Serving','Paid','Cancelled') DEFAULT 'Serving', " +
                "Subtotal DECIMAL(10,2) DEFAULT 0, " +
                "Tax DECIMAL(10,2) DEFAULT 0, " +
                "Total DECIMAL(10,2) DEFAULT 0, " +
                "FOREIGN KEY (TableID) REFERENCES RestaurantTable(TableID) " +
                "ON DELETE CASCADE ON UPDATE CASCADE, " +
                "FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID) " +
                "ON DELETE SET NULL ON UPDATE CASCADE)";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'Order' ensured successfully!");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to ensure Order table", e);
        }
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM `Order` ORDER BY OrderID";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load orders", e);
        }
        return orders;
    }

    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM `Order` WHERE OrderID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch Order with ID: " + orderId, e);
        }
    }

    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM `Order` WHERE Status = ? ORDER BY OrderID";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load orders for status: " + status, e);
        }
        return orders;
    }

    public List<Order> getOrdersByTableId(int tableId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM `Order` WHERE TableID = ? ORDER BY OrderID";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load orders for TableID: " + tableId, e);
        }
        return orders;
    }

    public int countPaidOrdersToday() {
        String sql = "SELECT COUNT(*) AS cnt FROM `Order` WHERE Status = 'Paid' AND DATE(CheckoutTime) = CURDATE()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("cnt");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to count today's paid orders", e);
        }
    }

    public boolean insertOrder(Order order) {
        String sql = "INSERT INTO `Order` (TableID, EmployeeID, Status, Subtotal, Tax, Total) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getTableId());
            ps.setInt(2, order.getEmployeeId());
            ps.setString(3, order.getStatus());
            ps.setDouble(4, order.getSubtotal());
            ps.setDouble(5, order.getTax());
            ps.setDouble(6, order.getTotal());

            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet key = ps.getGeneratedKeys()) {
                if (key.next()) {
                    order.setOrderId(key.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert Order for TableID: " + order.getTableId(), e);
        }
    }

    public boolean updateOrder(Order order) {
        String sql = "UPDATE `Order` SET TableID = ?, EmployeeID = ?, CheckoutTime = ?, Status = ?, Subtotal = ?, Tax = ?, Total = ? WHERE OrderID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, order.getTableId());
            ps.setInt(2, order.getEmployeeId());
            ps.setTimestamp(3, order.getCheckoutTime() != null ? Timestamp.valueOf(order.getCheckoutTime()) : null);
            ps.setString(4, order.getStatus());
            ps.setDouble(5, order.getSubtotal());
            ps.setDouble(6, order.getTax());
            ps.setDouble(7, order.getTotal());
            ps.setInt(8, order.getOrderId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update Order: " + order.getOrderId(), e);
        }
    }

    public boolean deleteOrder(int orderId) {
        String sql = "DELETE FROM `Order` WHERE OrderID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete Order: " + orderId, e);
        }
    }

    public boolean processPayment(int orderId, double taxRate) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Validate order
            Order order = getOrderById(orderId);
            if (order == null || !"Serving".equals(order.getStatus())) {
                throw new IllegalStateException("Order not found or not in Serving status");
            }

            // Calculate Subtotal
            String calcSql = "SELECT SUM(oi.Quantity * d.Price) AS Subtotal " +
                    "FROM OrderItem oi JOIN Dish d ON oi.DishID = d.DishID " +
                    "WHERE oi.OrderID = ?";
            double subtotal = 0.0;
            try (PreparedStatement ps = conn.prepareStatement(calcSql)) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        subtotal = rs.getDouble("Subtotal");
                    }
                }
            }

            double tax = subtotal * taxRate;
            double total = subtotal + tax;

            // Update Order
            String updateOrderSql = "UPDATE `Order` SET Status = ?, CheckoutTime = ?, Subtotal = ?, Tax = ?, Total = ? WHERE OrderID = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateOrderSql)) {
                ps.setString(1, "Paid");
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setDouble(3, subtotal);
                ps.setDouble(4, tax);
                ps.setDouble(5, total);
                ps.setInt(6, orderId);
                if (ps.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw new RuntimeException("Failed to process payment for OrderID: " + orderId, e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Timestamp checkoutTime = rs.getTimestamp("CheckoutTime");
        return new Order(
                rs.getInt("OrderID"),
                rs.getInt("TableID"),
                rs.getInt("EmployeeID"),
                checkoutTime != null ? checkoutTime.toLocalDateTime() : null,
                rs.getString("Status"),
                rs.getDouble("Subtotal"),
                rs.getDouble("Tax"),
                rs.getDouble("Total")
        );
    }
}