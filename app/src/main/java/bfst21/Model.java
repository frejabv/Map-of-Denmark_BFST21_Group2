package bfst21;

import java.util.ArrayList;
import java.util.List;

import bfst21.osm.*;

public class Model {
    private NodeIndex nodeIndex;
    private WayIndex wayIndex;
    //drawables are all ways that not in any other list
    private List<Drawable> drawables;
    private ArrayList<Way> coastlines;
    private List<Drawable> islands = new ArrayList<>();
    private ArrayList<Way> buildings, cycleways, footways, highways, junctions, living_streets, motorways, parks, paths, pedestrianWays, primaryWays, residentialWays, roads, secondaryWays, serviceWays, tertiaryWays, trackWays, trunkWays, unclassifiedWays, water;
    private Bounds bounds;

    private float minX, minY, maxX, maxY;

    public Model(String filepath) {
        nodeIndex = new NodeIndex();
        wayIndex = new WayIndex();

        drawables = new ArrayList<>();
        coastlines = new ArrayList<>();
        buildings = new ArrayList<>();
        cycleways = new ArrayList<>();
        footways = new ArrayList<>();
        highways = new ArrayList<>();
        junctions = new ArrayList<>();
        living_streets = new ArrayList<>();
        motorways = new ArrayList<>();
        parks = new ArrayList<>();
        paths = new ArrayList<>();
        pedestrianWays = new ArrayList<>();
        primaryWays = new ArrayList<>();
        residentialWays = new ArrayList<>();
        roads = new ArrayList<>();
        secondaryWays = new ArrayList<>();
        serviceWays = new ArrayList<>();
        tertiaryWays = new ArrayList<>();
        trackWays = new ArrayList<>();
        trunkWays = new ArrayList<>();
        unclassifiedWays = new ArrayList<>();
        water = new ArrayList<>();
        try {
            OSMParser.readMapElements(filepath, this);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static class Bounds {
        private final float minX, minY, maxX, maxY;

        public Bounds(float minX, float minY, float maxX, float maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
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
    }

    public void createBounds(float minX, float minY, float maxX, float maxY) {
        this.bounds = new Bounds(minX, minY, maxX, maxY);
    }

    /*
    getters, setters and adders
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
    public Bounds getBounds() {
        return this.bounds;
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

    public NodeIndex getNodeIndex() {
        return nodeIndex;
    }
    public void addToNodeIndex(Node node) {
        nodeIndex.addNode(node);
    }

    public WayIndex getWayIndex() { return wayIndex;}
    public void addToWayIndex(Way way) { wayIndex.addWay((way)); }

    public List<Drawable> getDrawables() {
        return drawables;
    }
    public void addWay(Way way) {
        drawables.add(way);
    }

    public ArrayList<Way> getCoastlines() {
        return coastlines;
    }
    public void addCoastline(Way way) {
        coastlines.add(way);
    }

    public ArrayList<Way> getBuildings() {
        return buildings;
    }
    public void addBuilding(Way way) { buildings.add(way); }

    public ArrayList<Way> getCycleways() {
        return cycleways;
    }
    public void addCycleway(Way way) { cycleways.add(way); }

    public ArrayList<Way> getFootways() {
        return footways;
    }
    public void addFootway(Way way) { footways.add(way); }

    public ArrayList<Way> getHighways() {
        return highways;
    }
    public void addHighway(Way way) { highways.add(way); }

    public ArrayList<Way> getJunctions() {
        return junctions;
    }
    public void addJunction(Way way) { junctions.add(way); }

    public ArrayList<Way> getLiving_streets() {
        return living_streets;
    }
    public void addLiving_street(Way way) { living_streets.add(way); }

    public ArrayList<Way> getMotorways() {
        return motorways;
    }
    public void addMotorway(Way way) {
        motorways.add(way);
    }

    public ArrayList<Way> getParks(){
        return parks;
    }
    public void addPark(Way way){
        parks.add(way);
    }

    public ArrayList<Way> getPaths() {
        return paths;
    }
    public void addPath(Way way) { paths.add(way); }

    public ArrayList<Way> getPedestrianWays() {
        return pedestrianWays;
    }
    public void addPedestrianWay(Way way) { pedestrianWays.add(way); }

    public ArrayList<Way> getPrimaryWays(){
        return primaryWays;
    }
    public void addPrimaryWay(Way way){
        primaryWays.add(way);
    }

    public ArrayList<Way> getResidentialWays(){
        return residentialWays;
    }
    public void addResidentialWay(Way way){
        residentialWays.add(way);
    }

    public ArrayList<Way> getRoads() {
        return roads;
    }
    public void addRoad(Way way) { roads.add(way); }

    public ArrayList<Way> getSecondaryWays(){
        return secondaryWays;
    }
    public void addSecondaryWay(Way way){
        secondaryWays.add(way);
    }

    public ArrayList<Way> getServiceWays() {
        return serviceWays;
    }
    public void addServiceWay(Way way) { serviceWays.add(way); }

    public ArrayList<Way> getTertiaryWays(){
        return tertiaryWays;
    }
    public void addTertiaryWay(Way way){
        tertiaryWays.add(way);
    }

    public ArrayList<Way> getTrackWays(){
        return trackWays;
    }
    public void addTrackWay(Way way){
        trackWays.add(way);
    }

    public ArrayList<Way> getTrunkWays(){
        return trunkWays;
    }
    public void addTrunkWay(Way way){
        trunkWays.add(way);
    }

    public ArrayList<Way> getUnclassifiedWays(){
        return unclassifiedWays;
    }
    public void addUnclassifiedWay(Way way){
        unclassifiedWays.add(way);
    }

    public ArrayList<Way> getWater(){
        return water;
    }
    public void addWater(Way way){
        water.add(way);
    }

    public List<Drawable> getIslands() {
        return islands;
    }
    public void setIslands(List<Drawable> islands){
        this.islands = islands;
    }

}
