package com.btl_oop;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class LoginController {
    @FXML
    private TextField _email;
    @FXML
    private PasswordField _password;

    private List<User> users;

    @FXML
    private void initialize() {
        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream("/com/btl_oop/users.json"))) {

            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, List<User>>>(){}.getType();
            Map<String, List<User>> data = gson.fromJson(reader, type);
            users = data.get("users");

            System.out.println("Load data from file");
        } catch (Exception e) {
            System.out.println("Load file error");
            e.printStackTrace();
        }
    }

    @FXML
    private void onLogin() {
        String user = _email.getText();
        String pass = _password.getText();

        User found = users.stream()
                .filter(u -> u.getUsername().equals(user))
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
}