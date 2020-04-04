module NovoJavaFXTeste {
    requires javafx.fxml;
    requires javafx.controls;

    exports gui;
    exports model.entities;
    exports model.services;

    opens application;
    opens gui;
}