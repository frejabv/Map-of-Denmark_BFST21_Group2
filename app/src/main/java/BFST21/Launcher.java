package BFST21;

import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {
    public static void main(String[] args) {
        Launcher.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var model = new Model();
        new View(model, primaryStage);
    }
}
