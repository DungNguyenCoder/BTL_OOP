package com.btl_oop.Model.DAO;

import com.btl_oop.Model.Entity.Employee;
import com.btl_oop.Utils.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    public EmployeeDAO() {
        ensureTable();
    }

    private void ensureTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Employee (" +
                "EmployeeID INT AUTO_INCREMENT PRIMARY KEY, " +
                "UserName VARCHAR(50) UNIQUE NOT NULL, " +
                "Password VARCHAR(100) NOT NULL, " +
                "FullName NVARCHAR(100) NOT NULL, " +
                "Email VARCHAR(100), " +
                "PhoneNumber VARCHAR(15), " +
                "DateOfBirth DATE, " +
                "Role ENUM('Manager','Waiter', 'Kitchen') DEFAULT 'Waiter', " +
                "Status ENUM('Active','Terminated') DEFAULT 'Active')";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Employee table created or verified successfully!");
        } catch (SQLException e) {
            throw new RuntimeException("Error creating Employee table.", e);
        }
    }

    public boolean insertEmployee(Employee employee) {
        String sql = "INSERT INTO Employee (UserName, Password, FullName, Email, PhoneNumber, DateOfBirth, Role, Status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Mã hóa mật khẩu
            String hashedPassword = BCrypt.hashpw(employee.getPassword(), BCrypt.gensalt());
            ps.setString(1, employee.getUserName());
            ps.setString(2, hashedPassword);
            ps.setString(3, employee.getFullName());
            ps.setString(4, employee.getEmail());
            ps.setString(5, employee.getPhoneNumber());
            ps.setObject(6, employee.getDateOfBirth() != null ? Date.valueOf(employee.getDateOfBirth()) : null);
            ps.setString(7, employee.getRole());
            ps.setString(8, employee.getStatus());

            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            try (ResultSet key = ps.getGeneratedKeys()) {
                if (key.next()) {
                    employee.setEmployeeId(key.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding employee: " + employee.getUserName(), e);
        }
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employee ORDER BY EmployeeID";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employee list.", e);
        }
        return employees;
    }

    public Employee getEmployeeById(int employeeId) {
        String sql = "SELECT * FROM Employee WHERE EmployeeID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmployee(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employee with ID: " + employeeId, e);
        }
    }
    public int getEmployeeIdById(int employeeId) {
        String sql = "SELECT EmployeeID FROM Employee WHERE EmployeeID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("EmployeeID");
                }
                return 0; // Nhân viên không tồn tại
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking EmployeeID: " + employeeId, e);
        }
    }
    public int getEmployeeIdByName(String fullName) {
        String sql = "SELECT EmployeeID FROM Employee WHERE FullName = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("EmployeeID");
                }
                return 0; // Nhân viên không tồn tại
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving EmployeeID for FullName: " + fullName, e);
        }
    }

    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE Employee SET UserName = ?, Password = ?, FullName = ?, Email = ?, " +
                "PhoneNumber = ?, DateOfBirth = ?, Role = ?, Status = ? WHERE EmployeeID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String hashedPassword = employee.getPassword().startsWith("$2a$")
                    ? employee.getPassword()
                    : BCrypt.hashpw(employee.getPassword(), BCrypt.gensalt());
            ps.setString(1, employee.getUserName());
            ps.setString(2, hashedPassword);
            ps.setString(3, employee.getFullName());
            ps.setString(4, employee.getEmail());
            ps.setString(5, employee.getPhoneNumber());
            ps.setObject(6, employee.getDateOfBirth() != null ? Date.valueOf(employee.getDateOfBirth()) : null);
            ps.setString(7, employee.getRole());
            ps.setString(8, employee.getStatus());
            ps.setInt(9, employee.getEmployeeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating employee: " + employee.getEmployeeId(), e);
        }
    }

    public boolean deleteEmployee(int employeeId) {
        String sql = "DELETE FROM Employee WHERE EmployeeID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting employee: " + employeeId, e);
        }
    }

    public Employee login(String userName, String password) {
        String sql = "SELECT * FROM Employee WHERE UserName = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("Password");
                    if (BCrypt.checkpw(password, storedPassword)) {
                        return mapResultSetToEmployee(rs);
                    }
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error logging in with Username: " + userName, e);
        }
    }

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getInt("EmployeeID"),
                rs.getString("UserName"),
                rs.getString("Password"),
                rs.getString("FullName"),
                rs.getString("Email"),
                rs.getString("PhoneNumber"),
                rs.getObject("DateOfBirth") != null ? rs.getDate("DateOfBirth").toLocalDate() : null,
                rs.getString("Role"),
                rs.getString("Status")
        );
    }

    public boolean isEmployeeExists(String username, String email, String phoneNumber) {
        String sql = """
        SELECT COUNT(*) FROM employee
        WHERE Username = ? OR Email = ? OR PhoneNumber = ?
    """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, phoneNumber);;
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}