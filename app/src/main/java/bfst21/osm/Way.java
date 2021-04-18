package bfst21.osm;

import java.util.ArrayList;
import java.util.List;

import bfst21.Rtree.Rectangle;
import javafx.scene.canvas.GraphicsContext;

public class Way extends Member implements Drawable {
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
        float minX = 180;
        float minY = 90;
        float maxX = -180;
        float maxY = -90;

        for (Node n: nodes) {
            if (n.getX() < minX)
                minX = n.getX();
            if (n.getX() > maxX)
                maxX = n.getX();
            if (n.getY() < minY)
                minY = n.getY();
            if (n.getY() > maxY)
                maxY = n.getY();
        }

        rect = new Rectangle(minX, minY, maxX, maxY);
    }

    public Rectangle getRect() {
        return rect;
    }
}
