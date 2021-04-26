package bfst21;

import bfst21.osm.Node;
import bfst21.pathfinding.Step;
import bfst21.pathfinding.TransportType;
import bfst21.search.RadixNode;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javafx.scene.layout.Priority.SOMETIMES;

public class Controller {
    @FXML
    private MapCanvas canvas;
    @FXML
    private VBox searchContainer;
    @FXML
    private VBox routeContainer;
    @FXML
    private TextField routeFieldFrom;
    @FXML
    private TextField routeFieldTo;
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
    @FXML
    private Text suggestionsHeader;
    @FXML
    private Text pinText;
    @FXML
    private Text cpuProcess;
    @FXML
    private Text cpuSystem;
    @FXML
    private Text ttd;
    @FXML
    private Text memoryUse;
    @FXML
    private Text scaletext;
    @FXML
    private VBox leftContainer;
    @FXML
    private HBox rightContainer;

    private Debug debug;
    private Point2D lastMouse;
    private boolean singleClick = true;
    private Node nodeFrom;
    private Model model;
    private ArrayList<Text> suggestionList = new ArrayList<>();
    private long fromNodeId, toNodeId;

    public void init(Model model) {
        this.model = model;
        canvas.init(model);
        canvas.setCurrentCanvasEdges();
        updateScaleBar();
        hideAll();
        debug = new Debug(canvas, cpuProcess, cpuSystem, ttd, memoryUse);
        changeType("debug", false);
        Spelling autocorrector = new Spelling();
        Regex regex = new Regex(setupRegexView());

        setUpSearchField(regex);
        setUpRouteFields(regex);

        if (model.getTtiMode()) {
            System.exit(0);
        }

        leftContainer.setMaxWidth(canvas.getWidth() / 100 * 33);
        rightContainer.setMaxWidth(canvas.getWidth() / 100 * 50);
        fromNodeId = 491469749l;
        toNodeId = 491471631l;

        model.setUpAStar();
        model.getAStar().AStarSearch(model.getNodeIndex().getMember(fromNodeId), model.getNodeIndex().getMember(toNodeId), model.getCurrentTransportType());
        showRoute();
        canvas.repaint(); //To show the route after it has been calculated
    }

    @FXML
    private VBox regexContainer;
    private List<Text> setupRegexView() {
        List<Text> regexVisualisers = new ArrayList<>();
        List<String> regexString = Arrays.asList("[Postcode] [City]", "[Street] [Number], [Floor] [Side], [Postal Code] [City]");
        for (int i = 0; i < regexString.size(); i++) {
            HBox hbox = new HBox();
            hbox.getStyleClass().add("regexLine");
            Text bullet = new Text("\u25CF");
            bullet.getStyleClass().add("regexMatch");
            Text text = new Text(regexString.get(i));
            hbox.getChildren().add(bullet);
            hbox.getChildren().add(text);
            regexVisualisers.add(bullet);
            regexContainer.getChildren().add(hbox);
        }
        return regexVisualisers;
    }

    public void setUpSearchField(Regex regex) {
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            //Run Regex Matcher
            regex.run(newText);
            addSuggestions(model, "search", null);
        });

        searchField.setOnAction(e -> {
            if (!suggestionList.isEmpty()) {
                searchField.textProperty().setValue(suggestionList.get(0).getText());
                Node node = model.getNodeIndex().getMember(model.getStreetTree().lookupNode(suggestionList.get(0).getText()).getId());
                canvas.setPin(node.getX(), node.getY());
                canvas.goToPosition(node.getX(), node.getX() + 0.0002, node.getY());
                searchContainer.getChildren().removeAll(suggestionList);
                suggestionList.clear();
            }
        });
    }

    public void setUpRouteFields(Regex regex) {
        routeFieldFrom.textProperty().addListener((obs, oldText, newText) -> {
            regex.run(newText);
            addSuggestions(model, "route", "from");
            if (newText.length() < oldText.length()) {
                canvas.hideRoute();
            }
        });

        routeFieldTo.textProperty().addListener((obs, oldText, newText) -> {
            regex.run(newText);
            addSuggestions(model, "route", "to");
            if (newText.length() < oldText.length()) {
                canvas.hideRoute();
            }
        });

        routeFieldFrom.setOnAction(e -> {
            if (!suggestionList.isEmpty()) {
                routeFieldFrom.textProperty().setValue(suggestionList.get(0).getText());
                fromNodeId = model.getStreetTree().lookupNode(suggestionList.get(0).getText()).getId();
                //Node node = model.getStreetTree().lookupNode(suggestionList.get(0).getText()).getId();
                //fromNodeId = Rtree.nearest(node.getX(), node.getY());
                if (toNodeId != 0) {
                    //model.getAStar().AStarSearch(fromNodeId, toNodeId);
                    System.out.println("Route searched");
                }
                routeContainer.getChildren().removeAll(suggestionList);
                suggestionList.clear();
            }
        });

        routeFieldTo.setOnAction(e -> {
            if (!suggestionList.isEmpty()) {
                routeFieldTo.textProperty().setValue(suggestionList.get(0).getText());
                toNodeId = model.getStreetTree().lookupNode(suggestionList.get(0).getText()).getId();
                //Node node = model.getStreetTree().lookupNode(suggestionList.get(0).getText()).getId();
                //toNodeId = Rtree.nearest(node.getX(), node.getY());
                if (fromNodeId != 0) {
                    //model.getAStar().AStarSearch(fromNodeID, toNodeId);
                    System.out.println("Route searched");
                }
                routeContainer.getChildren().removeAll(suggestionList);
                suggestionList.clear();
            }
        });
    }

    public void addSuggestions(Model model, String containerType, String fieldType) {
        VBox selectedContainer;
        TextField selectedField;
        if (containerType.equals("search")) {
            selectedContainer = searchContainer;
            selectedField = searchField;
        } else {
            selectedContainer = routeContainer;
            if (fieldType.equals("from")) {
                selectedField = routeFieldFrom;
            } else {
                selectedField = routeFieldTo;
            }
        }
        selectedContainer.getChildren().removeAll(suggestionList);
        suggestionList.clear();
        if (selectedField.textProperty().getValue().length() > 2) {
            ArrayList<RadixNode> suggestions = model.getStreetTree().getSuggestions(selectedField.textProperty().getValue());
            for (int i = 0; i < Math.min(8, suggestions.size()); i++) {
                RadixNode suggestion = suggestions.get(i);
                Text newSuggestion = new Text(suggestion.getFullName());
                newSuggestion.getStyleClass().add("suggestion");
                newSuggestion.setOnMouseClicked(e -> {
                    selectedField.textProperty().setValue(suggestion.getFullName());
                    Node node = model.getNodeIndex().getMember(suggestion.getId());
                    if (containerType.equals("search")) {
                        canvas.setPin(node.getX(), node.getY());
                        canvas.goToPosition(node.getX(), node.getX() + 0.0002, node.getY());
                    } else {
                        if (fieldType.equals("from")) {
                            fromNodeId = node.getId();
                            //fromNodeId = Rtree.nearest(node.getX(), node.getY());
                            //potential route search here as well
                        } else {
                            toNodeId = node.getId();
                            //toNodeId = Rtree.nearest(node.getX(), node.getY());
                            if (fromNodeId != 0) {
                                //model.getAStar().AStarSearch(fromNodeID, toNodeId);
                                System.out.println("Route searched");
                            }
                        }
                        System.out.println(fieldType + ": " + node.getId());
                    }
                    selectedContainer.getChildren().removeAll(suggestionList);
                    suggestionList.clear();
                });
                suggestionList.add(newSuggestion);
            }
        }
        selectedContainer.getChildren().addAll(suggestionList);
    }

    @FXML
    public void onKeyPressed(KeyEvent e) {
        if (e.getText().equals("d")) {
            toggleDebugMode();
        }
        if (e.getText().equals("s")) {
            canvas.showRoute();
            canvas.goToPosition(model.aStarMinX, model.aStarMaxX, model.aStarMaxY);
        }
        if (e.getText().equals("h")) {
            canvas.hideRoute();
        }
    }

    @FXML
    private void onScrollOnCanvas(ScrollEvent e) {
        double factor = Math.pow(1.01, e.getDeltaY());
        canvas.zoom(factor, new Point2D(e.getX(), e.getY()));
        updateScaleBar();
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
    private Button removePin;
    @FXML
    private void onMouseReleasedOnCanvas(MouseEvent e) {
        if (singleClick) {
            hideAll();
            String coordinates = canvas.setPin(new Point2D(e.getX(), e.getY()));
            changeType("pin", true);
            pinText.textProperty().setValue(coordinates);
            removePin.setOnAction(event -> {
                canvas.setPin = false;
                canvas.repaint();
                hideAll();
            });
        } else {
            singleClick = true;
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

    public void onMousePressedSearch() {
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

    public void onMousePressedRoute() {
        if (routeContainer.isVisible()) {
            hideAll();
            canvas.hideRoute();
        } else {
            changeType("route", true);
            canvas.showRoute();
        }
    }

    public void onMousePressedSettings() {
        if (settingsContainer.isVisible()) {
            hideAll();
        } else {
            changeType("settings", true);
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

    public void hideAll() {
        changeType("search", false);
    }

    public void fadeButtons() {
        searchButton.setStyle("-fx-opacity: .5");
        routeButton.setStyle("-fx-opacity: .5");
        settingsButton.setStyle("-fx-opacity: .5");
    }

    public void changeType(String type, boolean state) {
        if (canvas.setPin && type != "pin" && type != "debug") {
            canvas.setPin = false;
            canvas.repaint();
        }
        if (type != "route" && type != "debug") {
            canvas.hideRoute();
            canvas.repaint();
        }
        if (!type.equals("debug")) {
            searchContainer.setVisible(false);
            searchContainer.setManaged(false);
            routeContainer.setVisible(false);
            routeContainer.setManaged(false);
            settingsContainer.setVisible(false);
            settingsContainer.setManaged(false);
            pinContainer.setVisible(false);
            pinContainer.setManaged(false);
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
            case "pin":
                pinContainer.setVisible(state);
                pinContainer.setManaged(state);
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

    public void shutdownExecutor() {
        debug.shutdownExecutor();
    }

    @FXML
    private HBox scaleContainer;
    @FXML
    private VBox scale;
    public void updateScaleBar() {
        double scaleWidth = (canvas.getWidth() / 10) + 40;
        scaleContainer.setPrefWidth(scaleWidth);
        scale.setPrefWidth(scaleWidth);
        double scaleValue = Math.round(canvas.getDistanceWidth()) / 10.0;

        String metric;
        if (scaleValue < 1) {
            scaleValue = Math.round(canvas.getDistanceWidth() * 100);
            metric = " M";
        } else {
            scaleValue = Math.round(canvas.getDistanceWidth()) / 10.0;
            metric = " KM";
        }
        scaletext.textProperty().setValue(String.valueOf(scaleValue + metric));
    }

    public void onMousePressedPinHeart() {
        //add this point to POI
        model.addPOI(new POI("Near to #", "place", (float) canvas.getPinPoint().getX(), (float) canvas.getPinPoint().getY()));
        canvas.setPin = false;
        canvas.repaint();
        updateUserPOI();
    }

    @FXML
    private VBox userPOI;
    public void updateUserPOI() {
        userPOI.getChildren().clear();
        model.getPointsOfInterest().forEach(POI -> {
            Button currentPOI = new Button(POI.getName());
            userPOI.getChildren().add(currentPOI);
            currentPOI.setOnAction(event -> {
                canvas.goToPosition(POI.getX(), POI.getX() + 0.0002, POI.getY());
                canvas.repaint();
            });
        });
    }

    public void toggleShowNames() {
        canvas.showNames = !canvas.showNames;
        canvas.repaint();
    }

    @FXML
    private VBox routeDescription;
    @FXML
    private VBox routeStepsContainer;
    @FXML
    private Text arrivalText;
    @FXML
    private Text arrivalSmallText;
    public void showRoute() {
        routeDescription.setVisible(true);
        routeDescription.setManaged(true);
        routeStepsContainer.getChildren().clear();
        List<Step> routeSteps = model.getAStar().getPathDescription();
        for (Step temp : routeSteps) {
            HBox stepContainer = new HBox();
            stepContainer.setAlignment(Pos.CENTER_LEFT);
            stepContainer.getStyleClass().add("stepContainer");
            String imagePath;
            imagePath = temp.getDirection().toString().toLowerCase();
            if (imagePath.equals("continue")) {
                imagePath = "follow";
            } else if (imagePath.equals("arrival")) {
                imagePath = "pin";
            }
            Image stepIcon = new Image("bfst21/icons/" + imagePath + ".png");
            ImageView stepIconContainer = new ImageView(stepIcon);
            Label stepDescription = new Label(temp.toString());
            stepContainer.setHgrow(stepIconContainer, SOMETIMES);
            stepContainer.setHgrow(stepDescription, SOMETIMES);
            stepIconContainer.getStyleClass().add("stepIcon");
            stepIconContainer.setFitWidth(22.0);
            stepIconContainer.setFitHeight(22.0);
            stepIconContainer.setPickOnBounds(true);
            stepIconContainer.setPreserveRatio(true);
            stepContainer.getChildren().add(stepIconContainer);
            stepContainer.getChildren().add(stepDescription);
            stepDescription.maxWidth(Double.POSITIVE_INFINITY);
            stepDescription.getStyleClass().add("labelTest");
            stepDescription.setWrapText(true);
            routeStepsContainer.getChildren().add(stepContainer);
        }
        arrivalText.setText(String.valueOf(model.getAStar().getTotalDistance()));
        arrivalSmallText.setText(model.getAStar().getTotalTime());
    }

    public void hideRoute() {
        routeDescription.setVisible(false);
        routeDescription.setManaged(false);
    }

    @FXML
    private ToggleGroup selectTransportTypeSettings;
    public void selectTransportType() {
        ToggleButton currentButton = (ToggleButton) selectTransportTypeSettings.getSelectedToggle();
        model.setCurrentTransportType(TransportType.valueOf(currentButton.getText().toUpperCase()));
    }

    @FXML
    private CheckBox showAStarPath;
    public void toggleAStarDebugPath() {
        if (showAStarPath.isSelected()) {
            canvas.debugAStar = true;
            canvas.repaint();
        } else {
            canvas.debugAStar = false;
            canvas.repaint();
        }
    }

    @FXML
    private ToggleGroup selectTransportTypeRoute;
    public void selectTransportTypeRoute() {
        ToggleButton currentButton = (ToggleButton) selectTransportTypeRoute.getSelectedToggle();
        if (currentButton != null) {
            model.setCurrentTransportType(TransportType.valueOf(currentButton.getId().split("-")[0].toUpperCase()));
            model.getAStar().AStarSearch(model.getNodeIndex().getMember((long) fromNodeId), model.getNodeIndex().getMember((long) toNodeId), model.getCurrentTransportType());
            showRoute();
            canvas.repaint(); //To show the route after it has been calculated
        }
    }
}
