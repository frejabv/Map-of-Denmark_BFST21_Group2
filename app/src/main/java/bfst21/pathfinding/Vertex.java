package bfst21.pathfinding;

import bfst21.Model;
import bfst21.osm.Node;

import java.util.ArrayList;

public class Vertex extends Node{
    public float g_scores;
    public float h_scores;
    public float f_scores = 0;
    private ArrayList<Edge> adjacencies;
    public Vertex parent;
    public boolean explored = false;

    public Vertex(float x, float y, long id) {
        super(x,y*-Model.scalingConstant,id);
    }

    public void setHScores(float h_scores) {
        this.h_scores = h_scores;
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
}
