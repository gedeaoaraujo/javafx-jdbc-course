module NovoJavaFXTeste {
    requires java.sql;
    requires javafx.fxml;
    requires javafx.controls;
    requires org.jetbrains.annotations;

    exports gui;
    exports gui.listeners;
    exports model.entities;
    exports model.services;

    opens application;
    opens gui;
}