package bfst21;

import bfst21.Rtree.Rtree;
import bfst21.osm.*;
import bfst21.pathfinding.AStar;
import bfst21.pathfinding.TransportType;
import bfst21.search.RadixTree;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {
    private Map<Tag, List<Drawable>> drawableMap;
    private Map<Tag, List<Drawable>> fillMap;

    private POI_KDTree POITree;
    private MemberIndex<Node> nodeIndex;
    private MemberIndex<Way> wayIndex;
    private MemberIndex<Relation> relationIndex;
    private RadixTree streetTree;
    private List<Drawable> islands = new ArrayList<>();
    private ArrayList<Way> coastlines;
    HashMap<String, Image> imageSet;

    private ArrayList<POI> pointsOfInterest;
    private ArrayList<POI> systemPointsOfInterest;
    private Rtree roadRTree;
    private Node nearestNode;

    private boolean ttiMode;

    private AStar aStar;
    private List<Node> AStarPath;
    private List<Node> AStarDebugPath;
    private TransportType defaultTransportType = TransportType.CAR;
    private TransportType currentTransportType = defaultTransportType;
    float aStarMinX, aStarMaxX, aStarMinY, aStarMaxY;

    private float minX, minY, maxX, maxY;
    private List<AreaName> areaNames;

    // Scale nodes latitude to account for the curvature of the earth
    public final static float scalingConstant = 0.56f;

    public Model(String filePath, boolean ttiMode) {
        // Java wouldn't let me expand this into variables. Im very sorry about the mess
        this(
                Model.class.getResourceAsStream(filePath),
                OSMParser.genFileExtension(filePath),
                filePath,
                ttiMode
        );
    }

    public Model(InputStream in, FileExtension fileExtension, String fileName, boolean ttiMode) {
        drawableMap = new HashMap<>();
        fillMap = new HashMap<>();

        POITree = new POI_KDTree(this);

        nodeIndex = new MemberIndex<>();
        coastlines = new ArrayList<>();
        wayIndex = new MemberIndex<>();
        relationIndex = new MemberIndex<>();
        streetTree = new RadixTree();
        areaNames = new ArrayList<>();

        pointsOfInterest = new ArrayList<>();
        systemPointsOfInterest = new ArrayList<>();

        this.ttiMode = ttiMode;

        String[] fileNameParts = fileName.split("/");

        try {
            OSMParser.readMapElements(in, fileExtension, fileNameParts[fileNameParts.length - 1], this);
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }

        imageSet = new HashMap<>();
        imageSet.put("mill", new Image("bfst21/icons/" + "mill" + ".png"));
        imageSet.put("museum", new Image("bfst21/icons/" + "museum" + ".png"));
        imageSet.put("theme_park", new Image("bfst21/icons/" + "theme_park" + ".png"));
        imageSet.put("aerodrome", new Image("bfst21/icons/" + "aerodrome" + ".png"));
        imageSet.put("cinema", new Image("bfst21/icons/" + "cinema" + ".png"));
        imageSet.put("castle", new Image("bfst21/icons/" + "castle" + ".png"));
        imageSet.put("viewpoint", new Image("bfst21/icons/" + "viewpoint" + ".png"));
        imageSet.put("statue", new Image("bfst21/icons/" + "statue" + ".png"));
        imageSet.put("zoo", new Image("bfst21/icons/" + "zoo" + ".png"));
        imageSet.put("suitcase", new Image("bfst21/icons/" + "suitcase" + ".png"));
        imageSet.put("viewpoint", new Image("bfst21/icons/" + "viewpoint" + ".png"));
        imageSet.put("default", new Image("bfst21/icons/" + "default" + ".png"));
        imageSet.put("heart", new Image("bfst21/icons/" + "heart" + ".png"));

        List<Drawable> roadList = new ArrayList<>();
        for (Tag tag : drawableMap.keySet()) {
            roadList.addAll(drawableMap.get(tag));
        }
        roadRTree = new Rtree(roadList);

        System.out.println("here");
    }

    /*
     * getters, setters and adders
     */
    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public POI_KDTree getPOITree(){
        return POITree;
    }

    public MemberIndex<Node> getNodeIndex() {
        return nodeIndex;
    }

    public void setNodeIndex(MemberIndex<Node> nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    public void addToNodeIndex(Node node) {
        nodeIndex.addMember(node);
    }

    public MemberIndex<Way> getWayIndex() {
        return wayIndex;
    }

    public void setWayIndex(MemberIndex<Way> wayIndex) {
        this.wayIndex = wayIndex;
    }

    public void addToWayIndex(Way way) {
        wayIndex.addMember(way);
    }

    public MemberIndex<Relation> getRelationIndex() {
        return relationIndex;
    }

    public void setRelationIndex(MemberIndex<Relation> relationIndex) {
        this.relationIndex = relationIndex;
    }

    public void addToRelationIndex(Relation relation) {
        relationIndex.addMember(relation);
    }

    public ArrayList<Way> getCoastlines() {
        return coastlines;
    }

    public void setCoastlines(ArrayList<Way> coastlines) {
        this.coastlines = coastlines;
    }

    public void addCoastline(Way way) {
        coastlines.add(way);
    }

    public List<Drawable> getIslands() {
        return islands;
    }

    public void setIslands(List<Drawable> islands) {
        this.islands = islands;
    }

    public Map<Tag, List<Drawable>> getDrawableMap() {
        return drawableMap;
    }

    public void setDrawableMap(Map<Tag, List<Drawable>> drawableMap) {
        this.drawableMap = drawableMap;
    }

    public Map<Tag, List<Drawable>> getFillMap() {
        return fillMap;
    }

    public void setFillMap(Map<Tag, List<Drawable>> fillMap) {
        this.fillMap = fillMap;
    }

    public boolean getTtiMode() {
        return ttiMode;
    }

    public RadixTree getStreetTree() {
        return streetTree;
    }

    public void setStreetTree(RadixTree streetTree) {
        this.streetTree = streetTree;
    }

    public void setUpAStar() {
        aStar = new AStar(this);
    }

    public AStar getAStar() {
        return aStar;
    }

    public boolean existsAStarPath() {
        return AStarPath != null;
    }

    public List<Node> getAStarPath() {
        return AStarPath;
    }

    public void setAStarPath(List<Node> AStarPath) {
        this.AStarPath = AStarPath;
    }

    public List<Node> getAStarDebugPath() {
        return AStarDebugPath;
    }

    public void setAStarDebugPath(List<Node> AStarDebugPath) {
        this.AStarDebugPath = AStarDebugPath;
    }

    public void setCurrentTransportType(TransportType type) {
        this.currentTransportType = type;
    }

    public void setDefaultTransportType(TransportType type) {
        this.defaultTransportType = type;
        setCurrentTransportType(type);
    }

    public TransportType getCurrentTransportType() {
        return currentTransportType;
    }

    public TransportType getDefaultTransportType() {
        return defaultTransportType;
    }

    public void setAStarBounds(float minX, float minY, float maxX, float maxY) {
        this.aStarMinX = minX;
        this.aStarMinY = minY;
        this.aStarMaxX = maxX;
        this.aStarMaxY = maxY;
    }

    public void addPOI(POI poi) {
        pointsOfInterest.add(poi);
    }

    public void removePOI(POI poi) {
        pointsOfInterest.remove(poi);
    }

    public ArrayList<POI> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void addToAreaNamesIndex(AreaName areaName) {
        areaNames.add(areaName);
    }

    public List<AreaName> getAreaNames() {
        return areaNames;
    }

    public Rtree getRoadRTree() {
        return roadRTree;
    }

    public void setNearestNode(Node nearestNode) {
        this.nearestNode = nearestNode;
    public void addSystemPOI(POI poi) {systemPointsOfInterest.add(poi);}

    public ArrayList<POI> getSystemPointsOfInterest() {return systemPointsOfInterest;}

    public void addToCityIndex(City city) {
        cities.add(city);
    }

    public Node getNearestNode() {
        return nearestNode;
    }

    public List<City> getCities(){return cities;}
}
