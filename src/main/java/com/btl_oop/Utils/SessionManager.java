package com.btl_oop.Utils;

import com.btl_oop.Model.Entity.Admin;
import com.btl_oop.Model.Entity.Employee;

public class SessionManager {
    private static SessionManager instance;

    private Admin currentAdmin;
    private Employee currentEmployee;
    private String userRole;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
        this.currentEmployee = null;
    }

    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public boolean isAdmin() {
        return currentAdmin != null;
    }

    public void setCurrentEmployee(Employee employee) {
        this.currentEmployee = employee;
        this.currentAdmin = null;
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public boolean isEmployee() {
        return currentEmployee != null;
    }

    public void setUserRole(String role) {
        this.userRole = role;
    }

    public String getUserRole() {
        return userRole;
    }

    public boolean hasRole(String role) {
        return role != null && role.equalsIgnoreCase(this.userRole);
    }

    public String getCurrentUserName() {
        if (currentAdmin != null) {
            return currentAdmin.getFullName();
        } else if (currentEmployee != null) {
            return currentEmployee.getFullName();
        }
        return "Unknown User";
    }

    public int getCurrentUserId() {
        if (currentAdmin != null) {
            return currentAdmin.getAdminId();
        } else if (currentEmployee != null) {
            return currentEmployee.getEmployeeId();
        }
        return -1;
    }

    public void logout() {
        currentAdmin = null;
        currentEmployee = null;
        userRole = null;
    }

    public boolean isLoggedIn() {
        return currentAdmin != null || currentEmployee != null;
    }
}