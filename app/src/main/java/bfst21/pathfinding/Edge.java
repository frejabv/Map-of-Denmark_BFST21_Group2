package bfst21.pathfinding;

import bfst21.Model;
import bfst21.osm.Node;
import bfst21.osm.Tag;
import bfst21.osm.Way;

public class Edge {
    public final float weight;
    public final Node target;
    private boolean isDriveable, isCyclable, isWalkable;
    private final long wayID;

    public Edge(Node targetNode, float costVal, long wayID) {
        target = targetNode;
        weight = costVal;
        this.wayID = wayID;
    }

    public void setPathTypes(Way way, AStar astar) {
        for (Tag tag : way.getTags()) {
            if (astar.getDriveableTags().contains(tag)) {
                isDriveable = true;
            }
            if (astar.getCyclableTags().contains(tag)) {
                isCyclable = true;
            }
            if (astar.getWalkableTags().contains(tag)) {
                isWalkable = true;
            }
        }
    }

    public float getWeight(TransportType type, int speed) {
        if (type == TransportType.BICYCLE) {
            return weight / 15;
        } else if (type == TransportType.WALK) {
            return weight / 5;
        } else {
            return weight / speed;
        }
    }

    public boolean isDriveable() {
        return isDriveable;
    }

    public boolean isCyclable() {
        return isCyclable;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    public long getWayID() {
        return wayID;
    }
}
