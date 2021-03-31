package bfst21.osm;

import java.util.ArrayList;

public class Node extends Member{
    public double g_scores;
    public double h_scores;
    public double f_scores = 0;
    private ArrayList<Edge> adjacencies;
    public Node parent;
    public boolean explored = false;


    private float x;
    private float y;
    // Scale nodes lattitude to account for the curvature of the earth
    private static float scalingConstant = 0.56f;

    public Node(float x, float y, long id) {
        super(id);
        this.x = x;
        this.y = -y / scalingConstant;
    }
    public Node(long id, double hVal){
        super(id);
        h_scores = hVal;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setHScores(double h_scores){
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
}
