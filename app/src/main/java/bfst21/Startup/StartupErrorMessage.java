package bfst21.Startup;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class StartupErrorMessage {

    public StartupErrorMessage(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.NONE, "A problem occured loading the file", ButtonType.OK);
        alert.setTitle("Error");
        alert.show();
        try {
            new StartupScreen(stage);
        } catch (Exception e) {
            System.exit(1);
        }
    }
}
