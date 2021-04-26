package bfst21.osm;

import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bfst21.Rtree.Rectangle;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import org.jetbrains.annotations.NotNull;
import javafx.scene.paint.Color;

public class Way extends Member implements Drawable, Serializable {
    private List<Node> nodes;
    private Rectangle rect;
    String name = "";
    int maxSpeed = 1;
    boolean isOneway;
    boolean isJunction;

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
            //Maybe check if way has areatype declared????? like urban
            if (tags.contains(Tag.MOTORWAY) || tags.contains(Tag.MOTORWAY_LINK)) {
                maxSpeed = 130;
            } else if (tags.contains(Tag.SECONDARY) || tags.contains(Tag.TERTIARY) || tags.contains(Tag.TRUNK) || tags.contains(Tag.PRIMARY)) {
                maxSpeed = 80;
            } else if (tags.contains(Tag.JUNCTION) || tags.contains(Tag.LIVING_STREET) || tags.contains(Tag.UNCLASSIFIED) || tags.contains(Tag.RESIDENTIAL) || tags.contains(Tag.ROAD) || tags.contains(Tag.SERVICE)) {
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

    public boolean isJunction() {
        return isJunction;
    }

    public void setIsJunction() {
        this.isJunction = true;
        setIsOneway();
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.beginPath();
        var firstNode = nodes.get(0);
        gc.moveTo(firstNode.getX(), firstNode.getY());

        for (var node : nodes) {
            gc.lineTo(node.getX(), node.getY());
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
        float minX = 181;
        float minY = 181;
        float maxX = -181;
        float maxY = -181;

        for (Node n: nodes) {
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
            double currentSegmentDist = minimumDistanceToSegment(nodes.get(i-1), nodes.get(i), p);
            if (currentSegmentDist < smallestDistance) {
                smallestDistance = currentSegmentDist;
            }
        }
        return smallestDistance;
    }

    private Double minimumDistanceToSegment(Node n1, Node n2, Point2D p) {
        float A = (float) (p.getX() - n1.getX());
        float B = (float) (p.getY() - n1.getY());
        float C = n2.getX() - n1.getX();
        float D = n2.getY() - n1.getY();

        float dot = A * C + B * D;
        float len_sq = C * C + D * D;
        double param = -1;
        if (len_sq != 0) //in case of 0 length
            param = dot / len_sq;

        float xx;
        float yy;

        if (param < 0) {
            xx = n1.getX();
            yy = n1.getY();
        }
        else if (param > 1) {
            xx = n2.getX();
            yy = n2.getY();
        }
        else {
            xx = (float) (n1.getX() + param * C);
            yy = (float) (n1.getY() + param * D);
        }

        var dx = p.getX() - xx;
        var dy = p.getY() - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public Node nearestNode(Point2D p) {
        Node closest = nodes.get(0);
        double closestDist = closest.distanceToSquared(p);

        for (int i = 1; i < nodes.size(); i++) {
            double currentDist = nodes.get(i).distanceToSquared(p);
            if ( currentDist < closestDist) {
                closest = nodes.get(i);
                closestDist = currentDist;
            }
        }

        return closest;
    }
}
