package bfst21.osm;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class Way implements Drawable {
    private List<Node> nodes;
    private long id;

    public Way(long id) {
        this.nodes = new ArrayList<>();
        this.id = id;
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

    public long getId() {
        return id;
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
}
