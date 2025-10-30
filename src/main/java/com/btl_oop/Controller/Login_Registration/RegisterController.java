package com.btl_oop.Controller.Login_Registration;

import com.btl_oop.Model.DAO.EmployeeDAO;
import com.btl_oop.Model.Entity.Employee;
import com.btl_oop.Utils.AlertUtils;
import com.btl_oop.Utils.AppConfig;
import com.btl_oop.Utils.SceneUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class RegisterController {
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneNumberField;
    @FXML private DatePicker birthdayField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleChoose;
    @FXML private CheckBox agreeCheck;

    @FXML
    public void initialize() {
        roleChoose.getItems().addAll("Manager", "Waiter", "Kitchen");
    }

    @FXML
    public void onRegisterButtonClicked(ActionEvent event) {
        String name = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        LocalDate birthday = birthdayField.getValue();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        String validationError = validateInputs(username, password, confirmPassword, name, phoneNumber, email);
        if (validationError != null) {
            AlertUtils.showError(validationError);
            return;
        }

        //Đăng kí
        EmployeeDAO employeeDAO = new EmployeeDAO();
        String role = roleChoose.getSelectionModel().getSelectedItem();

        if (employeeDAO.isEmployeeExists(username, email, phoneNumber)) {
            AlertUtils.showError("Account already exists!");
            return;
        }

        Employee employee = new Employee(0, username, password, name, email, phoneNumber, birthday, role, "Active");
        employeeDAO.insertEmployee(employee);

        AlertUtils.showInfo("Registration successful!");

        try {
            SceneUtils.switchTo(event, AppConfig.PATH_LOGIN_SCREEN);;
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Unable to redirect to the login screen.");
        }
    }

    @FXML
    public void onBackToLoginClicked(MouseEvent event) throws IOException {
        SceneUtils.switchTo(event, AppConfig.PATH_LOGIN_SCREEN);
    }

    private String validateInputs(String username, String password, String confirmpassword,
                                  String name, String numberphone, String email) {
        if (username.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()
                || name.isEmpty() || email.isEmpty() || numberphone.isEmpty()) {
            return "Please fill in all required information!";
        }
        if (username.length() < 6) {
            return "Username must be at least 6 characters long!";
        }
        if (username.contains(" ")) {
            return "Username must not contain spaces!";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters long!";
        }
        if (!password.equals(confirmpassword)) {
            return "Password and confirm password do not match!";
        }
        if (!agreeCheck.isSelected()) {
            return "You must agree to the terms to continue!";
        }
        if (!isValidEmail(email)) {
            return "Invalid email address!";
        }
        if (!isValidPhone(numberphone)) {
            return "Invalid phone number!";
        }
        return null;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        String phoneRegex = "^\\d{9,12}$"; // 9-12 digits
        return Pattern.compile(phoneRegex).matcher(phone).matches();
    }
}