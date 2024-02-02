module org.example.tline {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires java.desktop;
    requires sqlite.jdbc;
    requires java.sql;

    opens org.example.tline to javafx.fxml;
    exports org.example.tline;
    exports org.example.tline.threads;
    opens org.example.tline.threads to javafx.fxml;
}