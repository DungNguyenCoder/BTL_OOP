package com.btl_oop.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable{
    @FXML
    ImageView img_register ;
    @FXML
    private Label titleLabel;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private CheckBox agreeCheck;
    @FXML
    private Button registerButton;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
