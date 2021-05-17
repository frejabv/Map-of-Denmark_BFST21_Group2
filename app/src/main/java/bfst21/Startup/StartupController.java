package bfst21.Startup;

import bfst21.Model;
import bfst21.View;
import bfst21.osm.FileExtension;
import bfst21.osm.OSMParser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class StartupController {
    private Stage stage;

    public void init(Stage stage) {
        this.stage = stage;
    }

    FileChooser fileChooser = new FileChooser();

    public void openFile() {
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            startMapView(selectedFile.getAbsolutePath());
        }
    }

    public void defaultFile() {
        try {
            var model = new Model("/bfst21/data/bornholm.osm", false);
            new View(model, stage, "Denmark");
        } catch (Exception e) {
            new StartUpErrorMessage();
        }
    }

    public void startMapView(String filePath) {
        try {
            FileExtension fileExtension = OSMParser.genFileExtension(filePath);
            InputStream in = new FileInputStream(filePath);
            String[] filePathParts = filePath.split("/");

            var model = new Model(in, fileExtension, filePathParts[filePathParts.length - 1], false);
            new View(model, stage, filePathParts[filePathParts.length - 1]);
        } catch (Exception e) {
            new StartUpErrorMessage();
        }
    }

    public void exit() {
        System.exit(0);
    }
}
