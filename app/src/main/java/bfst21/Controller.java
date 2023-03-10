package bfst21;

import bfst21.POI.POI;
import bfst21.osm.Node;
import bfst21.osm.Way;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.text.DecimalFormat;
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
    private Text closestRoad;
    @FXML
    private VBox leftContainer;
    @FXML
    private HBox rightContainer;
    @FXML
    private CheckBox showNames;
    @FXML
    private VBox NearbyPOI;
    @FXML
    private VBox removePinContainer;

    private Debug debug;
    private Point2D lastMouse;
    private boolean singleClick = true;
    private Model model;
    private final ArrayList<Text> suggestionList = new ArrayList<>();
    private Node fromNode, toNode;
    private POI currentPOI;

    @FXML
    private VBox regexContainer;
    @FXML
    private Button removePin;
    @FXML
    private HBox scaleContainer;
    @FXML
    private VBox scale;
    @FXML
    private ImageView heartIcon;
    @FXML
    private VBox userPOI;
    @FXML
    private VBox routeDescription;
    @FXML
    private VBox routeStepsContainer;
    @FXML
    private Text arrivalText;
    @FXML
    private Text arrivalSmallText;
    @FXML
    private CheckBox showAStarPath;
    @FXML
    private ToggleGroup selectTransportTypeRoute;
    @FXML
    private ToggleGroup selectTransportTypeSettings;
    @FXML
    private ToggleButton carRoute;
    @FXML
    private ToggleButton bicycleRoute;
    @FXML
    private ToggleButton walkRoute;
    @FXML
    private ToggleButton carSettings;
    @FXML
    private ToggleButton bicycleSettings;
    @FXML
    private ToggleButton walkSettings;

    public void init(Model model) {
        this.model = model;
        String OS = System.getProperty("os.name").toLowerCase();

        //Check if OS is Linux
        if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
            canvas.showNames = false;
            showNames.setVisible(false);
            showNames.setManaged(false);
        }

        model.initImageSet();

        canvas.init(model);
        canvas.setCurrentCanvasEdges();
        updateScaleBar();
        hideAll();
        debug = new Debug(canvas, cpuProcess, cpuSystem, ttd, memoryUse);
        changeType("debug", false);
        Regex regex = new Regex(setupRegexView());

        setUpSearchField(regex);
        setUpRouteFields(regex);

        if (model.getTtiMode()) {
            System.exit(0);
        }

        leftContainer.setMaxWidth(canvas.getWidth() / 100 * 33);
        rightContainer.setMaxWidth(canvas.getWidth() / 100 * 50);

        model.setUpAStar();
    }

    private List<Text> setupRegexView() {
        List<Text> regexVisualisers = new ArrayList<>();
        List<String> regexString = Arrays.asList("[Street] [House] [Floor] [Side] [Postcode] [More]", "[Street] [House] [Floor] [Postcode] [More]", "[Roadname] [Number] [Postcode] ([More])", "[Roadname] [Number] [Floor] [Side] ([More])", "[Roadname] [Number] ([More])", "[Roadname] ([More])");
        for (String s : regexString) {
            HBox hbox = new HBox();
            hbox.getStyleClass().add("regexLine");
            Text bullet = new Text("\u25CF");
            bullet.getStyleClass().add("regexMatch");
            Text text = new Text(s);
            hbox.getChildren().add(bullet);
            hbox.getChildren().add(text);
            regexVisualisers.add(bullet);
            regexContainer.getChildren().add(hbox);
        }
        return regexVisualisers;
    }

    public void setUpSearchField(Regex regex) {
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            String cleanedInput = regex.run(newText);
            addSuggestions(model, "search", null, cleanedInput);
        });

        searchField.setOnAction(e -> {
            if (!suggestionList.isEmpty()) {
                searchField.textProperty().setValue(suggestionList.get(0).getText());
                Node node = model.getNodeIndex().getMember(model.getStreetTree().lookupNode(suggestionList.get(0).getText()).getId());
                canvas.setPin(node.getX(), node.getY());
                if (canvas.getDistanceWidth() < 0.2){
                    canvas.goToPosition(node.getX(), node.getX() + 0.0002, node.getY());
                }
                canvas.goToPosition(node.getX(), node.getX() + 0.0002, node.getY());
                updateScaleBar();
                searchContainer.getChildren().removeAll(suggestionList);
                suggestionList.clear();
                searchContainer.getChildren().removeAll(searchContainer.lookup("#suggestionsHr"));
            }
        });
    }

    public void setUpRouteFields(Regex regex) {
        routeFieldFrom.textProperty().addListener((obs, oldText, newText) -> {
            hideRoute();
            String cleanedInput = regex.run(newText);
            addSuggestions(model, "route", "from", cleanedInput);
            if (newText.length() < oldText.length()) {
                canvas.hideRoute();
                fromNode = null;
                model.setAStarPath(null);
            }
        });

        routeFieldTo.textProperty().addListener((obs, oldText, newText) -> {
            hideRoute();
            String cleanedInput = regex.run(newText);
            addSuggestions(model, "route", "to", cleanedInput);
            if (newText.length() < oldText.length()) {
                canvas.hideRoute();
                toNode = null;
                model.setAStarPath(null);
            }
        });

        routeFieldFrom.setOnAction(e -> {
            if (!suggestionList.isEmpty()) {
                routeFieldFrom.textProperty().setValue(suggestionList.get(0).getText());
                Node nodeFrom = model.getNodeIndex().getMember(model.getStreetTree().lookupNode(routeFieldFrom.getText()).getId());
                Point2D p = new Point2D(nodeFrom.getX(), nodeFrom.getY());
                fromNode = model.getRoadRTree().nearestWay(p).nearestNode(p);
                if (toNode != null) {
                    model.getAStar().AStarSearch(fromNode, toNode, model.getCurrentTransportType());
                    showRouteDescription();
                    canvas.showRoute();
                    canvas.repaint();
                }
                routeContainer.getChildren().removeAll(suggestionList);
                suggestionList.clear();
                routeContainer.getChildren().removeAll(routeContainer.lookup("#suggestionsHr"));
                routeFieldTo.requestFocus();
            }
        });

        routeFieldTo.setOnAction(e -> {
            if (!suggestionList.isEmpty()) {
                routeFieldTo.textProperty().setValue(suggestionList.get(0).getText());
                Node nodeTo = model.getNodeIndex().getMember(model.getStreetTree().lookupNode(routeFieldTo.getText()).getId());
                Point2D p = new Point2D(nodeTo.getX(), nodeTo.getY());
                toNode = model.getRoadRTree().nearestWay(p).nearestNode(p);
                if (fromNode != null) {
                    model.getAStar().AStarSearch(fromNode, toNode, model.getCurrentTransportType());
                    showRouteDescription();
                    canvas.repaint();
                    canvas.showRoute();
                }
                routeContainer.getChildren().removeAll(suggestionList);
                suggestionList.clear();
                routeContainer.getChildren().removeAll(routeContainer.lookup("#suggestionsHr"));
                if (routeFieldFrom.getText().length() == 0) {
                    routeFieldFrom.requestFocus();
                }
            }
        });
    }

    public void addSuggestions(Model model, String containerType, String fieldType, String cleanedInput) {
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
            ArrayList<RadixNode> suggestions = model.getStreetTree().getSuggestions(cleanedInput);
            for (int i = 0; i < Math.min(8, suggestions.size()); i++) {
                RadixNode suggestion = suggestions.get(i);
                Text newSuggestion = new Text(suggestion.getFullName());
                newSuggestion.getStyleClass().add("suggestion");
                newSuggestion.setOnMouseClicked(e -> {
                    selectedField.textProperty().setValue(suggestion.getFullName());
                    Node node = model.getNodeIndex().getMember(suggestion.getId());
                    if (containerType.equals("search")) {
                        canvas.setPin(node.getX(), node.getY());
                        if (canvas.getDistanceWidth() < 0.2){
                            canvas.goToPosition(node.getX(), node.getX() + 0.0002, node.getY());
                        }
                        canvas.goToPosition(node.getX(), node.getX() + 0.0002, node.getY());
                        updateScaleBar();
                    } else {
                        if (fieldType.equals("from")) {
                            Point2D p = new Point2D(node.getX(), node.getY());
                            fromNode = model.getRoadRTree().nearestWay(p).nearestNode(p);
                        } else {
                            Point2D p = new Point2D(node.getX(), node.getY());
                            toNode = model.getRoadRTree().nearestWay(p).nearestNode(p);
                        }
                        if (fromNode != null && toNode != null) {
                            model.getAStar().AStarSearch(fromNode, toNode, model.getCurrentTransportType());
                            showRouteDescription();
                            canvas.showRoute();
                            canvas.repaint();
                        }
                    }
                    selectedContainer.getChildren().removeAll(suggestionList);
                    suggestionList.clear();
                    selectedContainer.getChildren().removeAll(selectedContainer.lookup("#suggestionsHr"));
                });
                suggestionList.add(newSuggestion);
            }
        }
        if (suggestionList.size() > 0) {
            Region hr = new Region();
            hr.setId("suggestionsHr");
            hr.getStyleClass().add("hr");
            selectedContainer.getChildren().remove(selectedContainer.lookup("#suggestionsHr"));
            selectedContainer.getChildren().add(hr);
            selectedContainer.getChildren().addAll(suggestionList);
        } else {
            selectedContainer.getChildren().removeAll(selectedContainer.lookup("#suggestionsHr"));
        }
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
    private void onMouseReleasedOnCanvas(MouseEvent e) {
        if (singleClick) {
            hideAll();
            changeType("pin", true);
            removePinContainer.getChildren().removeAll(removePinContainer.lookup(".button"));
            Point2D coordinates = canvas.setPin(new Point2D(e.getX(), e.getY()));
            DecimalFormat df = new DecimalFormat("#.#####");
            String xCoordinate = df.format(coordinates.getX()).replace(",",".");
            String yCoordinate = df.format(coordinates.getY() * -model.scalingConstant).replace(",",".");
            if (currentPOI != null && currentPOI.getX() != canvas.getPinPoint().getX() || currentPOI != null && currentPOI.getY() != canvas.getPinPoint().getY()) {
                currentPOI = null;
                heartIcon.setImage(new Image(getClass().getResource("/bfst21/icons/heart-border.png").toString()));
            }
            pinText.textProperty().setValue(xCoordinate + ", " + yCoordinate);
            removePin.setVisible(true);
            removePin.setManaged(true);
            removePinContainer.getChildren().add(removePin);

            updateNearbyPOI();
        } else {
            singleClick = true;
        }
    }

    private void updateNearbyPOI() {
        NearbyPOI.setVisible(true);
        NearbyPOI.setManaged(true);
        Text nearbyAttractionsText = new Text("Nearby Attractions");
        Region region = new Region();
        region.getStyleClass().add("hr");
        NearbyPOI.getChildren().clear();
        NearbyPOI.getChildren().add(nearbyAttractionsText);
        NearbyPOI.getChildren().add(region);
        ArrayList<POI> poiArrayList = model.getPOITree().nearest(canvas.pinPoint, 5);
        if (poiArrayList != null && poiArrayList.size() > 0) {
            for (POI poi : poiArrayList) {
                HBox nearbyContainer = new HBox();
                nearbyContainer.getStyleClass().add("nearbyPOIContainer");
                StackPane stackPane = new StackPane();
                if (poi.getImageType().equals("heart")) {
                    stackPane.setStyle("-fx-background-color:WHITE;-fx-background-radius: 15;-fx-min-width: 30;-fx-border-width: 1px;-fx-border-color: black;-fx-border-radius: 15;");
                } else {
                    stackPane.setStyle("-fx-background-color:rgba(52,152,219,1);-fx-background-radius: 15;-fx-min-width: 30;");
                }
                Image image = model.imageSet.get(poi.getImageType());
                ImageView imageview = new ImageView(image);
                imageview.getStyleClass().add("imageView");
                imageview.setFitHeight(16.0);
                imageview.setFitWidth(16.0);
                imageview.setPreserveRatio(true);
                imageview.getStyleClass().add("nearbyPOIImage");
                VBox textlines = new VBox();
                Text attractionName = new Text(poi.getName());
                Text attractionType = new Text(poi.getType().substring(0, 1).toUpperCase() + poi.getType().substring(1));
                attractionType.getStyleClass().add("attractionType");
                textlines.getChildren().add(attractionName);
                textlines.getChildren().add(attractionType);
                stackPane.getChildren().add(imageview);
                nearbyContainer.getChildren().add(stackPane);
                nearbyContainer.getChildren().add(textlines);
                NearbyPOI.getChildren().add(nearbyContainer);
            }
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
        if (canvas.setPin && !type.equals("pin") && !type.equals("debug")) {
            canvas.setPin = false;
            canvas.repaint();
        }
        if (!type.equals("route") && !type.equals("debug")) {
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
        searchContainer.getChildren().removeAll(suggestionList);
        routeContainer.getChildren().removeAll(suggestionList);
        suggestionList.clear();
        switch (type) {
            case "route":
                if (routeFieldFrom.getText().length() > 0 && routeFieldTo.getText().length() > 0) {
                    showRouteDescription();
                } else {
                    hideRoute();
                }
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
        scaletext.textProperty().setValue(scaleValue + metric);
    }

    public void onMousePressedPinHeart() {
        //add this point to POI
        Way road = model.getRoadRTree().nearestWay(new Point2D(canvas.getPinPoint().getX(), canvas.getPinPoint().getY()));
        String roadname = getClosestRoadString(road);
        String[] heartIconFilePath = heartIcon.getImage().getUrl().split("/");
        if (heartIconFilePath[heartIconFilePath.length - 1].equals("heart-border.png")) {
            if (currentPOI == null) {
                currentPOI = new POI("Near " + roadname, "user-defined", "heart", (float) canvas.getPinPoint().getX(), (float) canvas.getPinPoint().getY());
            }
            heartIcon.setImage(new Image(getClass().getResource("/bfst21/icons/heart.png").toString()));
            removePin.setVisible(false);
            removePin.setManaged(false);
            model.addPOI(currentPOI);
        } else {
            heartIcon.setImage(new Image(getClass().getResource("/bfst21/icons/heart-border.png").toString()));
            model.removePOI(currentPOI);
            changeType("pin", false);
            currentPOI = null;
        }
        canvas.setPin = false;
        canvas.repaint();
        updateUserPOI();
    }

    public void updateUserPOI() {
        userPOI.getChildren().clear();
        model.getPointsOfInterest().forEach(poi -> {
            Button currentPOILine = new Button(poi.getName());
            userPOI.getChildren().add(currentPOILine);
            currentPOILine.setOnAction(event -> {
                currentPOI = poi;
                changeType("pin", true);
                removePin.setVisible(false);
                removePin.setManaged(false);
                heartIcon.setImage(new Image(getClass().getResource("/bfst21/icons/heart.png").toString()));
                if (canvas.getDistanceWidth() < 0.2){
                    canvas.goToPosition(poi.getX(), poi.getX() + 0.0002, poi.getY());
                }
                canvas.goToPosition(poi.getX(), poi.getX() + 0.0002, poi.getY());
                updateScaleBar();
                canvas.repaint();
            });
        });
    }

    public void toggleKDLines() {
        canvas.kdLines = !canvas.kdLines;
        canvas.repaint();
        hideRoute();
    }

    public void toggleShowNames() {
        canvas.showNames = !canvas.showNames;
        canvas.repaint();
    }

    public void toggleShowPoi() {
        canvas.showPoi = !canvas.showPoi;
        canvas.repaint();
    }

    public void showRouteDescription() {
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
            HBox.setHgrow(stepIconContainer, SOMETIMES);
            HBox.setHgrow(stepDescription, SOMETIMES);
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
        setCurrentTransportType(model.getDefaultTransportType());
        routeDescription.setVisible(false);
        routeDescription.setManaged(false);
    }

    public void toggleAStarDebugPath() {
        if (showAStarPath.isSelected()) {
            canvas.debugAStar = true;
            canvas.repaint();
        } else {
            canvas.debugAStar = false;
            canvas.repaint();
        }
    }
    private ToggleButton oldTransportTypeRoute = carRoute;
    public void selectTransportTypeRoute() {
        ToggleButton currentRouteToggle = (ToggleButton) selectTransportTypeRoute.getSelectedToggle();
        String transportTypeCleaned = carRoute.getId().split("-")[0].toUpperCase();
        if (currentRouteToggle != null) {
            transportTypeCleaned = currentRouteToggle.getId().split("-")[0].toUpperCase();
        }
        else if (oldTransportTypeRoute != null) {
            transportTypeCleaned = oldTransportTypeRoute.getId().split("-")[0].toUpperCase();
        }
        setCurrentTransportType(TransportType.valueOf(transportTypeCleaned));
        model.getAStar().AStarSearch(fromNode, toNode, model.getCurrentTransportType());
        showRouteDescription();
        canvas.repaint(); //To show the route after it has been calculated
        if (currentRouteToggle != null) {
            oldTransportTypeRoute = currentRouteToggle;
        }
    }
    private ToggleButton oldTransportTypeSettings = carSettings;
    public void selectTransportTypeSettings() {
        ToggleButton currentSettingsToggle = (ToggleButton) selectTransportTypeSettings.getSelectedToggle();
        String transportTypeCleaned = carSettings.getId().split("-")[0].toUpperCase();
        if (currentSettingsToggle != null) {
            transportTypeCleaned = currentSettingsToggle.getId().split("-")[0].toUpperCase();
        }
        else if (oldTransportTypeSettings != null) {
            transportTypeCleaned = oldTransportTypeSettings.getId().split("-")[0].toUpperCase();
        }
        setCurrentTransportTypeSettings(TransportType.valueOf(transportTypeCleaned));
        model.setDefaultTransportType(TransportType.valueOf(transportTypeCleaned));
        if (currentSettingsToggle != null) {
            oldTransportTypeSettings = currentSettingsToggle;
        }
    }

    public void toggleSmallerViewPort() {
        canvas.smallerViewPort = !canvas.smallerViewPort;
        canvas.repaint();
    }

    public void toggleRTreeLines() {
        canvas.RTreeLines = !canvas.RTreeLines;
        canvas.repaint();
    }

    public void toggleRoadRectangles() {
        canvas.roadRectangles = !canvas.roadRectangles;
        canvas.repaint();
    }

    public void toggleNearestNodeLine() {
        canvas.nearestNodeLine = !canvas.nearestNodeLine;
        canvas.repaint();
    }

    public void updateClosestRoad(String text) {
        closestRoad.textProperty().setValue(text);
    }

    public String getClosestRoadString(Way road) {
        if (road.getName().equals("")) {
            return "Unnamed " + String.valueOf(road.getTag()).toLowerCase() + " road";
        } else {
            return road.getName();
        }
    }

    public void onMouseMovedOnCanvas(MouseEvent e) {
        Point2D mousePoint = canvas.mouseToModelCoords(new Point2D(e.getX(), e.getY()));
        Way road = model.getRoadRTree().nearestWay(mousePoint);

        updateClosestRoad(getClosestRoadString(road));

        model.setNearestNode(road.nearestNode(mousePoint));
        if (canvas.nearestNodeLine) {
            canvas.mousePoint = mousePoint;
            canvas.repaint();
        }
    }

    public void setCurrentTransportType(TransportType type) {
        model.setCurrentTransportType(type);
        carRoute.setSelected(false);
        bicycleRoute.setSelected(false);
        walkRoute.setSelected(false);
        if (type.equals(TransportType.CAR)) {
            carRoute.setSelected(true);
        } else if (type.equals(TransportType.BICYCLE)) {
            bicycleRoute.setSelected(true);
        } else if (type.equals(TransportType.WALK)) {
            walkRoute.setSelected(true);
        }
    }

    public void setCurrentTransportTypeSettings(TransportType type) {
        model.setCurrentTransportType(type);
        carSettings.setSelected(false);
        bicycleSettings.setSelected(false);
        walkSettings.setSelected(false);
        if (type.equals(TransportType.CAR)) {
            carSettings.setSelected(true);
        } else if (type.equals(TransportType.BICYCLE)) {
            bicycleSettings.setSelected(true);
        } else if (type.equals(TransportType.WALK)) {
            walkSettings.setSelected(true);
        }
    }

    public void toggleDoubleDraw() {
        canvas.doubleDraw = !canvas.doubleDraw;
        canvas.repaint();
    }

    public void removePin() {
        canvas.setPin = false;
        canvas.repaint();
        hideAll();
    }
}
