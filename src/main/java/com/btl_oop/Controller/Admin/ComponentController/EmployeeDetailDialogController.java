package com.btl_oop.Controller.Admin.ComponentController;

import com.btl_oop.Model.DAO.EmployeeDAO;
import com.btl_oop.Model.Entity.Employee;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class EmployeeDetailDialogController {

    @FXML private Circle avatarCircle;
    @FXML private Label customerNameLabel;
    @FXML private Label customerEmailLabel;
    @FXML private Label statusBadge;
    @FXML private TextField fullNameField;
    @FXML private TextField nickNameField;
    @FXML private TextField genderField;
    @FXML private TextField languageField;
    @FXML private Label emailAddressLabel;
    @FXML private Label emailTimeLabel;
    @FXML private Button editButton;
    @FXML private Button saveButton;

    private Employee currentEmployee;
    private EmployeeDAO employeeDAO = new EmployeeDAO();
    private boolean isEditMode = false;

    public void loadEmployee(Employee customer) {
        this.currentEmployee = customer;
        displayEmployeeData(customer);
    }

    private void displayEmployeeData(Employee employee) {
        // Set header info
        customerNameLabel.setText(employee.getFullName());
        customerEmailLabel.setText(employee.getEmail());

        // Set status badge
        statusBadge.setText(employee.getStatus());
        statusBadge.getStyleClass().removeAll("active", "inactive");
        if (employee.getStatus().equals("Activate")) {
            statusBadge.getStyleClass().add("active");
        } else {
            statusBadge.getStyleClass().add("inactive");
        }

        // Set avatar color
        avatarCircle.setFill(getColorForName(employee.getFullName()));

        // Set form fields
        fullNameField.setText(employee.getFullName());
        nickNameField.setText(employee.getUserName());
        languageField.setText("Vietnamese");

        // Set email info
        emailAddressLabel.setText(employee.getEmail());

    }

    private Color getColorForName(String name) {
        int hash = name.hashCode();
        double hue = (hash % 360);
        return Color.hsb(hue, 0.6, 0.9);
    }

    @FXML
    private void handleEdit() {
        isEditMode = !isEditMode;
        fullNameField.setEditable(isEditMode);
        nickNameField.setEditable(isEditMode);
        genderField.setEditable(isEditMode);
        languageField.setEditable(isEditMode);

        if (isEditMode) {
            editButton.setText("Cancel");
            saveButton.setVisible(true);
        } else {
            editButton.setText("Edit");
            saveButton.setVisible(false);
            displayEmployeeData(currentEmployee);
        }
    }

    @FXML
    private void handleSave() {
        currentEmployee.setFullName(fullNameField.getText());
        currentEmployee.setUserName(nickNameField.getText());

        displayEmployeeData(currentEmployee);

        isEditMode = false;
        editButton.setText("Edit");
        saveButton.setVisible(false);

        showAlert(Alert.AlertType.INFORMATION, "Success", "Customer data updated successfully!");

        employeeDAO.updateEmployee(currentEmployee);
        System.out.println("Customer data saved: " + currentEmployee.getFullName());
    }




    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}