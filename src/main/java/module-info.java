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

    opens com.btl_oop to javafx.fxml;
    exports com.btl_oop;
    exports com.btl_oop.Controller;
    opens com.btl_oop.Controller to javafx.fxml;
    exports com.btl_oop.Model.Entity;
    opens com.btl_oop.Model.Entity to javafx.fxml, com.google.gson;
}