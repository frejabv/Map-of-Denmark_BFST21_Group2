package bfst21;

import bfst21.osm.*;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.input.ScrollEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.ToggleButton;

import java.util.ArrayList;

public class Controller {
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
    private HBox debugContainer;
    @FXML
    private Button searchButton;
    @FXML
    private Button routeButton;
    @FXML
    private Button settingsButton;
    @FXML
    private TextField searchField;

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
    public void addSuggestions(){
        searchContainer.getChildren().removeAll(suggestionList);
        suggestionList.clear();
        if(searchField.textProperty().getValue().length() > 2) {
            //for(Member temp : possibleMatches)
            for (int i = 0; i < 8; i++) {
                Text newSuggestion = new Text("Suggestion!" + i);
                newSuggestion.getStyleClass().add("suggestion");
                suggestionList.add(newSuggestion);
            }
        }
        searchContainer.getChildren().addAll(suggestionList);
    }

    @FXML
    public void onKeyPressed(KeyEvent e){

        if (e.getText().equals("d")) {
            if(debugContainer.isVisible()){
                changeType("debug", false);
            }
            else {
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
    }

    @FXML
    private void onMousePressedOnCanvas(MouseEvent e) {
        lastMouse = new Point2D(e.getX(), e.getY());
        Node tester = model.getKdTree().nearest(lastMouse);
        System.out.println("Nearest node coordinates are: x = " + tester.getX() + " y =" + tester.getY());
    }


    public void onMousePressedSearch(MouseEvent mouseEvent) {
        if(searchContainer.isVisible()){
            hideAll();
            fadeButtons();
        }
        else{
            changeType("search",true);
            changeType("route",false);
            changeType("settings",false);
            fadeButtons();
            searchButton.setStyle("-fx-opacity: 1");
        }
    }
    public void onMousePressedRoute(MouseEvent mouseEvent) {
        if(routeContainer.isVisible()){
            hideAll();
            fadeButtons();
        }
        else{
            changeType("search",false);
            changeType("route",true);
            changeType("settings",false);
            fadeButtons();
            routeButton.setStyle("-fx-opacity: 1");
        }
    }

    public void onMousePressedSettings(MouseEvent mouseEvent) {
        if(settingsContainer.isVisible()){
            hideAll();
            fadeButtons();
        }
        else {
            changeType("search",false);
            changeType("route",false);
            changeType("settings",true);
            fadeButtons();
            settingsButton.setStyle("-fx-opacity: 1");
        }
    }


    public void defaultColorMode(MouseEvent mouseEvent){
        canvas.colorScheme.defaultMode();
        canvas.repaint();
    }
    public void darkColorMode(MouseEvent mouseEvent){
        canvas.colorScheme.darkMode();
        canvas.repaint();
    }
    public void deuteranopeColorMode(MouseEvent mouseEvent){
        canvas.colorScheme.deuteranopeColorMode();
        canvas.repaint();
    }
    public void protanopeColorMode(MouseEvent mouseEvent){
        canvas.colorScheme.protanopeColorMode();
        canvas.repaint();
    }
    public void tritanopeColorMode(MouseEvent mouseEvent){
        canvas.colorScheme.tritanopeColorMode();
        canvas.repaint();
    }

    public void hideAll(){
        changeType("search",false);
        changeType("route",false);
        changeType("settings",false);
    }
    public void fadeButtons(){
        searchButton.setStyle("-fx-opacity: .5");
        routeButton.setStyle("-fx-opacity: .5");
        settingsButton.setStyle("-fx-opacity: .5");
    }
    public void changeType(String type, boolean state){
        switch (type){
            case "route":
                routeContainer.setVisible(state);
                routeContainer.setManaged(state);
                break;
            case "settings":
                settingsContainer.setVisible(state);
                settingsContainer.setManaged(state);
                break;
            case "debug":
                debugContainer.setVisible(state);
                debugContainer.setManaged(state);
                break;
            default:
                searchContainer.setVisible(state);
                searchContainer.setManaged(state);
        }
    }
}
