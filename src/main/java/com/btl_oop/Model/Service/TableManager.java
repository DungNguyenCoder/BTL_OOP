package com.btl_oop.Model.Service;

import com.btl_oop.Model.DAO.RestaurantTableDAO;
import com.btl_oop.Model.Entity.RestaurantTable;
import com.btl_oop.Model.Enum.TableStatus;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList; // Thêm import này
import java.util.Comparator; // Thêm import này

public class TableManager {
    private static TableManager instance;
    private final RestaurantTableDAO tableDAO;
    private final Map<Integer, RestaurantTable> tableCache;

    private TableManager() {
        this.tableDAO = new RestaurantTableDAO();
        this.tableCache = new ConcurrentHashMap<>();
        loadTables();
    }

    public static synchronized TableManager getInstance() {
        if (instance == null) {
            instance = new TableManager();
        }
        return instance;
    }

    private void loadTables() {
        try {
            List<RestaurantTable> tables = tableDAO.getAllTables();
            System.out.println("TableManager: Loading " + tables.size() + " tables from database");
            for (RestaurantTable table : tables) {
                tableCache.put(table.getTableId(), table);
                System.out.println("TableManager: Loaded table " + table.getTableNumber() + " with status " + table.getStatus());
            }
        } catch (Exception e) {
            System.err.println("TableManager: Error loading tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<RestaurantTable> getAllTables() {
        return tableDAO.getAllTables();
    }

    // Trả về danh sách các bàn từ cache, được sắp xếp
    public List<RestaurantTable> getAllCachedTables() {
        List<RestaurantTable> sortedTables = new ArrayList<>(tableCache.values());
        sortedTables.sort(Comparator.comparingInt(RestaurantTable::getTableNumber));
        return sortedTables;
    }

    public RestaurantTable getTable(int tableId) {
        return tableCache.get(tableId);
    }

    public RestaurantTable getTableByNumber(int tableNumber) {
        System.out.println("TableManager: Looking for table number " + tableNumber);
        System.out.println("TableManager: Cache size: " + tableCache.size());

        for (RestaurantTable table : tableCache.values()) {
            System.out.println("TableManager: Checking table " + table.getTableNumber() + " (ID: " + table.getTableId() + ")");
            if (table.getTableNumber() == tableNumber) {
                System.out.println("TableManager: Found table " + tableNumber);
                return table;
            }
        }
        System.out.println("TableManager: Table " + tableNumber + " not found in cache");
        return null;
    }

    public boolean changeTableStatus(int tableId, TableStatus newStatus) {
        RestaurantTable table = tableCache.get(tableId);
        if (table == null) {
            return false;
        }

        if (!table.getStatus().canTransitionTo(newStatus)) {
            return false;
        }

        boolean success = tableDAO.updateTableStatus(tableId, newStatus);
        if (success) {
            table.setStatus(newStatus);
            tableCache.put(tableId, table);
        }
        return success;
    }

    public boolean changeTableStatus(int tableId, TableStatus newStatus, String orderId) {
        RestaurantTable table = tableCache.get(tableId);
        if (table == null) {
            return false;
        }

        if (!table.getStatus().canTransitionTo(newStatus)) {
            return false;
        }

        boolean success = tableDAO.updateTableStatus(tableId, newStatus, orderId);
        if (success) {
            table.setStatus(newStatus);
            table.setCurrentOrderId(orderId);
            tableCache.put(tableId, table);
        }
        return success;
    }

    public boolean seatGuests(int tableId) {
        return changeTableStatus(tableId, TableStatus.OCCUPIED);
    }

    public boolean startOrder(int tableId, String orderId) {
        return changeTableStatus(tableId, TableStatus.ACTIVE_ORDERS, orderId);
    }

    public boolean finishCooking(int tableId) {
        return changeTableStatus(tableId, TableStatus.READY_TO_SERVE);
    }

    public boolean finishServing(int tableId) {
        return changeTableStatus(tableId, TableStatus.CLEANING);
    }

    public boolean markCleaned(int tableId) {
        // Hàm này sẽ tự động cập nhật cả DB và cache
        return changeTableStatus(tableId, TableStatus.AVAILABLE, null);
    }

    public Map<TableStatus, Integer> getTableStatusCounts() {
        Map<TableStatus, Integer> counts = new HashMap<>();
        for (TableStatus status : TableStatus.values()) {
            counts.put(status, 0);
        }

        for (RestaurantTable table : tableCache.values()) {
            counts.put(table.getStatus(), counts.get(table.getStatus()) + 1);
        }

        return counts;
    }

    public void refreshTables() {
        tableCache.clear();
        loadTables();
    }

    public String getTimeSinceStatusChange(int tableId) {
        RestaurantTable table = tableCache.get(tableId);
        if (table == null) {
            return "Unknown";
        }

        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - table.getStatusChangeTime();
        long minutes = timeDiff / (1000 * 60);

        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + "m ago";
        } else {
            long hours = minutes / 60;
            return hours + "h ago";
        }
    }
}