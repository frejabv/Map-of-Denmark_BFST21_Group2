package bfst21.Startup;

import bfst21.AlertMessage;
import bfst21.Model;
import bfst21.View;
import bfst21.osm.FileExtension;
import bfst21.osm.OSMParser;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


public class StartupController {
    Stage stage;

    @FXML
    private BorderPane loadingText;
    @FXML
    private VBox fileContainer;

    public void init(Stage stage) {
        loadingText.setVisible(false);
        loadingText.setManaged(false);
        this.stage = stage;
    }

    FileChooser fileChooser = new FileChooser();

    public void openFile() {
        File selectedFile = fileChooser.showOpenDialog(stage);
        setStyling();
        PauseTransition pause = new PauseTransition();
        pause.setOnFinished(event -> {
            openChosenFile(selectedFile);
        });
        pause.play();
    }

    public void openChosenFile(File selectedFile) {
        // Sanity check of filetype here
        if (selectedFile != null) {
            startMapView(selectedFile.getAbsolutePath());
        }
    }

    public void defaultFile() {
        setStyling();
        PauseTransition pause = new PauseTransition();
        pause.setOnFinished(event -> {
            openDefaultFile();
        });
        pause.play();
    }

    public void openDefaultFile() {
        try {
            var model = new Model("/bfst21/data/bornholm.osm", false);
            new View(model, stage);
        } catch (Exception e) {
            new AlertMessage(stage);
        }
    }

    public void startMapView(String filePath) {
        try {
            FileExtension fileExtension = OSMParser.genFileExtension(filePath);
            InputStream in = new FileInputStream(filePath);
            String[] filePathParts = filePath.split("/");

            var model = new Model(in, fileExtension, filePathParts[filePathParts.length - 1], false);
            View view = new View(model, stage);
        } catch (Exception e) {
            new AlertMessage(stage);
        }
    }

    public void exit() {
        System.exit(0);
    }

    public void setStyling() {
        loadingText.setVisible(true);
        loadingText.setManaged(true);
        fileContainer.setVisible(false);
        fileContainer.setManaged(false);
    }
}
