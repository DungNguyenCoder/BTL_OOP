package com.btl_oop.Model.DAO;

import com.btl_oop.Model.Entity.RestaurantTable;
import com.btl_oop.Model.Enum.TableStatus;
import com.btl_oop.Utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantTableDAO {
   private void ensureTable()
   {
       try (Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement()) {
           
           // First, create table if not exists with basic schema
           String createTableSql = "CREATE TABLE IF NOT EXISTS RestaurantTable(" +
                   "TableID INT AUTO_INCREMENT PRIMARY KEY,"+
                   "TableNumber INT NOT NULL UNIQUE,"+
                   "Capacity INT CHECK (Capacity > 0)," +
                   "Status ENUM('Available','Occupied','Reserved') DEFAULT 'Available')";
           stmt.execute(createTableSql);
           
           try {
               String checkColumnSql = "SELECT CurrentOrderId FROM RestaurantTable LIMIT 1";
               stmt.executeQuery(checkColumnSql);
               System.out.println("Table already has new schema");
           } catch (SQLException e) {
               System.out.println("Updating table schema...");
               
               // Add new columns
               stmt.execute("ALTER TABLE RestaurantTable ADD COLUMN CurrentOrderId VARCHAR(50) NULL AFTER Status");
               stmt.execute("ALTER TABLE RestaurantTable ADD COLUMN StatusChangeTime BIGINT DEFAULT 0 AFTER CurrentOrderId");
               
               // Update Status enum
               stmt.execute("ALTER TABLE RestaurantTable MODIFY COLUMN Status ENUM('AVAILABLE','OCCUPIED','CLEANING','ACTIVE_ORDERS','READY_TO_SERVE') DEFAULT 'AVAILABLE'");
               
               System.out.println("Table schema updated successfully!");
           }
           
       } catch (SQLException e) {
           throw new RuntimeException("Failed to ensure RestaurantTable table", e);
       }
   }
    public boolean updateTableStatus(int tableId, TableStatus status) {
        String sql = "UPDATE RestaurantTable SET Status = ?, StatusChangeTime = ? WHERE TableID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, System.currentTimeMillis());
            ps.setInt(3, tableId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update Table status for TableID: " + tableId, e);
        }
    }

    public boolean updateTableStatus(int tableId, TableStatus status, String orderId) {
        String sql = "UPDATE RestaurantTable SET Status = ?, CurrentOrderId = ?, StatusChangeTime = ? WHERE TableID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, orderId);
            ps.setLong(3, System.currentTimeMillis());
            ps.setInt(4, tableId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update Table status for TableID: " + tableId, e);
        }
    }
    public List<RestaurantTable> getAllTables() {
        ensureTable(); // Ensure table exists and has correct schema
        List<RestaurantTable> tables = new ArrayList<>();
        String sql = "SELECT * FROM RestaurantTable ORDER BY TableNumber";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String statusStr = rs.getString("Status");
                TableStatus status;
                
                // Handle both old and new status formats
                try {
                    status = TableStatus.valueOf(statusStr);
                } catch (IllegalArgumentException e) {
                    // Convert old format to new format
                    switch (statusStr) {
                        case "Available":
                            status = TableStatus.AVAILABLE;
                            break;
                        case "Occupied":
                            status = TableStatus.OCCUPIED;
                            break;
                        case "Reserved":
                            status = TableStatus.AVAILABLE; // Map Reserved to Available
                            break;
                        default:
                            status = TableStatus.AVAILABLE;
                    }
                }
                
                String orderId = rs.getString("CurrentOrderId");
                long statusChangeTime = rs.getLong("StatusChangeTime");
                
                RestaurantTable table = new RestaurantTable(
                        rs.getInt("TableID"),
                        rs.getInt("TableNumber"),
                        rs.getInt("Capacity"),
                        status,
                        orderId
                );
                table.setStatusChangeTime(statusChangeTime);
                tables.add(table);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load tables", e);
        }
        return tables;
    }

    public RestaurantTable getTableById(int tableId) {
        String sql = "SELECT * FROM RestaurantTable WHERE TableID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TableStatus status = TableStatus.valueOf(rs.getString("Status"));
                    String orderId = rs.getString("CurrentOrderId");
                    long statusChangeTime = rs.getLong("StatusChangeTime");
                    
                    RestaurantTable table = new RestaurantTable(
                            rs.getInt("TableID"),
                            rs.getInt("TableNumber"),
                            rs.getInt("Capacity"),
                            status,
                            orderId
                    );
                    table.setStatusChangeTime(statusChangeTime);
                    return table;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load table with ID: " + tableId, e);
        }
        return null;
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
            throw new RuntimeException("Unable to retrieve TableID for TableNumber: " + tableNumber, e);
        }
    }


}
