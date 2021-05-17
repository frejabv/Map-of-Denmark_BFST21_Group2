package bfst21;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import bfst21.Startup.StartupScreen;
import bfst21.osm.FileExtension;
import javafx.stage.Stage;

public class WindowController {
    private static WindowController instance;

    private Stage primaryStage;
    private StartupScreen startupScreen;
    private Model mapModel;
    private View mapView;

    private WindowController() {
        startupScreen = null;
        mapModel = null;
        mapView = null;
    }

    public static WindowController getInstance() {
        if (instance == null) {
            instance = new WindowController();
        }

        return instance;
    }

    public void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public void activateFileSelector() throws IOException {
        mapModel = null;
        mapView = null;
        primaryStage.close();
        primaryStage = new Stage();
        primaryStage.setOnCloseRequest(e -> System.exit(1));

        startupScreen = new StartupScreen(primaryStage);
    }

    public void activateMap(InputStream in, FileExtension fileExtension, String fileName)
            throws IOException, ClassNotFoundException, XMLStreamException {
        startupScreen = null;

        mapModel = new Model(in, fileExtension, fileName, false);
        mapView = new View(mapModel, primaryStage);
    }
}
