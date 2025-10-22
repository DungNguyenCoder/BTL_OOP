package com.btl_oop.Controller.LoginController;

import com.btl_oop.Model.Entity.User;
import com.btl_oop.Model.Store.UserStore;
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
import java.util.List;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML private Label signUpTab;
    @FXML private Label logInTab;

    @FXML
    private void onLogin(ActionEvent event) throws IOException {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.equals("admin") && pass.equals("admin")) {
            AlertUtils.showInfo("Đăng nhập quản trị viên thành công");
            SceneUtils.switchTo(event, AppConfig.PATH_ADMIN_SCREEN);
            System.out.println("Login as admin");
            return;
        }

        List<User> users = UserStore.getAllUsers();
        User found = users.stream()
                .filter(u -> u.getUsername().equals(user))
                .findFirst()
                .orElse(null);

        if (found == null) {
            AlertUtils.showError("Tài khoản không tồn tại");
            System.out.println("Account not found");
            return;
        }
        
        if (found.getPassword().equals(pass)) {
            AlertUtils.showInfo("Đăng nhập thành công");
            SceneUtils.switchTo(event, AppConfig.PATH_ORDER_MENU_SCREEN);
            System.out.println("Login successful");
        } else {
            AlertUtils.showError("Sai mật khẩu");
            System.out.println("Wrong password");
        }
    }

    @FXML
    private void onClickRegister(MouseEvent event) throws IOException {
        SceneUtils.switchTo(event, AppConfig.PATH_REGISTER_SCREEN);
    }

    @FXML
    private void switchToSignUp(MouseEvent event) {
//        signUpTab.getStyleClass().remove("toggle-inactive");
//        signUpTab.getStyleClass().add("toggle-active");
//
//        logInTab.getStyleClass().remove("toggle-active");
//        logInTab.getStyleClass().add("toggle-inactive");
//
//        System.out.println("Switched to Sign Up mode");
    }

    @FXML
    private void switchToLogIn(MouseEvent event) {
//        logInTab.getStyleClass().remove("toggle-inactive");
//        logInTab.getStyleClass().add("toggle-active");
//
//        signUpTab.getStyleClass().remove("toggle-active");
//        signUpTab.getStyleClass().add("toggle-inactive");
//
//        System.out.println("Switched to Log In mode");
    }
}