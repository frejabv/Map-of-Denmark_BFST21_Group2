package bfst21.osm;

import bfst21.Model;
import bfst21.pathfinding.Edge;
import javafx.geometry.Point2D;

import java.io.Serializable;
import java.util.ArrayList;

public class Node extends Member implements Serializable {
    private float x;
    private float y;
    private ArrayList<Edge> adjacencies;

    public Node(float x, float y, long id) {
        super(id);
        this.x = x;
        this.y = y / -Model.scalingConstant;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void addAdjacencies(Edge edge) {
        if (adjacencies == null) {
            adjacencies = new ArrayList<>();
        }
        adjacencies.add(edge);
    }

    public double distanceToSquared(Point2D p) {
        return (x - p.getX()) * (x - p.getX()) + (y - p.getY()) * (y - p.getY());
    }
}
