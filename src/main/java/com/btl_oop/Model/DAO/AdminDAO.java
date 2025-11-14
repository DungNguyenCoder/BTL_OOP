package com.btl_oop.Model.DAO;

import com.btl_oop.Model.Entity.Admin;
import com.btl_oop.Utils.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    public AdminDAO() {
        ensureTable();
    }

    private void ensureTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Admin (" +
                "AdminID INT AUTO_INCREMENT PRIMARY KEY, " +
                "UserName VARCHAR(50) UNIQUE NOT NULL, " +
                "Password VARCHAR(100) NOT NULL, " +
                "FullName NVARCHAR(100) NOT NULL, " +
                "Email VARCHAR(100), " +
                "PhoneNumber VARCHAR(15), " +
                "DateOfBirth DATE, " +
                "Status ENUM('Active','Inactive') DEFAULT 'Active', " +
                "CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "LastLogin DATETIME NULL)";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Admin table created or verified successfully!");
        } catch (SQLException e) {
            throw new RuntimeException("Error creating Admin table.", e);
        }
    }

    public Admin getAdminById(int adminId) {
        String sql = "SELECT * FROM Admin WHERE AdminID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving admin with ID: " + adminId, e);
        }
    }


    public Admin login(String userName, String password) {
        String sql = "SELECT * FROM Admin WHERE UserName = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("Password");
                    if (BCrypt.checkpw(password, storedPassword)) {
                        Admin admin = mapResultSetToAdmin(rs);
                        updateLastLogin(admin.getAdminId());
                        return admin;
                    }
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error logging in with Username: " + userName, e);
        }
    }

    private void updateLastLogin(int adminId) {
        String sql = "UPDATE Admin SET LastLogin = NOW() WHERE AdminID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Warning: Could not update last login time for admin " + adminId);
        }
    }


    private Admin mapResultSetToAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminId(rs.getInt("AdminID"));
        admin.setUserName(rs.getString("UserName"));
        admin.setPassword(rs.getString("Password"));
        admin.setFullName(rs.getString("FullName"));
        admin.setEmail(rs.getString("Email"));
        admin.setPhoneNumber(rs.getString("PhoneNumber"));
        admin.setDateOfBirth(rs.getObject("DateOfBirth") != null ? rs.getDate("DateOfBirth").toLocalDate() : null);
        admin.setStatus(rs.getString("Status"));
        admin.setCreatedAt(rs.getTimestamp("CreatedAt") != null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null);
        admin.setLastLogin(rs.getTimestamp("LastLogin") != null ? rs.getTimestamp("LastLogin").toLocalDateTime() : null);
        return admin;
    }

}