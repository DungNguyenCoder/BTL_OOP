package com.btl_oop.Utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertUtils {

    public static void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Thông báo", message);
    }

    public static void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, "Cảnh báo", message);
    }

    public static void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Lỗi", message);
    }

    public static boolean showConfirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        alert.showAndWait();
        return alert.getResult() == ButtonType.YES;
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}
