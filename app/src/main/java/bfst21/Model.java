package bfst21;

import bfst21.Rtree.Rtree;
import bfst21.osm.*;
import bfst21.search.RadixTree;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Rtree rtree;

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

        List<Drawable> testList= new ArrayList<>();
        testList.addAll(drawableMap.get(Tag.CYCLEWAY));
        testList.addAll(drawableMap.get(Tag.FOOTWAY));
        testList.addAll(drawableMap.get(Tag.LIVING_STREET));
        testList.addAll(drawableMap.get(Tag.PATH));
        testList.addAll(drawableMap.get(Tag.RESIDENTIAL));
        testList.addAll(drawableMap.get(Tag.TERTIARY));
        testList.addAll(drawableMap.get(Tag.SERVICE));
        testList.addAll(drawableMap.get(Tag.UNCLASSIFIED));
        testList.addAll(drawableMap.get(Tag.TRACK));

        rtree = new Rtree(this, testList);
        System.out.println("here");
    }


    public Rtree getRtree() {
        return rtree;
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
}
