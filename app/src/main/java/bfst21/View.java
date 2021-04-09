package bfst21;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class View {
    public View(Model model, Stage stage) throws IOException {
        var loader = new FXMLLoader(View.class.getResource("View.fxml"));
        Scene scene = loader.load();
        stage.setScene(scene);
        stage.setTitle("Initial window");
        stage.centerOnScreen();
        Controller controller = loader.getController();
        stage.show();
        controller.init(model);
        stage.setOnCloseRequest(
                event -> controller.shutdownExecutor()
        );
    }
}
