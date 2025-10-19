package com.btl_oop.Controller.LoginController;

import com.btl_oop.Model.DAO.EmployeeDAO;
import com.btl_oop.Model.Entity.Employee;
import com.btl_oop.Model.Entity.User;
import com.btl_oop.Model.Store.UserStore;
import com.btl_oop.Utils.AlertUtils;
import com.btl_oop.Utils.AppConfig;
import com.btl_oop.Utils.SceneUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.util.List;

public class LoginController {
    @FXML
    private TextField _usernameField;
    @FXML
    private PasswordField _password;

    private EmployeeDAO employeeDAO = new EmployeeDAO();

//    @FXML
//    private void initialize() {
//        String plain = "admin";
//        String hash = BCrypt.hashpw(plain, BCrypt.gensalt(12));
//        System.out.println(hash);
//    }
    @FXML
    private void onLogin(ActionEvent event) throws IOException {
        String user = _usernameField.getText();
        String pass = _password.getText();

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

        if ("ADMIN".equalsIgnoreCase(emp.getRole())) {
            AlertUtils.showInfo("Đăng nhập quản trị viên thành công");
//            SceneUtils.switchTo(event, AppConfig.PATH_ADMIN_SCREEN);
        } else {
            AlertUtils.showInfo("Đăng nhập nhân viên thành công");
            SceneUtils.switchTo(event, AppConfig.PATH_ORDER_MENU_SCREEN);
        }
    }

    @FXML
    private void onClickRegister(MouseEvent event) throws IOException {
        SceneUtils.switchTo(event, AppConfig.PATH_REGISTER_SCREEN);
    }
}