package bfst21.pathfinding;

import bfst21.Model;
import bfst21.osm.*;

public class Edge{
    public final double weight;
    public final Node target;
    private boolean isDriveable, isCyclable, isWalkable;
    private long wayID;

    public Edge(Node targetNode, double costVal, long wayID){
        target = targetNode;
        weight = costVal;
        this.wayID = wayID;
    }

    public void setPathTypes(Way way, Model model){
        for(Tag tag : way.getTags()) {
            if(model.getDriveableTags().contains(tag)) {
                isDriveable = true;
            }
            if(model.getCyclableTags().contains(tag)) {
                isCyclable = true;
            }
            if(model.getWalkableTags().contains(tag)) {
                isWalkable = true;
            }
        }
    }

    public boolean isDriveable(){
        return isDriveable;
    }

    public boolean isCyclable(){
        return isCyclable;
    }

    public boolean isWalkable(){
        return isWalkable;
    }

    public long getWayID(){
        return wayID;
    }
}
