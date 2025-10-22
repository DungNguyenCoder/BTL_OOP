package com.btl_oop.Model.DAO;

import com.btl_oop.Model.Entity.RestaurantTable;
import com.btl_oop.Utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantTableDAO {
   private void ensureTable()
   {
       String sql ="CREATE TABLE  IF NOT EXISTS RestaurantTable(" +
               "TableID INT AUTO_INCREMENT PRIMARY KEY,"+
               "TableNumber INT NOT NULL UNIQUE,"+
               "Capacity INT CHECK (Capacity > 0)" +
               "Status ENUM('Available','Occupied','Reserved') DEFAULT 'Available')";
       try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement()) {
           stmt.execute(sql);
           System.out.println("Table 'RestaurantTable' ensured successfully!");
       } catch (SQLException e) {
           throw new RuntimeException("Failed to ensure RestaurantTable table", e);
       }
   }
    public boolean updateTableStatus(int tableId, String status) {
        String sql = "UPDATE RestaurantTable SET Status = ? WHERE TableID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, tableId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update Table status for TableID: " + tableId, e);
        }

    }
    public List<RestaurantTable> getAllTables() {
        List<RestaurantTable> tables = new ArrayList<>();
        String sql = "SELECT * FROM RestaurantTable ORDER BY TableNumber";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tables.add(new RestaurantTable(
                        rs.getInt("TableID"),
                        rs.getInt("TableNumber"),
                        rs.getInt("Capacity"),
                        rs.getString("Status")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load tables", e);
        }
        return tables;
    }
    public int getTableIdByNumber(int tableNumber) {
        String sql = "SELECT TableID FROM RestaurantTable WHERE TableNumber = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TableID");
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Không thể lấy TableID cho TableNumber: " + tableNumber, e);
        }
    }


}
