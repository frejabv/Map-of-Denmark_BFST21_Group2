package bfst21;

import bfst21.Rtree.Rtree;
import bfst21.osm.*;
import bfst21.pathfinding.AStar;
import bfst21.pathfinding.TransportType;
import bfst21.search.RadixTree;
import bfst21.osm.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


import javax.xml.stream.XMLStreamException;

public class Model {
    private Map<Tag, List<Drawable>> drawableMap;
    private Map<Tag, List<Drawable>> fillMap;

    private ArrayList<Tag> drawableTagPriority;
    private ArrayList<Tag> fillableTagPriority;

    private MemberIndex<Node> nodeIndex;
    private MemberIndex<Way> wayIndex;
    private MemberIndex<Relation> relationIndex;
    private RadixTree streetTree;
    private List<Drawable> islands = new ArrayList<>();
    private ArrayList<Way> coastlines;

    private ArrayList<POI> pointsOfInterest;
    private Rtree roadRTree;

    private boolean ttiMode;

    private AStar aStar;
    private List<Node> AStarPath;
    private List<Node> AStarDebugPath;
    private TransportType currentTransportType = TransportType.CAR;
    float aStarMinX, aStarMaxX, aStarMinY, aStarMaxY;

    private float minX, minY, maxX, maxY;
    private List<City> cities;

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

        nodeIndex = new MemberIndex<>();
        coastlines = new ArrayList<>();
        wayIndex = new MemberIndex<>();
        relationIndex = new MemberIndex<>();
        streetTree = new RadixTree();
        cities = new ArrayList<>();
        drawableTagPriority = new ArrayList<>();
        fillableTagPriority = new ArrayList<>();

        pointsOfInterest = new ArrayList<>();

        this.ttiMode = ttiMode;

        String[] fileNameParts = fileName.split("/");

        try {
            OSMParser.readMapElements(in, fileExtension, fileNameParts[fileNameParts.length - 1], this);
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }

        List<Drawable> roadList = new ArrayList<>();
        for (Tag tag: drawableMap.keySet()) {
            roadList.addAll(drawableMap.get(tag));
        }
        roadRTree = new Rtree(roadList);

        drawableMap.forEach((tag, drawables) -> {
            drawableTagPriority.add(tag);
        });
        fillMap.forEach((tag, drawables) -> {
            fillableTagPriority.add(tag);
        });
        drawableTagPriority.sort((a, b) -> Integer.compare(a.layer, b.layer));
        fillableTagPriority.sort((a, b) -> Integer.compare(a.layer, b.layer));
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

    public TransportType getCurrentTransportType() {
        return currentTransportType;
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

    public ArrayList<POI> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void addToCityIndex(City city) {
        cities.add(city);
    }

    public List<City> getCities() {
        return cities;
    }

    public Rtree getRoadRTree() {
        return roadRTree;
    }

    public ArrayList<Tag> getDrawableTagPriority(){
        return drawableTagPriority;
    }

    public ArrayList<Tag> getFillableTagPriority(){ return fillableTagPriority; }
}
