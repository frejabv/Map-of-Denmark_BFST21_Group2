package bfst21;

import java.util.ArrayList;

public class Vertex {
    public final String value;
    public double g_scores;
    public final double h_scores;
    public double f_scores = 0;
    public ArrayList<Edge> adjacencies;
    public Vertex parent;
    public boolean explored = false;

    public Vertex(String val, double hVal){
        value = val;
        h_scores = hVal;
    }

    public String toString(){
        return value;
    }

}
