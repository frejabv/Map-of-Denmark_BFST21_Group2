package bfst21.osm;

import bfst21.osm.*;

public class Edge{
    public final double weight;
    public final Node target;

    public Edge(Node targetNode, double costVal){
        target = targetNode;
        weight = costVal;
    }
}
