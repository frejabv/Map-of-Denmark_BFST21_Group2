package bfst21;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.input.ScrollEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Controller {
    private Model model;
    private Point2D lastMouse;
    boolean singleClick = true;
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

    @FXML Text suggestionsHeader;


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
    }

    ArrayList<Text> suggestionList = new ArrayList<>();

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
            if (debugContainer.isVisible()) {
                changeType("debug", false);
            } else {
                changeType("debug", true);
            }
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
        }
        else{
            singleClick = true;
        }
    }
    public void onMousePressedSearch(MouseEvent mouseEvent) {
        if (searchContainer.isVisible()) {
            hideAll();
        }
        else{
            changeType("search",true);
        }
    }

    public void onMousePressedRoute(MouseEvent mouseEvent) {
        if (routeContainer.isVisible()) {
            hideAll();
        }
        else{
            changeType("route",true);
            fadeButtons();
        }
    }

    public void onMousePressedSettings(MouseEvent mouseEvent) {
        if (settingsContainer.isVisible()) {
            hideAll();
        }
        else {
            changeType("settings",true);
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

    public void hideAll(){
        changeType("search",false);
    }

    public void fadeButtons() {
        searchButton.setStyle("-fx-opacity: .5");
        routeButton.setStyle("-fx-opacity: .5");
        settingsButton.setStyle("-fx-opacity: .5");
    }
    public void changeType(String type, boolean state){
        searchContainer.setVisible(false);
        searchContainer.setManaged(false);
        routeContainer.setVisible(false);
        routeContainer.setManaged(false);
        settingsContainer.setVisible(false);
        settingsContainer.setManaged(false);
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
            default:
                fadeButtons();
                searchContainer.setVisible(state);
                searchContainer.setManaged(state);
                if(state){
                  searchButton.setStyle("-fx-opacity: 1");
                }
        }
    }
}
