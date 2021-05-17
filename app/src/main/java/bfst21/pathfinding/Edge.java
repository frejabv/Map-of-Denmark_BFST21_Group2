package bfst21.pathfinding;

import bfst21.osm.Tag;
import bfst21.osm.Way;

public class Edge {
    private final float weight;
    private final Vertex target;
    private boolean isDriveable, isCyclable, isWalkable;
    private final long wayID;

    public Edge(Vertex targetNode, float weight, long wayID) {
        this.target = targetNode;
        this.weight = weight;
        this.wayID = wayID;
    }

    public void setPathTypes(Way way, AStar astar) {
        Tag tag = way.getTag();
        if (astar.getDriveableTags().contains(tag)) {
            isDriveable = true;
        }
        if (astar.getCyclableTags().contains(tag)) {
            isCyclable = true;
        }
        if (astar.getWalkableTags().contains(tag)) {
            isWalkable = true;
        }
        if (way.isCyclable()) {
            isCyclable = true;
        }
        if (way.isWalkable()) {
            isWalkable = true;
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

    public float getWeight() {
        return weight;
    }

    public Vertex getTarget() {
        return target;
    }
}
