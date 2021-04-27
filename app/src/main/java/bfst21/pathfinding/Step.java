package bfst21.pathfinding;

public class Step {
    Direction direction;
    String roadName;
    double distance;
    int exits;

    public Step(Direction direction, String roadName, double distance) {
        this.direction = direction;
        this.roadName = roadName;
        this.distance = distance;
    }

    public void setExits(int exits) {
        this.exits = exits;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String toString() {
        String result = "";
        if (direction.equals(Direction.RIGHT)) {
            result = "Turn right and follow ";
        } else if (direction.equals(Direction.LEFT)) {
            result = "Turn left and follow ";
        } else if (direction.equals(Direction.CONTINUE)) {
            result = "Continue ahead on ";
        } else if (direction.equals(Direction.ROUNDABOUT_OTHER_EXIT)) {
            String ending;
            if (exits == 3) {
                ending = exits + "rd";
            } else {
                ending = exits + "th";
            }
            result = "Take the " + ending + " exit in the roundabout and follow ";
        } else if (direction.equals(Direction.ROUNDABOUT_SECOND_EXIT)) {
            result = "Take the 2nd exit in the roundabout and follow ";
        } else if (direction.equals(Direction.ROUNDABOUT_FIRST_EXIT)) {
            result = "Take the 1st exit in the roundabout and follow ";
        } else if (direction.equals(Direction.FOLLOW)) {
            result = "Follow ";
        } else if (direction.equals(Direction.ARRIVAL)) {
            return "Arrived at " + roadName;
        }
        result += roadName + " for " + getMetric();

        if(direction.equals(Direction.NO_PATH)){
            result = "No path was found";
        }
        return result;
    }

    private String getMetric() {
        String metric = "km";
        if (distance < 1) {
            distance = distance * 1000;
            metric = "m";
        }
        return Math.round(distance) + metric;
    }

}
