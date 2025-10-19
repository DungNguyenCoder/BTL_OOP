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
import javafx.scene.control.*;

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
    @FXML private CheckBox checkOrderRole;
    @FXML private CheckBox agreeCheck;

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

        //Đang ki
        EmployeeDAO employeeDAO = new EmployeeDAO();
        String role = "Manager";
        if(checkOrderRole.isSelected()) {
            role = "Waiter";
        }

        if (employeeDAO.isEmployeeExists(username)) {
            AlertUtils.showError("Tài khoản đã tồn tại!");
            return;
        }

        Employee employee = new Employee(0, username, password, name, email, phoneNumber, birthday, role, "Active");
        employeeDAO.insertEmployee(employee);

        AlertUtils.showInfo("Đăng ký thành công!");

        try {
            SceneUtils.switchTo(event, AppConfig.PATH_LOGIN_SCREEN);;
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtils.showError("Không thể chuyển về màn hình đăng nhập: ");
        }
    }

    @FXML
    public void onBackToLoginClicked(ActionEvent event) throws IOException {
        SceneUtils.switchTo(event, AppConfig.PATH_LOGIN_SCREEN);
    }

    private String validateInputs(String username, String password, String confirmpassword,
                                  String name, String numberphone, String email) {
        if (username.isEmpty() || password.isEmpty() || confirmpassword.isEmpty()
                || name.isEmpty() || email.isEmpty() || numberphone.isEmpty()) {
            return "Vui lòng nhập đầy đủ thông tin!";
        }
        if (username.length() < 6) {
            return "Tên đăng nhập phải có ít nhất 6 ký tự!";
        }
        if (username.contains(" ")) {
            return "Tên đăng nhập không được chứa khoảng trắng!";
        }
        if (password.length() < 6) {
            return "Mật khẩu phải có ít nhất 6 ký tự!";
        }
        if (!password.equals(confirmpassword)) {
            return "Mật khẩu và xác nhận mật khẩu không khớp!";
        }
        if (!agreeCheck.isSelected()) {
            return "Bạn cần đồng ý với điều khoản để tiếp tục!";
        }
        if (!isValidEmail(email)) {
            return "Email không hợp lệ!";
        }
        if (!isValidPhone(numberphone)) {
            return "Số điện thoại không hợp lệ!";
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