package bfst21.pathfinding;

import bfst21.Model;
import bfst21.osm.Node;

import java.util.ArrayList;

public class Vertex extends Node {
    private float g_scores;
    private float h_scores;
    private float f_scores = 0;
    private ArrayList<Edge> adjacencies;
    private Vertex parent;
    private boolean explored = false;

    public Vertex(float x, float y, long id) {
        super(x, y * -Model.scalingConstant, id);
    }

    public void addAdjacencies(Edge edge) {
        if (adjacencies == null) {
            adjacencies = new ArrayList<>();
        }
        adjacencies.add(edge);
    }

    public ArrayList<Edge> getAdjacencies() {
        return adjacencies;
    }

    public void setG_scores(float g_scores) {
        this.g_scores = g_scores;
    }

    public float getG_scores() {
        return g_scores;
    }

    public void setH_scores(float h_scores) {
        this.h_scores = h_scores;
    }

    public float getH_scores() {
        return h_scores;
    }

    public void setF_scores(float f_scores) {
        this.f_scores = f_scores;
    }

    public float getF_scores() {
        return f_scores;
    }

    public void setParent(Vertex parent) {
        this.parent = parent;
    }

    public Vertex getParent() {
        return parent;
    }

    public void setExplored(boolean explored) {
        this.explored = explored;
    }

    public boolean isExplored() {
        return explored;
    }
}
