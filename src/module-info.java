module NovoJavaFXTeste {
    requires javafx.fxml;
    requires javafx.controls;

    exports gui;

    opens application;
    opens gui;
}