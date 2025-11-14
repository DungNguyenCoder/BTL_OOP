package com.btl_oop;

import com.btl_oop.Utils.AppConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

public class RestaurantManagement extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RestaurantManagement.class.getResource(AppConfig.PATH_LOGIN_SCREEN));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Restaurant Management");
        stage.setScene(scene);

        stage.setWidth(1280);
        stage.setHeight(720);
        stage.setResizable(true);

        stage.show();
        System.out.println(BCrypt.hashpw("admin", BCrypt.gensalt()));
    }

    public static void main(String[] args) {
        launch();
    }
}
