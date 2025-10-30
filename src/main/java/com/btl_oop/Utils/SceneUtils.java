package com.btl_oop.Utils;

import com.btl_oop.RestaurantManagement;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneUtils {
    public static void switchTo(Event event, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(RestaurantManagement.class.getResource(fxmlPath));
        Parent root = loader.load();

        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
