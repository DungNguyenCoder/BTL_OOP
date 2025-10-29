module com.btl_oop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires com.google.gson;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens com.btl_oop.Model.Entity to javafx.fxml;
    opens com.btl_oop.Utils to javafx.fxml;

    opens com.btl_oop to javafx.fxml;
    opens com.btl_oop.Controller.LoginController to javafx.fxml;
    opens com.btl_oop.Controller.Admin to javafx.fxml;
    opens com.btl_oop.Controller.Admin.ComponentController to javafx.fxml;
    opens com.btl_oop.Controller.Admin.MainController to javafx.fxml;
    opens com.btl_oop.Controller.Order to javafx.fxml;
    opens com.btl_oop.Controller.DeskManager to javafx.fxml;
    // Root Controller package no longer contains classes; remove to avoid module error


    exports com.btl_oop;
    exports com.btl_oop.Model.Entity;
    exports com.btl_oop.Controller.Order;
    exports com.btl_oop.Model.Enum;

    requires java.sql;
    requires mysql.connector.j;
    requires jbcrypt;
    requires okhttp3;
    requires org.json;
    exports com.btl_oop.Model.DAO;
}
