package bfst21;

import bfst21.Startup.StartupScreen;
import javafx.application.Application;
import javafx.stage.Stage;


public class Launcher extends Application {
    public static void main(String[] args) {
        Launcher.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        boolean ttiMode = getParameters().getRaw().size() > 0 && getParameters().getRaw().get(0).equals("ttiMode");
        boolean fileSelectorMode = getParameters().getRaw().size() > 0 && getParameters().getRaw().get(0).equals("fileSelector");
        if(fileSelectorMode){
            new StartupScreen(primaryStage);
        }
        else{
            var model = new Model("data/samsoe.osm",ttiMode);
            new View(model, primaryStage);
        }
    }
}
