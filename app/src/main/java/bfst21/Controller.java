package bfst21;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Controller {
    @FXML
    private MapCanvas canvas;
    @FXML
    private VBox searchContainer;
    @FXML
    private VBox routeContainer;
    @FXML
    private VBox settingsContainer;
    @FXML
    private VBox pinContainer;
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
    @FXML Text suggestionsHeader;
    @FXML Text pinText;
    @FXML
    private Text cpuProcess;
    @FXML
    private Text cpuSystem;
    @FXML
    private Text ttd;
    @FXML
    private Text memoryUse;

    private Debug debug;
    private Point2D lastMouse;
    private boolean singleClick = true;

    public void init(Model model) {
        canvas.init(model);
        hideAll();
        debug = new Debug(canvas,cpuProcess,cpuSystem,ttd,memoryUse);
        changeType("debug", false);
        Spelling autocorrector = new Spelling();
        Regex regex = new Regex(setupRegexView());
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            //Run Regex Matcher
            regex.run(newText);
        });
        if (model.getTtiMode()) {
            System.exit(0);
        }
    }

    @FXML
    private VBox regexContainer;

    private List<Text> setupRegexView() {
        List<Text> regexVisualisers = new ArrayList<>();
        List<String> regexString = Arrays.asList("[Postcode] [City]", "[Street] [Number], [Floor] [Side], [Postal Code] [City]");
        for (int i = 0; i < regexString.size(); i++) {
            HBox hbox = new HBox();
            hbox.getStyleClass().add("regexLine");
            Text bullet = new Text("●");
            bullet.getStyleClass().add("regexMatch");
            Text text = new Text(regexString.get(i));
            hbox.getChildren().add(bullet);
            hbox.getChildren().add(text);
            regexVisualisers.add(bullet);
            regexContainer.getChildren().add(hbox);
        }
        return regexVisualisers;
    }

    @FXML
    public void onKeyPressed(KeyEvent e) {
        if (e.getText().equals("d")) {
            toggleDebugMode();
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
        if(singleClick) {
            pinContainer.getChildren().remove(pinContainer.lookup(".button"));
            String coordinates = canvas.setPin(new Point2D(e.getX(), e.getY()));
            changeType("pin", true);
            pinText.textProperty().setValue(coordinates);
            Button removePin = new Button("Remove pin");
            removePin.setOnAction(event -> {
                canvas.setPin = false;
                canvas.repaint();
                hideAll();
            });
            pinContainer.getChildren().add(removePin);
        }
        else{
            singleClick = true;
        }
    }

    public void toggleDebugMode(){
        if (debugContainer.isVisible()) {
            changeType("debug", false);
            enableDebugWindow.setSelected(false);
        } else {
            changeType("debug", true);
            enableDebugWindow.setSelected(true);
        }
    }

    public void onMousePressedSearch() {
        if (searchContainer.isVisible()) {
            hideAll();
            if(canvas.setPin){
                canvas.setPin = false;
                canvas.repaint();
            }
        }
        else{
            changeType("search",true);
        }
    }

    public void onMousePressedRoute() {
        if (routeContainer.isVisible()) {
            hideAll();
        }
        else{
            changeType("route",true);
        }
    }

    public void onMousePressedSettings() {
        if (settingsContainer.isVisible()) {
            hideAll();
        }
        else {
            changeType("settings",true);
        }
    }

    public void defaultColorMode() {
        canvas.renderingStyle.defaultMode();
        canvas.repaint();
    }

    public void darkColorMode() {
        canvas.renderingStyle.darkMode();
        canvas.repaint();
    }

    public void deuteranopeColorMode() {
        canvas.renderingStyle.deuteranopeColorMode();
        canvas.repaint();
    }

    public void protanopeColorMode() {
        canvas.renderingStyle.protanopeColorMode();
        canvas.repaint();
    }

    public void tritanopeColorMode() {
        canvas.renderingStyle.tritanopeColorMode();
        canvas.repaint();
    }

    public void hideAll(){
        changeType("search",false);
    }

    public void fadeButtons() {
        searchButton.setStyle("-fx-opacity: .5");
        routeButton.setStyle("-fx-opacity: .5");
        settingsButton.setStyle("-fx-opacity: .5");
    }

    public void changeType(String type, boolean state){
        if(!type.equals("debug")){
            searchContainer.setVisible(false);
            searchContainer.setManaged(false);
            routeContainer.setVisible(false);
            routeContainer.setManaged(false);
            settingsContainer.setVisible(false);
            settingsContainer.setManaged(false);
            pinContainer.setVisible(false);
            pinContainer.setManaged(false);
        }
        switch (type){
            case "route":
                fadeButtons();
                routeContainer.setVisible(state);
                routeContainer.setManaged(state);
                if(state){
                  routeButton.setStyle("-fx-opacity: 1");
                }
                break;
            case "settings":
                fadeButtons();
                settingsContainer.setVisible(state);
                settingsContainer.setManaged(state);
                if(state){
                  settingsButton.setStyle("-fx-opacity: 1");
                }
                break;
            case "debug":
                debugContainer.setVisible(state);
                debugContainer.setManaged(state);
                break;
            case "pin":
                pinContainer.setVisible(state);
                pinContainer.setManaged(state);
                break;
            default:
                fadeButtons();
                searchContainer.setVisible(state);
                searchContainer.setManaged(state);
                if(state){
                    searchButton.setStyle("-fx-opacity: 1");
                }
        }
    }

    public void shutdownExecutor() {
        debug.shutdownExecutor();
    }
}
