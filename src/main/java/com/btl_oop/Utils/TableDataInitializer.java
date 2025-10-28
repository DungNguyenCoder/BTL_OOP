package com.btl_oop.Utils;

import com.btl_oop.Model.DAO.RestaurantTableDAO;
import com.btl_oop.Model.Entity.RestaurantTable;
import com.btl_oop.Model.Enum.TableStatus;

import java.sql.Connection;
import java.sql.Statement;

public class TableDataInitializer {
    
    public static void initializeTables() {
        try {
            // Ensure table exists first
            RestaurantTableDAO dao = new RestaurantTableDAO();
            
            // Check if tables already exist
            if (hasTables()) {
                System.out.println("Tables already exist, skipping initialization");
                return;
            }
            
            // Clear and recreate 8 tables
            clearTableData();
            insertSampleTables();
            
            // Verify we have 8 tables
            int count = getTableCount();
            if (count != 8) {
                System.err.println("Warning: Expected 8 tables, but found " + count);
            } else {
                System.out.println("Successfully created 8 tables");
            }
            
        } catch (Exception e) {
            System.err.println("Failed to initialize table data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static boolean hasTables() {
        String sql = "SELECT COUNT(*) FROM RestaurantTable";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Found " + count + " existing tables");
                return count > 0;
            }
        } catch (Exception e) {
            System.err.println("Error checking existing tables: " + e.getMessage());
        }
        return false;
    }
    
    
    private static int getTableCount() {
        String sql = "SELECT COUNT(*) FROM RestaurantTable";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error counting tables: " + e.getMessage());
        }
        return 0;
    }
    
    
    private static void clearTableData() {
        String sql = "DELETE FROM RestaurantTable WHERE TableID > 0";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.err.println("Failed to clear table data: " + e.getMessage());
        }
    }
    
    private static void insertSampleTables() {
        String sql = "INSERT INTO RestaurantTable (TableNumber, Capacity, Status, CurrentOrderId, StatusChangeTime) VALUES " +
                "(1, 4, 'AVAILABLE', NULL, " + System.currentTimeMillis() + "), " +
                "(2, 2, 'AVAILABLE', NULL, " + System.currentTimeMillis() + "), " +
                "(3, 6, 'AVAILABLE', NULL, " + System.currentTimeMillis() + "), " +
                "(4, 4, 'AVAILABLE', NULL, " + System.currentTimeMillis() + "), " +
                "(5, 2, 'AVAILABLE', NULL, " + System.currentTimeMillis() + "), " +
                "(6, 8, 'AVAILABLE', NULL, " + System.currentTimeMillis() + "), " +
                "(7, 4, 'AVAILABLE', NULL, " + System.currentTimeMillis() + "), " +
                "(8, 2, 'AVAILABLE', NULL, " + System.currentTimeMillis() + ")";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            System.out.println("Sample tables inserted successfully!");
            
        } catch (Exception e) {
            System.err.println("Failed to insert sample tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
