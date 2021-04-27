package bfst21.osm;

import bfst21.pathfinding.Edge;

import java.util.ArrayList;




import java.io.Serializable;
import bfst21.Model;
import javafx.geometry.Point2D;

public class Node extends Member implements Serializable {
    private float x;
    private float y;
    public float g_scores;
    public float h_scores;
    public float f_scores = 0;
    private ArrayList<Edge> adjacencies;
    public Node parent;
    public boolean explored = false;

    public Node(float x, float y, long id) {
        super(id);
        this.x = x;
        this.y = y / -Model.scalingConstant;
    }
    public Node(long id, float hVal){
        super(id);
        h_scores = hVal;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setHScores(float h_scores){
        this.h_scores = h_scores;
    }

    public void addAdjecencies(Edge edge){
        if(adjacencies == null){
            adjacencies = new ArrayList<>();
        }
        adjacencies.add(edge);
    }

    public ArrayList<Edge> getAdjecencies(){
        return adjacencies;
    }

    public double distanceToSquared(Point2D p) {
        return (x - p.getX()) * (x - p.getX()) + (y - p.getY()) * (y - p.getY());
    }
}
