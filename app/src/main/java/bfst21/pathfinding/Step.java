package bfst21.pathfinding;

public class Step {
    Direction direction;
    String roadName;
    float distance;

    public Step() {

    }
    public Step(Direction direction, String roadName, float distance) {
        this.direction = direction;
        this.roadName = roadName;
        this.distance = distance;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

}
