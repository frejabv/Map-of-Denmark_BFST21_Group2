package bfst21.osm;

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

    public Way(long id) {
        super(id);
        this.nodes = new ArrayList<>();
    }

    public Way() {
        super(0);
        this.nodes = new ArrayList<>();
    }

    public Node first(){
        return nodes.get(0);
    }
    public Node last(){
        return nodes.get(nodes.size() - 1);
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public static Way merge(Way first, Way second) {
        if(first == null) return second;
        if(second == null) return first;
        Way merged = new Way();
        merged.nodes.addAll(first.nodes);
        merged.nodes.addAll(second.nodes.subList(1,second.nodes.size()));
        return merged;
    }

    public static Way merge(Way first, Way second, Way third) {
        return merge(merge(first,second),third);
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
        float minX = 181, minY = 91, maxX = -181, maxY = -91;

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
        float pointXDiff = (float) (p.getX() - n1.getX());
        float pointYDiff = (float) (p.getY() - n1.getY());
        float nDeltaX = n2.getX() - n1.getX();
        float nDeltaY = n2.getY() - n1.getY();

        float dot = pointXDiff * nDeltaX + pointYDiff * nDeltaY;
        float lengthSq = nDeltaX * nDeltaX + nDeltaY * nDeltaY;
        double param = -1;
        if (lengthSq != 0) //in case of 0 length
            param = dot / lengthSq;

        float nearestX;
        float nearestY;

        if (param < 0) {
            nearestX = n1.getX();
            nearestY = n1.getY();
        }
        else if (param > 1) {
            nearestX = n2.getX();
            nearestY = n2.getY();
        }
        else {
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
            if ( currentDist < closestDist) {
                closest = nodes.get(i);
                closestDist = currentDist;
            }
        }

        return closest;
    }
}
