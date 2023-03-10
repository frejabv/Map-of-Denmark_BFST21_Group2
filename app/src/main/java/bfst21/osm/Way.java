package bfst21.osm;

import bfst21.Rtree.Rectangle;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Way extends Member implements Drawable, Serializable {
    private List<Node> nodes;
    private HashMap<Long, String> roleMap;
    private Rectangle rect;
    private String name = "";
    private int maxSpeed = 1;
    private boolean isOneway;
    private boolean isJunction;
    private boolean isCyclable;
    private boolean isWalkable;

    public Way(long id) {
        super(id);
        this.nodes = new ArrayList<>();
    }

    public Way() {
        super(0);
        this.nodes = new ArrayList<>();
    }

    public Node first() {
        return nodes.get(0);
    }

    public Node last() {
        return nodes.get(nodes.size() - 1);
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public static Way merge(Way first, Way second) {
        if (first == null) return second;
        if (second == null) return first;
        Way merged = new Way();
        merged.nodes.addAll(first.nodes);
        merged.nodes.addAll(second.nodes.subList(1, second.nodes.size()));
        return merged;
    }

    public HashMap<Long, String> getRoleMap() {
        return roleMap;
    }

    public void addRole(long id, String role) {
        if (roleMap == null) {
            roleMap = new HashMap<>();
        }
        roleMap.put(id, role);
    }

    public static Way merge(Way first, Way second, Way third) {
        return merge(merge(first, second), third);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void checkSpeed() {
        if (maxSpeed == 1) {
            if (tag == Tag.MOTORWAY || tag == Tag.MOTORWAY_LINK) {
                maxSpeed = 130;
            } else if (tag == Tag.SECONDARY || tag == Tag.TERTIARY || tag == Tag.TRUNK || tag == Tag.PRIMARY) {
                maxSpeed = 80;
            } else if (tag == Tag.LIVING_STREET || tag == Tag.UNCLASSIFIED || tag == Tag.RESIDENTIAL || tag == Tag.ROAD || tag == Tag.SERVICE) {
                maxSpeed = 50;
            }
        }
    }

    public void setIsOneway() {
        this.isOneway = true;
    }

    public boolean isOneway() {
        return isOneway;
    }

    public void setIsJunction() {
        this.isJunction = true;
        setIsOneway();
    }

    public boolean isJunction() {
        return isJunction;
    }

    public void setIsCyclable(boolean isCyclable) {
        this.isCyclable = isCyclable;
    }

    public boolean isCyclable() {
        return isCyclable;
    }

    public void setIsWalkable(boolean isWalkable) {
        this.isWalkable = isWalkable;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    @Override
    public void draw(GraphicsContext gc, RenderingStyle renderingStyle) {
        gc.beginPath();
        var firstNode = nodes.get(0);
        gc.moveTo(firstNode.getX(), firstNode.getY());

        for (int i = 1; i < nodes.size(); i++) {
            gc.lineTo(nodes.get(i).getX(), nodes.get(i).getY());
        }
        gc.stroke();
    }

    public void drawRelationPart(GraphicsContext gc) {
        var firstNode = nodes.get(0);
        gc.moveTo(firstNode.getX(), firstNode.getY());

        for (var node : nodes) {
            gc.lineTo(node.getX(), node.getY());
        }
    }

    public void createRectangle() {
        float minX = 1800, minY = 1800, maxX = -1800, maxY = -1800;

        for (Node n : nodes) {
            if (n.getX() < minX) {
                minX = n.getX();
            }
            if (n.getX() > maxX) {
                maxX = n.getX();
            }
            if (n.getY() < minY) {
                minY = n.getY();
            }
            if (n.getY() > maxY) {
                maxY = n.getY();
            }
        }

        rect = new Rectangle(minX, minY, maxX, maxY);
    }

    public Rectangle getRect() {
        return rect;
    }

    public double minimumDistanceToSquared(Point2D p) {
        double smallestDistance = Double.POSITIVE_INFINITY;
        for (int i = 1; i < nodes.size(); i++) {
            double currentSegmentDist = minimumDistanceToSegment(nodes.get(i - 1), nodes.get(i), p);
            if (currentSegmentDist < smallestDistance) {
                smallestDistance = currentSegmentDist;
            }
        }
        return smallestDistance;
    }

    private Double minimumDistanceToSegment(Node n1, Node n2, Point2D p) {
        float pointXDiff = (float) (p.getX() - n1.getX());
        float pointYDiff = (float) (p.getY() - n1.getY());
        float nDeltaX = n2.getX() - n1.getX();
        float nDeltaY = n2.getY() - n1.getY();

        float dot = pointXDiff * nDeltaX + pointYDiff * nDeltaY;
        float lengthSq = nDeltaX * nDeltaX + nDeltaY * nDeltaY;
        /*
        param describes where the point is, relative to the line.
        if param < 0 || param > 1 it is closest to one of the end nodes.
        if 0 < param < 1, then the closest point is on the line segment.
        */
        double param = -1;
        if (lengthSq != 0) //in case of 0 length
            param = dot / lengthSq;

        float nearestX;
        float nearestY;

        if (param < 0) {
            nearestX = n1.getX();
            nearestY = n1.getY();
        } else if (param > 1) {
            nearestX = n2.getX();
            nearestY = n2.getY();
        } else {
            nearestX = (float) (n1.getX() + param * nDeltaX);
            nearestY = (float) (n1.getY() + param * nDeltaY);
        }

        var deltaX = p.getX() - nearestX;
        var deltaY = p.getY() - nearestY;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public Node nearestNode(Point2D p) {
        Node closest = nodes.get(0);
        double closestDist = closest.distanceToSquared(p);

        for (int i = 1; i < nodes.size(); i++) {
            double currentDist = nodes.get(i).distanceToSquared(p);
            if (currentDist < closestDist) {
                closest = nodes.get(i);
                closestDist = currentDist;
            }
        }

        return closest;
    }
}
