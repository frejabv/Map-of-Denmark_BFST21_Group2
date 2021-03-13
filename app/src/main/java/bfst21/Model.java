package bfst21;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bfst21.osm.*;

public class Model {
    private NodeIndex nodeIndex;

    private Map<Tag, List<Drawable>> drawableMap;
    //

    // drawables are all ways that not in any other list
    private List<Drawable> drawables;
    private ArrayList<Way> coastlines;
    private List<Drawable> islands = new ArrayList<>();
    private Bounds bounds;

    private float minX, minY, maxX, maxY;

    public Model(String filepath) {
        drawableMap = new HashMap<>();

        drawables = new ArrayList<>();
        coastlines = new ArrayList<>();
        nodeIndex = new NodeIndex();
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

    public List<Drawable> getIslands() {
        return islands;
    }

    public void setIslands(List<Drawable> islands) {
        this.islands = islands;
    }

    public Map<Tag, List<Drawable>> getDrawableMap() {
        return drawableMap;
    }

}
