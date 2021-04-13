package bfst21;

import javafx.application.Application;
import javafx.stage.Stage;


public class Launcher extends Application {
    public static void main(String[] args) {
        Launcher.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        var ttiMode = getParameters().getRaw().size() > 0 && getParameters().getRaw().get(0).equals("ttiMode");
        var model = new Model("data/bornholm.osm", ttiMode);
        new View(model, primaryStage);
    }
}
