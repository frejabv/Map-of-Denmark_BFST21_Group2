package bfst21;

import bfst21.osm.Node;

import com.sun.management.OperatingSystemMXBean;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {
    boolean singleClick = true;
    @FXML
    Text suggestionsHeader;
    ScheduledExecutorService executor;
    OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean();
    Runtime runtime = Runtime.getRuntime();
    long processMemory = runtime.totalMemory() - runtime.freeMemory();
    ArrayList<Text> suggestionList = new ArrayList<>();
    private Model model;
    private Point2D lastMouse;
    @FXML
    private MapCanvas canvas;
    @FXML
    private VBox searchContainer;
    @FXML
    private VBox routeContainer;
    @FXML
    private VBox settingsContainer;
    @FXML
    private VBox debugContainer;
    @FXML
    private Button searchButton;
    @FXML
    private Button routeButton;
    @FXML
    private Button settingsButton;
    @FXML
    private TextField searchField;
    @FXML
    private CheckBox enableDebugWindow;
    @FXML
    private Text cpuProcess;
    @FXML
    private Text cpuSystem;
    @FXML
    private Text ttd;
    @FXML
    private Text memoryUse;
    Runnable updateStats = new Runnable() {
        public void run() {
            cpuProcess.textProperty().setValue("CPU Process Load: " + bean.getProcessCpuLoad());
            cpuSystem.textProperty().setValue("CPU System Load: " + bean.getSystemCpuLoad() + "( " + bean.getSystemLoadAverage() + " average)");
            memoryUse.textProperty().setValue("Memory Use (Experimental): " + processMemory);
            long total = 0;
            for (long temp : canvas.redrawAverage) {
                total += temp;
            }
            ttd.textProperty().set("Redraw time (Rolling Average): ~" + TimeUnit.NANOSECONDS.toMillis(total / canvas.redrawAverage.length) + "ms (" + total / canvas.redrawAverage.length + "ns)");
        }
    };

    public void init(Model model, Stage stage) {
        this.model = model;
        canvas.init(model);
        hideAll();
        changeType("debug", false);
        Spelling autocorrector = new Spelling();

        searchField.textProperty().addListener((obs, oldText, newText) -> {
            String searchStringCorrected = "";
            String[] result = autocorrector.correction(newText);
            for (String temp : result) {
                searchStringCorrected = temp + " ";
            }
            addSuggestions();
        });
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(updateStats, 0, 3, TimeUnit.SECONDS);
        if (model.getTtiMode()) {
            System.exit(0);
        }
    }

    public void addSuggestions() {
        searchContainer.getChildren().removeAll(suggestionList);
        suggestionList.clear();
        if (searchField.textProperty().getValue().length() > 2) {
            // for(Member temp : possibleMatches)
            for (int i = 0; i < 8; i++) {
                Text newSuggestion = new Text("Suggestion!" + i);
                newSuggestion.getStyleClass().add("suggestion");
                suggestionList.add(newSuggestion);
            }
        }
        searchContainer.getChildren().addAll(suggestionList);
    }

    @FXML
    public void onKeyPressed(KeyEvent e) {

        if (e.getText().equals("d")) {
            toggleDebugMode();
        }
    }


    public void toggleDebugMode() {
        if (debugContainer.isVisible()) {
            changeType("debug", false);
            enableDebugWindow.setSelected(false);
        } else {
            changeType("debug", true);
            enableDebugWindow.setSelected(true);
        }
    }

    @FXML
    private void onScrollOnCanvas(ScrollEvent e) {
        double factor = Math.pow(1.01, e.getDeltaY());
        canvas.zoom(factor, new Point2D(e.getX(), e.getY()));
    }

    @FXML
    private void onMouseDraggedOnCanvas(MouseEvent e) {
        double dx = e.getX() - lastMouse.getX();
        double dy = e.getY() - lastMouse.getY();
        canvas.pan(dx, dy);
        onMousePressedOnCanvas(e);
        singleClick = false;
    }

    @FXML
    private void onMousePressedOnCanvas(MouseEvent e) {
        lastMouse = new Point2D(e.getX(), e.getY());
    }

    @FXML
    private void onMouseReleasedOnCanvas(MouseEvent e) {
        if (singleClick) {
            //TODO make this look good
            Point2D lastMouseCoords = canvas.mouseToModelCoords(new Point2D(e.getX(), e.getY()));
            Node tester = model.getKdTree().nearest(lastMouseCoords);
            System.out.println(tester == null ?
                    "You clicked outside the map! We can't find anything in the void"
                    : "Nearest node ID: " + tester.getId() + " with the coordinates (" + tester.getX() + ", " + tester.getY() * -0.56f + ")");

            searchContainer.getChildren().remove(searchContainer.lookup(".button"));
            String coordinates = canvas.setPin(new Point2D(e.getX(), e.getY()));
            changeType("search", true);
            suggestionsHeader.textProperty().setValue(coordinates);
            Button removePin = new Button("Remove pin");
            removePin.setOnAction(event -> {
                canvas.setPin = false;
                canvas.repaint();
                hideAll();
            });
            searchContainer.getChildren().add(removePin);
        } else {
            singleClick = true;
        }
    }

    public void onMousePressedSearch(MouseEvent mouseEvent) {
        if (searchContainer.isVisible()) {
            hideAll();
            if (canvas.setPin) {
                canvas.setPin = false;
                canvas.repaint();
            }
        } else {
            changeType("search", true);
        }
    }

    public void onMousePressedRoute(MouseEvent mouseEvent) {
        if (routeContainer.isVisible()) {
            hideAll();
        } else {
            changeType("route", true);
        }
    }

    public void onMousePressedSettings(MouseEvent mouseEvent) {
        if (settingsContainer.isVisible()) {
            hideAll();
        } else {
            changeType("settings", true);
        }
    }

    public void defaultColorMode(MouseEvent mouseEvent) {
        canvas.renderingStyle.defaultMode();
        canvas.repaint();
    }

    public void darkColorMode(MouseEvent mouseEvent) {
        canvas.renderingStyle.darkMode();
        canvas.repaint();
    }

    public void deuteranopeColorMode(MouseEvent mouseEvent) {
        canvas.renderingStyle.deuteranopeColorMode();
        canvas.repaint();
    }

    public void protanopeColorMode(MouseEvent mouseEvent) {
        canvas.renderingStyle.protanopeColorMode();
        canvas.repaint();
    }

    public void tritanopeColorMode(MouseEvent mouseEvent) {
        canvas.renderingStyle.tritanopeColorMode();
        canvas.repaint();
    }

    public void hideAll() {
        changeType("search", false);
    }

    public void fadeButtons() {
        searchButton.setStyle("-fx-opacity: .5");
        routeButton.setStyle("-fx-opacity: .5");
        settingsButton.setStyle("-fx-opacity: .5");
    }

    public void changeType(String type, boolean state) {
        if (!type.equals("debug")) {
            searchContainer.setVisible(false);
            searchContainer.setManaged(false);
            routeContainer.setVisible(false);
            routeContainer.setManaged(false);
            settingsContainer.setVisible(false);
            settingsContainer.setManaged(false);
        }
        switch (type) {
            case "route":
                fadeButtons();
                routeContainer.setVisible(state);
                routeContainer.setManaged(state);
                if (state) {
                    routeButton.setStyle("-fx-opacity: 1");
                }
                break;
            case "settings":
                fadeButtons();
                settingsContainer.setVisible(state);
                settingsContainer.setManaged(state);
                if (state) {
                    settingsButton.setStyle("-fx-opacity: 1");
                }
                break;
            case "debug":
                debugContainer.setVisible(state);
                debugContainer.setManaged(state);
                break;
            default:
                fadeButtons();
                searchContainer.setVisible(state);
                searchContainer.setManaged(state);
                if (state) {
                    searchButton.setStyle("-fx-opacity: 1");
                }
        }
    }

    @FXML
    public void ToggleKDLines() {
        canvas.kdLines = !canvas.kdLines;
        canvas.repaint();
    }
}
