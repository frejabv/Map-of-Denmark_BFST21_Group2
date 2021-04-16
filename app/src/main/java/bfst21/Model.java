package bfst21;

import bfst21.osm.*;
import bfst21.pathfinding.AStar;
import bfst21.pathfinding.TransportType;
import bfst21.search.RadixTree;

import java.util.*;

public class Model {
    private Map<Tag, List<Drawable>> drawableMap;
    private Map<Tag, List<Drawable>> fillMap;

    // drawables are all ways that not in any other list
    private List<Drawable> drawables;
    private MemberIndex<Node> nodeIndex;
    private MemberIndex<Way> wayIndex;
    private MemberIndex<Relation> relationIndex;
    private RadixTree streetTree;
    private List<Drawable> islands = new ArrayList<>();
    private ArrayList<Way> coastlines;
    private boolean ttiMode;
    private List<Node> AStarPath;
    private AStar aStar;
    private TransportType currentTransportType = TransportType.CAR;

    private final ArrayList<Tag> driveable = new ArrayList<>(Arrays.asList(Tag.LIVING_STREET,Tag.MOTORWAY,Tag.PEDESTRIAN,Tag.PRIMARY, Tag.RESIDENTIAL, Tag.ROAD, Tag.SECONDARY, Tag.SERVICE, Tag.TERTIARY, Tag.TRACK, Tag.TRUNK, Tag.UNCLASSIFIED));
    private final ArrayList<Tag> cyclable = new ArrayList<>(Arrays.asList(Tag.CYCLEWAY, Tag.LIVING_STREET,Tag.PATH, Tag.PEDESTRIAN, Tag.RESIDENTIAL, Tag.ROAD, Tag.SECONDARY, Tag.SERVICE, Tag.TERTIARY, Tag.TRACK, Tag.UNCLASSIFIED));
    private final ArrayList<Tag> walkable = new ArrayList<>(Arrays.asList(Tag.FOOTWAY, Tag.LIVING_STREET,Tag.PATH, Tag.PEDESTRIAN, Tag.RESIDENTIAL, Tag.ROAD, Tag.SERVICE, Tag.TRACK, Tag.UNCLASSIFIED));

    private float minX, minY, maxX, maxY;

    public Model(String filepath, boolean ttiMode) {
        drawableMap = new HashMap<>();
        fillMap = new HashMap<>();

        drawables = new ArrayList<>();
        nodeIndex = new MemberIndex<>();
        coastlines = new ArrayList<>();
        wayIndex = new MemberIndex<>();
        relationIndex = new MemberIndex<>();
        streetTree = new RadixTree();

        this.ttiMode = ttiMode;

        try {
            OSMParser.readMapElements(filepath, this);
        } catch (Exception e) {
            System.out.println("error: " + e.getClass() + " " + e.getMessage());
        }
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

    public void addToNodeIndex(Node node) {
        nodeIndex.addMember(node);
    }

    public List<Drawable> getDrawables() {
        return drawables;
    }

    public MemberIndex<Way> getWayIndex() {
        return wayIndex;
    }

    public void addToWayIndex(Way way) {
        wayIndex.addMember(way);
    }

    public MemberIndex<Relation> getRelationIndex() {
        return relationIndex;
    }

    public void addToRelationIndex(Relation relation) {
        relationIndex.addMember(relation);
    }

    public ArrayList<Way> getCoastlines() {
        return coastlines;
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

    public Map<Tag, List<Drawable>> getFillMap() {
        return fillMap;
    }

    public boolean getTtiMode() {
        return ttiMode;
    }

    public RadixTree getStreetTree() {
        return streetTree;
    }

    public boolean existsAStarPath(){
        return AStarPath != null;
    }

    public List<Node> getAStarPath(){
        return AStarPath;
    }

    public void setAStarPath(List<Node> AStarPath){
        this.AStarPath = AStarPath;
    }

    public void setUpAStar() {
        aStar = new AStar(this);
    }

    public AStar getAStar() {
        return aStar;
    }

    public void setCurrentTransportType(TransportType type){ this.currentTransportType = type; }

    public TransportType getCurrentTransportType(){ return currentTransportType; }

    public ArrayList<Tag> getDriveableTags() {
        return driveable;
    }

    public ArrayList<Tag> getCyclableTags() {
        return cyclable;
    }

    public ArrayList<Tag> getWalkableTags() {
        return walkable;
    }
}
