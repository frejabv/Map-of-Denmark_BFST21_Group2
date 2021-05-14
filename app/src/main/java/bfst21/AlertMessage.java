package bfst21;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertMessage {

    public AlertMessage(String message){
        Alert alert = new Alert(Alert.AlertType.NONE, message , ButtonType.OK);
        alert.setTitle("Error");
        alert.showAndWait().ifPresent(response -> {
            new Launcher();
        });
    }
}
