package bfst21.Startup;

import bfst21.Model;
import bfst21.View;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class StartupController {
    Stage stage;
    public void init(Stage stage){
        this.stage = stage;
    }

    FileChooser fileChooser = new FileChooser();

    public void openFile(){
        File selectedFile = fileChooser.showOpenDialog(stage);
        //Sanity check of filetype here
        if(selectedFile != null){
            startMapView(selectedFile.getAbsolutePath());
        }
    }
    public void defaultFile(){
        startMapView("data/bornholm.osm.zip.obj");
    }
    public void startMapView(String filePath) {
        var model = new Model(filePath,false);
        try {
            View view = new View(model, stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void exit() {
        System.exit(0);
    }
}
