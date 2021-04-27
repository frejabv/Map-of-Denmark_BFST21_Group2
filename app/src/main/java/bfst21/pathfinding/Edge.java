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

    public void setPathTypes(Way way, Model model) {
        for (Tag tag : way.getTags()) {
            if (model.getDriveableTags().contains(tag)) {
                isDriveable = true;
            }
            if (model.getCyclableTags().contains(tag)) {
                isCyclable = true;
            }
            if (model.getWalkableTags().contains(tag)) {
                isWalkable = true;
            }
        }
    }

    public float getWeight(TransportType type, Model model) {
        if (type == TransportType.BICYCLE) {
            return weight / 15;
        } else if (type == TransportType.WALK) {
            return weight / 5;
        } else {
            return weight / model.getWayIndex().getMember(wayID).getSpeed();
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
