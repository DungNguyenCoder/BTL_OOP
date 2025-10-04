package com.btl_oop.Controller;

import com.btl_oop.HelloApplication;
import com.btl_oop.Model.Entity.User;
import com.btl_oop.Model.Store.UserStore;
import com.btl_oop.Utils.AppConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class LoginController {
    @FXML
    private TextField _email;
    @FXML
    private PasswordField _password;

    @FXML
    private void onLogin() {
        List<User> users = UserStore.getAllUsers();

        String user = _email.getText();
        String pass = _password.getText();

        User found = users.stream()
                .filter(u -> u.getEmail().equals(user))
                .findFirst()
                .orElse(null);

        if (found == null) {
            System.out.println("Account not found");
            return;
        }
        
        if (found.getPassword().equals(pass)) {
            System.out.println("Login successful");
        } else {
            System.out.println("Wrong password");
        }
    }

    @FXML
    void onClickRegister(MouseEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(AppConfig.PATH_REGISTER_SCREEN));
        Parent loginRoot = loader.load();
        Scene loginScene = new Scene(loginRoot);
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(loginScene);
        stage.show();
    }
}