package bfst21;

import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {
    public static void main(String[] args) {
        Launcher.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var model = new Model();
        var view = new View(model, primaryStage);
        new Controller(model, view);
    }
}
