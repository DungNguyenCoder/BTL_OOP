package com.btl_oop.Model.DAO;

import com.btl_oop.Model.Entity.OrderItem;
import com.btl_oop.Utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {
    public OrderItemDAO() {
        ensureTable();
    }

    private void ensureTable() {
        String sql = "CREATE TABLE IF NOT EXISTS OrderItem (" +
                "OrderItemID INT AUTO_INCREMENT PRIMARY KEY, " +
                "OrderID INT NOT NULL, " +
                "DishID INT NOT NULL, " +
                "Quantity INT NOT NULL CHECK (Quantity > 0), " +
                "FOREIGN KEY (OrderID) REFERENCES `Order`(OrderID) " +
                "ON DELETE CASCADE ON UPDATE CASCADE, " +
                "FOREIGN KEY (DishID) REFERENCES Dish(DishID) " +
                "ON DELETE RESTRICT ON UPDATE CASCADE)";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Đã tạo hoặc kiểm tra bảng OrderItem!");
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tạo bảng OrderItem", e);
        }
    }

    public boolean insertOrderItem(OrderItem orderItem) {
        String sql = "INSERT INTO OrderItem (OrderID, DishID, Quantity) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, orderItem.getOrderId());
            ps.setInt(2, orderItem.getDishId());
            ps.setInt(3, orderItem.getQuantity());

            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet key = ps.getGeneratedKeys()) {
                if (key.next()) {
                    orderItem.setOrderItemId(key.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm OrderItem cho OrderID: " + orderItem.getOrderId(), e);
        }
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM OrderItem WHERE OrderID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new OrderItem(
                            rs.getInt("OrderItemID"),
                            rs.getInt("OrderID"),
                            rs.getInt("DishID"),
                            rs.getInt("Quantity")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy OrderItems cho OrderID: " + orderId, e);
        }
        return items;
    }

    public boolean updateOrderItem(OrderItem orderItem) {
        String sql = "UPDATE OrderItem SET Quantity = ? WHERE OrderItemID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderItem.getQuantity());
            ps.setInt(2, orderItem.getOrderItemId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật OrderItem: " + orderItem.getOrderItemId(), e);
        }
    }

    public boolean deleteOrderItem(int orderItemId) {
        String sql = "DELETE FROM OrderItem WHERE OrderItemID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderItemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa OrderItem: " + orderItemId, e);
        }
    }

    //thêm lấy món ăn từ order theo id
}