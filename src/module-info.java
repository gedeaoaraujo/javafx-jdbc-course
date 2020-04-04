module NovoJavaFXTeste {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.sql;

    exports gui;
    exports model.entities;
    exports model.services;

    opens application;
    opens gui;
}