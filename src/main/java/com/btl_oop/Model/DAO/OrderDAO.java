package com.btl_oop.Model.DAO;

import com.btl_oop.Model.Entity.Order;
import com.btl_oop.Model.Entity.OrderItem;
import com.btl_oop.Model.Entity.OrderTotals;
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
        ensureView();
    }

    private void ensureTable() {
        String createSql = "CREATE TABLE IF NOT EXISTS `Order` (" +
                "OrderID INT AUTO_INCREMENT PRIMARY KEY, " +
                "TableID INT NOT NULL, " +
                "EmployeeID INT, " +
                "CheckoutTime DATETIME, " +
                "Status ENUM('Preparing','Ready','Serving','Paid','Cancelled') DEFAULT 'Preparing', " +
                "FOREIGN KEY (TableID) REFERENCES RestaurantTable(TableID) " +
                "ON DELETE CASCADE ON UPDATE CASCADE, " +
                "FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID) " +
                "ON DELETE SET NULL ON UPDATE CASCADE)";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createSql);
            // Attempt to upgrade enum if table already existed with older values
            String alterSql = "ALTER TABLE `Order` MODIFY Status " +
                    "ENUM('Preparing','Ready','Serving','Paid','Cancelled') DEFAULT 'Preparing'";
            try {
                stmt.execute(alterSql);
            } catch (SQLException ignore) {
                // Ignore if enum already matches
            }
            System.out.println("Table 'Order' ensured/updated successfully!");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to ensure Order table", e);
        }
    }

    private void ensureView() {
        String createViewSql = "CREATE OR REPLACE VIEW v_order_totals AS " +
                "SELECT " +
                "    o.OrderID, " +
                "    SUM(oi.Quantity * oi.UnitPrice) AS Subtotal, " +
                "    SUM(oi.Quantity * oi.UnitPrice) * 0.10 AS Tax, " +
                "    SUM(oi.Quantity * oi.UnitPrice) * 1.10 AS Total " +
                "FROM `Order` o " +
                "LEFT JOIN OrderItem oi ON o.OrderID = oi.OrderID " +
                "GROUP BY o.OrderID";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createViewSql);
            System.out.println("View 'v_order_totals' ensured successfully!");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to ensure v_order_totals view", e);
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

    public OrderTotals getOrderTotalsById(int orderId) {
        String sql = "SELECT * FROM v_order_totals WHERE OrderID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new OrderTotals(
                            rs.getInt("OrderID"),
                            rs.getDouble("Subtotal"),
                            rs.getDouble("Tax"),
                            rs.getDouble("Total")
                    );
                }
                System.err.println("Warning: No totals found for Order #" + orderId);
                return new OrderTotals(orderId, 0.0, 0.0, 0.0);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch OrderTotals for ID: " + orderId, e);
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
        String sql = "INSERT INTO `Order` (TableID, EmployeeID, Status) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getTableId());
            ps.setInt(2, order.getEmployeeId());
            ps.setString(3, order.getStatus());

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
        String sql = "UPDATE `Order` SET TableID = ?, EmployeeID = ?, CheckoutTime = ?, Status = ? WHERE OrderID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, order.getTableId());
            ps.setInt(2, order.getEmployeeId());
            ps.setTimestamp(3, order.getCheckoutTime() != null ? Timestamp.valueOf(order.getCheckoutTime()) : null);
            ps.setString(4, order.getStatus());
            ps.setInt(5, order.getOrderId());
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

            Order order = getOrderById(orderId);
            if (order == null) {
                throw new IllegalStateException("Order not found");
            }
            if (!"Ready".equals(order.getStatus())) {
                throw new IllegalStateException("Order is not Ready to be paid");
            }

            String updateOrderSql = "UPDATE `Order` SET Status = ?, CheckoutTime = ? WHERE OrderID = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateOrderSql)) {
                ps.setString(1, "Paid");
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setInt(3, orderId);
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
                rs.getString("Status")
        );
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        String sql = "UPDATE `Order` SET Status = ? WHERE OrderID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, orderId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update order status for OrderID: " + orderId + " to " + newStatus, e);
        }
    }
    public List<Order> getServingOrdersWithActiveTables() {
        List<Order> orders = new ArrayList<>();
        String query = """
            SELECT o.oderId , o.tableId, o.status
            FROM orders o
            INNER JOIN tables t ON o.table_id = t.table_id
            WHERE o.status = 'Serving' 
            AND t.status = 'ACTIVE_ORDERS'
            ORDER BY o.CheckoutTime DESC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("OrderId"));
                order.setTableId(rs.getInt("TableId"));
                order.setStatus(rs.getString("Status"));
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error getting serving orders: " + e.getMessage());
            e.printStackTrace();
        }

        return orders;
    }

    public Order getOrderByTableId(int tableId) {
        String sql = "SELECT * FROM `Order` WHERE TableID = ? " +
                "AND Status IN ('Preparing','Ready','Serving') ORDER BY OrderID DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch Order for TableID: " + tableId, e);
        }
    }
}
