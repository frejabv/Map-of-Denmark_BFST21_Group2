package bfst21.pathfinding;

import bfst21.osm.*;

public class Edge{
    public final double weight;
    public final Node target;
    private boolean isDriveable, isCyclable, isWalkable;

    public Edge(Node targetNode, double costVal){
        target = targetNode;
        weight = costVal;
    }

    public void setPathTypes(Way way){

    }

    public boolean isDriveable(){
        return isDriveable;
    }

    public boolean isCyclable(){
        return isCyclable;
    }

    public boolean isWalkable(){
        return isDriveable;
    }
}
