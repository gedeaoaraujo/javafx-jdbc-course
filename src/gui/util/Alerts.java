package gui.util;

import javafx.scene.control.Alert;

public class Alerts {

    public static void showAlert(String title, String header, String content, Alert.AlertType type){
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }
}
