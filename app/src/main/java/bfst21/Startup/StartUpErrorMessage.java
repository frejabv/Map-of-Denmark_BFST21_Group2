package bfst21.Startup;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class StartUpErrorMessage {

    public StartUpErrorMessage() {
        Alert alert = new Alert(Alert.AlertType.NONE, "A problem occured loading the file", ButtonType.OK);
        alert.setTitle("Error");
        alert.showAndWait();
    }
}
