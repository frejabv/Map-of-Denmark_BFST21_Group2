package bfst21.Startup;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StartupScreen {
    public StartupScreen(Stage stage) throws IOException {
        var loader = new FXMLLoader(bfst21.View.class.getResource("StartupScreen.fxml"));
        Scene scene = loader.load();
        stage.setScene(scene);
        stage.setTitle("Initial window");
        StartupController controller = loader.getController();
        stage.show();
        controller.init(stage);
    }
}