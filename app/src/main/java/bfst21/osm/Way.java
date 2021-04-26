package bfst21.osm;

import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Way extends Member implements Drawable, Serializable {
    private List<Node> nodes;
    String name = "";
    int maxSpeed = -1;
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
        if (maxSpeed == -1) {
            //Maybe check if way has areatype declared????? like urban
            if (tags.contains(Tag.MOTORWAY)) {
                maxSpeed = 130;
            } else if (tags.contains(Tag.SECONDARY) || tags.contains(Tag.TERTIARY) || tags.contains(Tag.TRUNK) || tags.contains(Tag.UNCLASSIFIED) || tags.contains(Tag.PRIMARY)) {
                maxSpeed = 80;
            } else if (tags.contains(Tag.JUNCTION) || tags.contains(Tag.LIVING_STREET) || tags.contains(Tag.RESIDENTIAL) || tags.contains(Tag.ROAD) || tags.contains(Tag.SERVICE)) {
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
}
