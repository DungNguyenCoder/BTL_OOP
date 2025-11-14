package com.btl_oop.Model.DAO;

import com.btl_oop.Model.Entity.Dish;
import com.btl_oop.Utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DishDAO {
    private final AdminDAO adminDAO;

    public DishDAO() {
        ensureTable();
        this.adminDAO = new AdminDAO();
    }

    private void ensureTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Dish (" +
                "DishID INT PRIMARY KEY AUTO_INCREMENT, " +
                "Name VARCHAR(100) NOT NULL, " +
                "Price DECIMAL(10,2) NOT NULL CHECK (Price >= 0), " +
                "Description TEXT, " +
                "PrepareTime INT CHECK (PrepareTime >= 0), " +
                "Category VARCHAR(50), " +
                "ImageURL VARCHAR(255), " +
                "isPopular BOOLEAN DEFAULT True)";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'Dish' ensured successfully!");
        } catch (SQLException e) {
            throw new RuntimeException(" Failed to ensure Dish table", e);
        }
    }

    private boolean isValidAdmin(int adminId) {
        if (adminId <= 0) {
            return false;
        }

        String sql = "SELECT AdminID FROM Admin WHERE AdminID = ? AND Status = 'Active'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error validating admin: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // READ OPERATIONS (Không cần quyền Admin)
    // =====================================================

    public Dish getDishById(int dishId) {
        String sql = "SELECT * FROM Dish WHERE DishID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Dish(
                            rs.getInt("DishID"),
                            rs.getString("Name"),
                            rs.getDouble("Price"),
                            rs.getString("Description"),
                            rs.getInt("PrepareTime"),
                            rs.getString("Category"),
                            rs.getString("ImageURL"),
                            rs.getBoolean("isPopular")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve Dish with ID: " + dishId, e);
        }
    }

    public List<Dish> getAllDish() {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT * FROM Dish ORDER BY DishID";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Dish a = new Dish(
                        rs.getInt("DishID"),
                        rs.getString("Name"),
                        rs.getDouble("Price"),
                        rs.getString("Description"),
                        rs.getInt("PrepareTime"),
                        rs.getString("Category"),
                        rs.getString("ImageURL"),
                        rs.getBoolean("isPopular")
                );
                dishes.add(a);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load dishes", e);
        }

        return dishes;
    }

    public List<Dish> getDishesByCategory(String category) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        String sql = "SELECT * FROM Dish WHERE Category = ? ORDER BY DishID";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Dish dish = new Dish(
                            rs.getInt("DishID"),
                            rs.getString("Name"),
                            rs.getDouble("Price"),
                            rs.getString("Description"),
                            rs.getInt("PrepareTime"),
                            rs.getString("Category"),
                            rs.getString("ImageURL"),
                            rs.getBoolean("isPopular")
                    );
                    dishes.add(dish);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load dishes", e);
        }
        return dishes;
    }

    // =====================================================
    // WRITE OPERATIONS (Chỉ Admin)
    // =====================================================

    public boolean insertDish(Dish dish, int adminId) throws SQLException {
        if (!isValidAdmin(adminId)) {
            throw new SecurityException("Access denied. Only administrators can add dishes. Admin ID: " + adminId);
        }
        String sql = "INSERT INTO Dish(Name,Price,Description,PrepareTime,Category,ImageURL,isPopular) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dish.getName());
            ps.setDouble(2, dish.getPrice());
            ps.setString(3, dish.getDescription());
            ps.setInt(4, dish.getPrepareTime());
            ps.setString(5, dish.getCategory());
            ps.setString(6, dish.getImageUrl());
            ps.setBoolean(7, dish.isPopular());

            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet key = ps.getGeneratedKeys()) {
                if (key.next()) {
                    dish.setDishId(key.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert Dish", e);
        }
    }

    public boolean updateDish(Dish dish, int adminId) {
        if (!isValidAdmin(adminId)) {
            throw new SecurityException("Access denied. Only administrators can update dishes. Admin ID: " + adminId);
        }
        String sql = "UPDATE Dish SET Name = ?, Price = ?, Description = ?, PrepareTime = ?, Category = ?, ImageURL = ?, isPopular = ? WHERE DishID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dish.getName());
            ps.setDouble(2, dish.getPrice());
            ps.setString(3, dish.getDescription());
            ps.setInt(4, dish.getPrepareTime());
            ps.setString(5, dish.getCategory());
            ps.setString(6, dish.getImageUrl());
            ps.setBoolean(7, dish.isPopular());
            ps.setInt(8, dish.getDishId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update Dish", e);
        }
    }

    public boolean deleteDish(Dish dish) {
        String sql = "DELETE FROM Dish WHERE DishID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dish.getDishId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete Dish", e);
        }
    }
}