package com.btl_oop.Controller.LoginController;

import com.btl_oop.Model.DAO.EmployeeDAO;
import com.btl_oop.Model.Entity.Employee;
import com.btl_oop.Utils.AlertUtils;
import com.btl_oop.Utils.AppConfig;
import com.btl_oop.Utils.SceneUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label signUpTab;
    @FXML
    private Label logInTab;

    private EmployeeDAO employeeDAO = new EmployeeDAO();

    @FXML
    private void onLogin(ActionEvent event) throws IOException {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        Employee emp = employeeDAO.login(user, pass);

        if (emp == null) {
            AlertUtils.showError("Sai tên đăng nhập hoặc mật khẩu!");
            return;
        }

        // Có thể kiểm tra trạng thái
        if ("Inactive".equalsIgnoreCase(emp.getStatus())) {
            AlertUtils.showWarning("Tài khoản đã bị vô hiệu hoá.");
            return;
        }
        if (user.equals("kitchen123")) {
            AlertUtils.showInfo("Đăng nhập beeps");
            SceneUtils.switchTo(event, AppConfig.PATH_KITCHEN_SCREEN);
        } else if ("ADMIN".equalsIgnoreCase(emp.getRole())) {
            AlertUtils.showInfo("Đăng nhập quản trị viên thành công");
            SceneUtils.switchTo(event, AppConfig.PATH_ADMIN_SCREEN);
        } else if ("WAITER".equalsIgnoreCase(emp.getRole())) {
            AlertUtils.showInfo("Đăng nhập nhân viên thành công");
            SceneUtils.switchTo(event, AppConfig.PATH_ORDER_MENU_SCREEN);
        } else if ("MANAGER".equalsIgnoreCase(emp.getRole())) {
            AlertUtils.showInfo("Đăng nhập nhân viên thành công");
            SceneUtils.switchTo(event, AppConfig.PATH_TABLE_MAP);
        }
    }

    @FXML
    private void onClickRegister(MouseEvent event) throws IOException {
        SceneUtils.switchTo(event, AppConfig.PATH_REGISTER_SCREEN);
    }
}
