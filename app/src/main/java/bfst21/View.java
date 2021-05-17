package bfst21;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class View {
    public View(Model model, Stage stage) throws IOException {
        this(model, stage, "Default");
    }

    public View(Model model, Stage stage, String windowName) throws IOException {
        var loader = new FXMLLoader(bfst21.View.class.getResource("View.fxml"));
        Scene scene = loader.load();
        stage.setScene(scene);
        stage.setTitle("Map - " + windowName);
        stage.centerOnScreen();
        Controller controller = loader.getController();
        stage.show();
        controller.init(model);
        stage.setOnCloseRequest(event -> controller.shutdownExecutor());
    }
}
