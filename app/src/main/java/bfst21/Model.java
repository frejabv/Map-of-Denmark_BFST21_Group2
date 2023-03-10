package bfst21;

import bfst21.POI.POI;
import bfst21.POI.POI_KDTree;
import bfst21.Rtree.Rtree;
import bfst21.osm.*;
import bfst21.pathfinding.AStar;
import bfst21.pathfinding.TransportType;
import bfst21.pathfinding.Vertex;
import bfst21.search.RadixTree;
import javafx.scene.image.Image;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Model {
    // Scale nodes latitude to account for the curvature of the earth
    public final static float scalingConstant = 0.56f;
    private Map<Tag, List<Drawable>> drawableMap;
    private Map<Tag, List<Drawable>> fillMap;

    private final ArrayList<Tag> drawableTagList;
    private final ArrayList<Tag> fillableTagList;

    private POI_KDTree POITree;
    private MemberIndex<Node> nodeIndex;
    private MemberIndex<Way> wayIndex;
    private MemberIndex<Relation> relationIndex;
    private RadixTree streetTree;
    private List<Drawable> islands = new ArrayList<>();
    private ArrayList<Way> coastlines;
    HashMap<String, Image> imageSet;
    HashMap<Node, Vertex> vertexMap = new HashMap<>();
    ArrayList<Vertex> vertexIndex;

    private final ArrayList<POI> pointsOfInterest;
    private final ArrayList<POI> systemPointsOfInterest;

    private final ArrayList<Drawable> fillables400, fillables150, fillables7;
    private final ArrayList<Drawable> drawables700, drawables400, drawables150, drawables7, drawables3;
    private final Rtree fillableRTree400, fillableRTree150, fillableRTree7;
    private final Rtree drawableRTree700, drawableRTree400, drawableRTree150, drawableRTree7, drawableRTree3;

    private final ArrayList<Drawable> roadlist;
    private final Rtree roadTree;
    private Node nearestNode;

    private List<Drawable> areaNames;
    private final Rtree areaTree;

    private final boolean ttiMode;

    private AStar aStar;
    private List<Vertex> AStarPath;
    private List<Vertex> AStarDebugPath;
    private TransportType defaultTransportType = TransportType.CAR;
    private TransportType currentTransportType = defaultTransportType;
    private float minX, minY, maxX, maxY;

    public Model(String filePath, boolean ttiMode) throws IOException, XMLStreamException, ClassNotFoundException {
        // Java wouldn't let me expand this into variables. Im very sorry about the mess
        this(Model.class.getResourceAsStream(filePath), OSMParser.genFileExtension(filePath), filePath, ttiMode);
    }

    public Model(InputStream in, FileExtension fileExtension, String fileName, boolean ttiMode) throws ClassNotFoundException, IOException, XMLStreamException {
        drawableMap = new HashMap<>();
        fillMap = new HashMap<>();

        POITree = new POI_KDTree();

        nodeIndex = new MemberIndex<>();
        coastlines = new ArrayList<>();
        wayIndex = new MemberIndex<>();
        relationIndex = new MemberIndex<>();
        streetTree = new RadixTree();
        areaNames = new ArrayList<>();
        drawableTagList = new ArrayList<>();
        fillableTagList = new ArrayList<>();

        pointsOfInterest = new ArrayList<>();
        systemPointsOfInterest = new ArrayList<>();

        this.ttiMode = ttiMode;

        String[] fileNameParts = fileName.split("/");

        OSMParser.readMapElements(in, fileExtension, fileNameParts[fileNameParts.length - 1], this);


        drawableMap.forEach((tag, drawables) -> drawableTagList.add(tag));
        fillMap.forEach((tag, drawables) -> fillableTagList.add(tag));

        roadlist = new ArrayList<>();
        drawables700 = new ArrayList<>();
        drawables400 = new ArrayList<>();
        drawables150 = new ArrayList<>();
        drawables7 = new ArrayList<>();
        drawables3 = new ArrayList<>();

        fillables400 = new ArrayList<>();
        fillables150 = new ArrayList<>();
        fillables7 = new ArrayList<>();


        for (Tag tag : fillMap.keySet()) {
            addDrawableToRTreeList("map", fillMap.get(tag), tag.zoomLimit);
        }

        for (Tag tag : drawableMap.keySet()) {
            addDrawableToRTreeList("map", drawableMap.get(tag), tag.zoomLimit);
        }

        addDrawableToRTreeList("list", relationIndex.getDrawableMembers(), 0);

        roadTree = new Rtree(roadlist);
        drawableRTree700 = new Rtree(drawables700);
        drawableRTree400 = new Rtree(drawables400);
        drawableRTree150 = new Rtree(drawables150);
        drawableRTree7 = new Rtree(drawables7);
        drawableRTree3 = new Rtree(drawables3);

        fillableRTree400 = new Rtree(fillables400);
        fillableRTree150 = new Rtree(fillables150);
        fillableRTree7 = new Rtree(fillables7);

        areaTree = new Rtree(areaNames);
    }

    public void initImageSet() {
        imageSet = new HashMap<>();
        imageSet.put("mill", new Image("bfst21/icons/" + "mill" + ".png"));
        imageSet.put("museum", new Image("bfst21/icons/" + "museum" + ".png"));
        imageSet.put("theme_park", new Image("bfst21/icons/" + "theme_park" + ".png"));
        imageSet.put("aerodrome", new Image("bfst21/icons/" + "aerodrome" + ".png"));
        imageSet.put("cinema", new Image("bfst21/icons/" + "cinema" + ".png"));
        imageSet.put("castle", new Image("bfst21/icons/" + "castle" + ".png"));
        imageSet.put("statue", new Image("bfst21/icons/" + "statue" + ".png"));
        imageSet.put("zoo", new Image("bfst21/icons/" + "zoo" + ".png"));
        imageSet.put("suitcase", new Image("bfst21/icons/" + "suitcase" + ".png"));
        imageSet.put("viewpoint", new Image("bfst21/icons/" + "viewpoint" + ".png"));
        imageSet.put("default", new Image("bfst21/icons/" + "default" + ".png"));
        imageSet.put("heart", new Image("bfst21/icons/" + "heart" + ".png"));
    }

    /*
     * getters, setters and adders
     */
    public float getMinX() {
        return minX;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public float getMinY() {
        return minY;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public float getMaxX() {
        return maxX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public POI_KDTree getPOITree() {
        return POITree;
    }

    public void setPOITree(POI_KDTree POITree) {
        this.POITree = POITree;
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

    public List<Vertex> getAStarPath() {
        return AStarPath;
    }

    public void setAStarPath(List<Vertex> AStarPath) {
        this.AStarPath = AStarPath;
    }

    public List<Vertex> getAStarDebugPath() {
        return AStarDebugPath;
    }

    public void setAStarDebugPath(List<Vertex> AStarDebugPath) {
        this.AStarDebugPath = AStarDebugPath;
    }

    public TransportType getCurrentTransportType() {
        return currentTransportType;
    }

    public void setCurrentTransportType(TransportType type) {
        this.currentTransportType = type;
    }

    public TransportType getDefaultTransportType() {
        return defaultTransportType;
    }

    public void setDefaultTransportType(TransportType type) {
        this.defaultTransportType = type;
        setCurrentTransportType(type);
    }

    public void addPOI(POI poi) {
        pointsOfInterest.add(poi);
        POITree.insert(poi);
    }

    public void removePOI(POI poi) {
        pointsOfInterest.remove(poi);
        POITree.remove(poi);
    }

    public ArrayList<POI> getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void addToAreaNamesIndex(AreaName areaName) {
        areaNames.add(areaName);
    }

    public List<Drawable> getAreaNames() {
        return areaNames;
    }

    public void setAreaNames(List<Drawable> areaNames) {
        this.areaNames = areaNames;
    }

    public Rtree getFillableRTree400() {
        return fillableRTree400;
    }

    public Rtree getFillableRTree150() {
        return fillableRTree150;
    }

    public Rtree getFillableRTree7() {
        return fillableRTree7;
    }

    public Rtree getDrawableRTree700() {
        return drawableRTree700;
    }

    public Rtree getDrawableRTree400() {
        return drawableRTree400;
    }

    public Rtree getDrawableRTree150() {
        return drawableRTree150;
    }

    public Rtree getDrawableRTree7() {
        return drawableRTree7;
    }

    public Rtree getDrawableRTree3() {
        return drawableRTree3;
    }

    public Node getNearestNode() {
        return nearestNode;
    }

    public void setNearestNode(Node nearestNode) {
        this.nearestNode = nearestNode;
    }

    public Rtree getAreaTree() {
        return areaTree;
    }

    public Rtree getRoadRTree() {
        return roadTree;
    }

    public HashMap<Node, Vertex> getVertexMap() {
        return vertexMap;
    }

    public void setVertexMap(HashMap<Node, Vertex> vertexMap) {
        this.vertexMap = vertexMap;
    }

    public void nullifyVertexMap() {
        vertexMap = null;
    }

    public void setVertexIndex(ArrayList<Vertex> vertexIndex) {
        this.vertexIndex = vertexIndex;
    }

    public ArrayList<Vertex> getVertexIndex() {
        return vertexIndex;
    }

    public void addSystemPOI(POI poi) {
        systemPointsOfInterest.add(poi);
    }

    public ArrayList<POI> getSystemPointsOfInterest() {
        return systemPointsOfInterest;
    }

    private final ArrayList<Tag> driveable = new ArrayList<>(Arrays.asList(
            Tag.MOTORWAY_LINK, Tag.LIVING_STREET, Tag.MOTORWAY, Tag.PEDESTRIAN,
            Tag.PRIMARY, Tag.RESIDENTIAL, Tag.ROAD, Tag.SECONDARY, Tag.SERVICE,
            Tag.TERTIARY, Tag.TRUNK, Tag.TRACK, Tag.UNCLASSIFIED));

    public void addDrawableToRTreeList(String type, List<Drawable> drawableList, int zoomLimit) {
        if (type.equals("map")) {
            //if not fillable, it is drawable
            boolean fillable = fillableTagList.contains(drawableList.get(0).getTag());
            switch (zoomLimit) {
                case 700:
                    drawables700.addAll(drawableList);
                    break;
                case 400:
                    if (fillable) {
                        fillables400.addAll(drawableList);
                    } else {
                        drawables400.addAll(drawableList);
                    }
                    break;
                case 150:
                    if (fillable) {
                        fillables150.addAll(drawableList);
                    } else {
                        drawables150.addAll(drawableList);
                    }
                    break;
                case 7:
                    if (fillable) {
                        fillables7.addAll(drawableList);
                    } else {
                        drawables7.addAll(drawableList);
                    }
                    break;
                case 3:
                    drawables3.addAll(drawableList);
                    break;
            }
            if (driveable.contains(drawableList.get(0).getTag())) {
                roadlist.addAll(drawableList);
            }
        } else {
            for (Drawable d : drawableList) {
                boolean fillable = fillableTagList.contains(d.getTag());
                if (d.getTag() != null) {
                    switch (d.getTag().zoomLimit) {
                        case 700:
                            drawables700.add(d);
                            break;
                        case 400:
                            if (fillable) {
                                fillables400.add(d);
                            } else {
                                drawables400.add(d);
                            }
                            break;
                        case 150:
                            if (fillable) {
                                fillables150.add(d);
                            } else {
                                drawables150.add(d);
                            }
                            break;
                        case 7:
                            if (fillable) {
                                fillables7.add(d);
                            } else {
                                drawables7.add(d);
                            }
                            break;
                        case 3:
                            drawables3.add(d);
                            break;
                    }
                }
                if (driveable.contains(d.getTag())) {
                    roadlist.add(d);
                }
            }
        }
    }
}
