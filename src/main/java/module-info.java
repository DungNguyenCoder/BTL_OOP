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


    opens com.btl_oop.Model.Entity to com.google.gson, javafx.fxml;
    opens com.btl_oop.Model.Store to com.google.gson, javafx.fxml;
    opens com.btl_oop.Utils to javafx.fxml;
    opens com.btl_oop to javafx.fxml, com.google.gson;


    exports com.btl_oop;
    exports com.btl_oop.Model.Entity;
    exports com.btl_oop.Model.Store;
    exports com.btl_oop.Model.Data;
    exports com.btl_oop.Model.Enum;

    exports com.btl_oop.Controller.Order;

    exports com.btl_oop.Controller.Admin.MainController;
    exports com.btl_oop.Controller.Admin.ComponentController;
    exports com.btl_oop.Controller.Admin;

    exports com.btl_oop.Controller.LoginController;


    opens com.btl_oop.Controller.Order to com.google.gson, javafx.fxml;

    opens com.btl_oop.Controller.Admin.MainController to com.google.gson, javafx.fxml;
    opens com.btl_oop.Controller.Admin.ComponentController to com.google.gson, javafx.fxml;
    opens com.btl_oop.Controller.Admin to com.google.gson, javafx.fxml;

    opens com.btl_oop.Controller.LoginController to com.google.gson, javafx.fxml;

}
