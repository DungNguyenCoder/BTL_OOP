package com.btl_oop.Controller.Login_Registration;

import com.btl_oop.Model.DAO.AdminDAO;
import com.btl_oop.Model.DAO.EmployeeDAO;
import com.btl_oop.Model.Entity.Admin;
import com.btl_oop.Model.Entity.Employee;
import com.btl_oop.Utils.AlertUtils;
import com.btl_oop.Utils.AppConfig;
import com.btl_oop.Utils.SceneUtils;
import com.btl_oop.Utils.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private final AdminDAO adminDAO = new AdminDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    @FXML
    private void onLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            AlertUtils.showError("Please enter both username and password!");
            return;
        }

        Admin admin = adminDAO.login(username, password);
        if (admin != null) {
            handleAdminLogin(admin, event);
            return;
        }

        Employee employee = employeeDAO.login(username, password);
        if (employee != null) {
            handleEmployeeLogin(employee, event);
            return;
        }

        AlertUtils.showError("Incorrect username or password!");

        passwordField.clear();
    }

    private void handleAdminLogin(Admin admin, ActionEvent event) throws IOException {
        if ("Inactive".equalsIgnoreCase(admin.getStatus())) {
            AlertUtils.showWarning("This admin account has been disabled.");
            passwordField.clear();
            return;
        }
        SessionManager.getInstance().setCurrentAdmin(admin);
        SessionManager.getInstance().setUserRole("ADMIN");

        AlertUtils.showInfo("Welcome, Admin " + admin.getFullName() + "!");
        SceneUtils.switchTo(event, AppConfig.PATH_ADMIN_SCREEN);
    }

    private void handleEmployeeLogin(Employee employee, ActionEvent event) throws IOException {
        if ("Terminated".equalsIgnoreCase(employee.getStatus())) {
            AlertUtils.showWarning("This employee account has been terminated.");
            passwordField.clear();
            return;
        }
        SessionManager.getInstance().setCurrentEmployee(employee);
        SessionManager.getInstance().setUserRole(employee.getRole());

        String destinationPath;
        String welcomeMessage;

        switch (employee.getRole().toUpperCase()) {
            case "MANAGER":
                destinationPath = AppConfig.PATH_TABLE_MAP;
                welcomeMessage = "Manager login successful. Welcome, " + employee.getFullName() + "!";
                break;

            case "WAITER":
                destinationPath = AppConfig.PATH_ORDER_MENU_SCREEN;
                welcomeMessage = "Waiter login successful. Welcome, " + employee.getFullName() + "!";
                break;

            case "KITCHEN":
                destinationPath = AppConfig.PATH_KITCHEN_SCREEN;
                welcomeMessage = "Kitchen staff login successful. Welcome, " + employee.getFullName() + "!";
                break;

            default:
                AlertUtils.showError("Invalid employee role: " + employee.getRole());
                return;
        }

        AlertUtils.showInfo(welcomeMessage);
        SceneUtils.switchTo(event, destinationPath);
    }

    @FXML
    private void onClickRegister(MouseEvent event) throws IOException {
        SceneUtils.switchTo(event, AppConfig.PATH_REGISTER_SCREEN);
    }
}
