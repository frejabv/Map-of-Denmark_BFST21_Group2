package bfst21.pathfinding;

import bfst21.Model;
import bfst21.osm.Drawable;
import bfst21.osm.Node;
import bfst21.osm.Tag;
import bfst21.osm.Way;

import java.util.*;

public class AStar {
    Model model;
    boolean isRoundabout;
    double totalDistance = 0;
    double totalTime = 0;
    int exits = 0;
    List<Node> path;
    TransportType type;

    private final ArrayList<Tag> driveable = new ArrayList<>(Arrays.asList(Tag.MOTORWAY_LINK, Tag.LIVING_STREET, Tag.MOTORWAY, Tag.PEDESTRIAN, Tag.PRIMARY, Tag.RESIDENTIAL, Tag.ROAD, Tag.SECONDARY, Tag.SERVICE, Tag.TERTIARY, Tag.TRACK, Tag.TRUNK, Tag.UNCLASSIFIED));
    private final ArrayList<Tag> cyclable = new ArrayList<>(Arrays.asList(Tag.CYCLEWAY, Tag.LIVING_STREET, Tag.PATH, Tag.PEDESTRIAN, Tag.RESIDENTIAL, Tag.ROAD, Tag.SECONDARY, Tag.SERVICE, Tag.TERTIARY, Tag.TRACK, Tag.UNCLASSIFIED));
    private final ArrayList<Tag> walkable = new ArrayList<>(Arrays.asList(Tag.FOOTWAY, Tag.LIVING_STREET, Tag.PATH, Tag.PEDESTRIAN, Tag.RESIDENTIAL, Tag.ROAD, Tag.SERVICE, Tag.TERTIARY, Tag.TRACK, Tag.UNCLASSIFIED));

    public AStar(Model model) {
        this.model = model;
        readData();
    }

    private void readData() {
        for (Map.Entry<Tag, List<Drawable>> entry : model.getDrawableMap().entrySet()) {
            List<Drawable> value = entry.getValue();
            for (Drawable way : value) {
                Way tempWay = (Way) way;
                for (int i = 0; i < tempWay.getNodes().size(); i++) {
                    Node node = tempWay.getNodes().get(i);
                    if (i != (tempWay.getNodes().size() - 1)) {
                        Node nextNode = tempWay.getNodes().get(i + 1);
                        Edge edge = new Edge(nextNode, distanceToNode(node, nextNode), tempWay.getId());
                        edge.setPathTypes(tempWay, this);
                        node.addAdjacencies(edge);
                    }
                    if (i > 0 && !tempWay.isOneway()) {
                        Node previousNode = tempWay.getNodes().get(i - 1);
                        Edge edge = new Edge(previousNode, distanceToNode(node, previousNode), tempWay.getId());
                        edge.setPathTypes(tempWay, this);
                        node.addAdjacencies(edge);
                    }
                }
            }
        }
    }

    public void AStarSearch(Node start, Node end, TransportType type) {
        this.type = type;
        model.setAStarPath(null);
        ArrayList<Node> listOfCheckedNodes = new ArrayList();

        PriorityQueue<Node> pq = new PriorityQueue<>(20, new NodeComparator());

        //cost from start
        start.g_scores = 0;

        pq.add(start);

        boolean found = false;

        while ((!pq.isEmpty()) && (!found)) { //While content in PQ and goal not found
            //the node in having the lowest f_score value
            Node current = pq.poll(); //Gets the first element in the PQ

            current.explored = true; //Adds current to the explored set to remember that it is explored
            listOfCheckedNodes.add(current);

            //Checks if current is goal
            if (current.getId() == end.getId()) {
                found = true;
            }

            //Checks every child of current node
            for (Edge e : current.getAdjacencies()) {
                if (type == TransportType.CAR && e.isDriveable() || type == TransportType.BICYCLE && e.isCyclable() || type == TransportType.WALK && e.isWalkable()) {
                    Node child = e.target;
                    child.setHScores(distanceToNode(child, end) / type.maxSpeed);
                    float cost = e.getWeight(type, model.getWayIndex().getMember(e.getWayID()).getSpeed());
                    float temp_g_scores = current.g_scores + cost;
                    float temp_f_scores = temp_g_scores + child.h_scores;


                    //Checks if child node has been evaluated and the newer f_score is higher, skip
                    if ((child.explored) && (temp_f_scores >= child.f_scores)) {
                        continue;
                    }

                    //else if child node is not in queue (add it) or newer f_score is lower (Update them)
                    else if ((!pq.contains(child)) || (temp_f_scores < child.f_scores)) {
                        child.parent = current;
                        child.g_scores = temp_g_scores;
                        child.f_scores = temp_f_scores;

                        if (pq.contains(child)) {
                            pq.remove(child);
                        }
                        pq.add(child);
                    }
                }
            }
        }

        createPath(end);

        List<Node> debugPath = new ArrayList<>();
        for (Node node : listOfCheckedNodes) {
            debugPath.add(node);
            node.h_scores = 0;
            node.f_scores = 0;
            node.g_scores = 0;
            node.parent = null;
            node.explored = false;
        }
        model.setAStarDebugPath(debugPath);
    }

    public void createPath(Node target) {
        float minX = 100;
        float maxX = -100;
        float minY = 100;
        float maxY = -100;
        path = new ArrayList<>();
        for (Node node = target; node != null; node = node.parent) { //Starts on the target and work back to start
            if (node.getX() < minX) {
                minX = node.getX();
            }
            if (node.getX() > maxX) {
                maxX = node.getX();
            }
            if (node.getY() < minY) {
                minY = node.getY();
            }
            if (node.getY() > maxY) {
                maxY = node.getY();
            }
            path.add(node);
        }
        Collections.reverse(path);
        model.setAStarPath(path);
        model.setAStarBounds(minX, minY, maxX, maxY);
    }

    public ArrayList<Step> getPathDescription() {
        ArrayList<Step> routeDescription = new ArrayList<>();
        double currentDistance = 0;
        int currentMaxSpeed;
        totalTime = 0;
        totalDistance = 0;
        Direction direction = Direction.FOLLOW;

        for (int i = 1; i < path.size() - 1; i++) {
            Node node = path.get(i);
            Node nextNode = path.get(i + 1);
            Node previousNode = path.get(i - 1);
            currentDistance += distanceToNode(previousNode, node);

            long firstId = 0;
            long secondId = 0;

            for (Edge e : previousNode.getAdjacencies()) {
                if (e.target == node && (type == TransportType.CAR && e.isDriveable() || type == TransportType.BICYCLE && e.isCyclable() || type == TransportType.WALK && e.isWalkable())) {
                    firstId = e.getWayID();
                }
            }
            for (Edge e : node.getAdjacencies()) {
                if (e.target == nextNode && (type == TransportType.CAR && e.isDriveable() || type == TransportType.BICYCLE && e.isCyclable() || type == TransportType.WALK && e.isWalkable())) {
                    secondId = e.getWayID();
                }
            }

            String lastRoadName;
            if (model.getWayIndex().getMember(firstId).getName().equals("")) {
                lastRoadName = "unknown road";
            } else {
                lastRoadName = model.getWayIndex().getMember(firstId).getName();
            }

            //count exits in roundabout
            if (isRoundabout && node.getAdjacencies().size() > 1) {
                exits++;
            }

            //the next way is different than the current
            if (firstId != secondId && !lastRoadName.equals(model.getWayIndex().getMember(secondId).getName())) {
                currentMaxSpeed = model.getWayIndex().getMember(firstId).getSpeed();
                if (type.equals(TransportType.WALK)) {
                    currentMaxSpeed = TransportType.WALK.maxSpeed;
                } else if (type.equals(TransportType.BICYCLE)) {
                    currentMaxSpeed = TransportType.BICYCLE.maxSpeed;
                }

                if (!isRoundabout) { //if we're in a roundabout we don't want to give a description
                    Step step = new Step(direction, lastRoadName, currentDistance);
                    if (exits > 0) {
                        step.setExits(exits);
                    }
                    routeDescription.add(step);
                }

                totalDistance += currentDistance;
                totalTime += currentDistance / currentMaxSpeed;
                currentDistance = 0;
                direction = getDirection(node, previousNode, nextNode);
            }

            //we handle the last piece of road
            if (i == path.size() - 2) {
                currentMaxSpeed = model.getWayIndex().getMember(firstId).getSpeed();
                currentDistance += distanceToNode(node, nextNode);
                totalDistance += currentDistance;
                totalTime += currentDistance / currentMaxSpeed;
                String roadName;
                if (model.getWayIndex().getMember(secondId).getName().equals("")) {
                    roadName = "unknown road";
                } else {
                    roadName = model.getWayIndex().getMember(secondId).getName();
                }
                Step step = new Step(direction, roadName, currentDistance);
                if (exits > 0) {
                    step.setExits(exits);
                }
                routeDescription.add(step);
                routeDescription.add(new Step(Direction.ARRIVAL, roadName, 0));
            }
        }

        // we handle very short paths
        if (2 == path.size()) {
            long firstId = 0;
            for (Edge e : path.get(0).getAdjacencies()) {
                if (e.target == path.get(1)) {
                    firstId = e.getWayID();
                }
            }

            currentMaxSpeed = model.getWayIndex().getMember(firstId).getSpeed();
            currentDistance += distanceToNode(path.get(0), path.get(1));
            totalDistance += currentDistance;
            totalTime += currentDistance / currentMaxSpeed;

            String lastRoadName;
            if (model.getWayIndex().getMember(firstId).getName().equals("")) {
                lastRoadName = "unknown road";
            } else {
                lastRoadName = model.getWayIndex().getMember(firstId).getName();
            }

            Step step = new Step(direction, lastRoadName, currentDistance);
            if (exits > 0) {
                step.setExits(exits);
            }
            routeDescription.add(step);
            routeDescription.add(new Step(Direction.ARRIVAL, lastRoadName, 0));
        }

        if (routeDescription.size() == 0) {
            routeDescription.add(new Step(Direction.NO_PATH, "", 0));
        }

        return routeDescription;
    }

    private Direction getDirection(Node currentNode, Node previousNode, Node nextNode) {
        Direction direction = null;
        double theta = Math.atan2(nextNode.getY() - currentNode.getY(), nextNode.getX() - currentNode.getX()) -
                Math.atan2(previousNode.getY() - currentNode.getY(), previousNode.getX() - currentNode.getX());

        double result = Math.toDegrees(theta); //the same as multiplying theta by 180/pi

        //we handle if the result has the wrong sign, in this case plus instead of minus
        if (result > 0) result = 360 - result;
        result = Math.abs(result);

        if (result > 190) {
            direction = Direction.LEFT;
        } else if (result < 165) {
            for (Edge e : currentNode.getAdjacencies()) {
                if (e.target == nextNode) {
                    if (model.getWayIndex().getMember(e.getWayID()).isJunction()) {
                        isRoundabout = true;
                        exits = 0;
                        break;
                    } else if (isRoundabout) {
                        isRoundabout = false;
                        if (exits == 1) {
                            direction = Direction.ROUNDABOUT_FIRST_EXIT;
                        } else if (exits == 2) {
                            direction = Direction.ROUNDABOUT_SECOND_EXIT;
                        } else {
                            direction = Direction.ROUNDABOUT_OTHER_EXIT;
                        }
                        break;
                    } else {
                        direction = Direction.RIGHT;
                        break;
                    }
                }
            }
        } else {
            direction = Direction.CONTINUE;
        }
        return direction;
    }

    private static class NodeComparator implements Comparator<Node> {
        //override compare method
        public int compare(Node i, Node j) {
            if (i.f_scores > j.f_scores) {
                return 1;
            } else if (i.f_scores < j.f_scores) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private float distanceToNode(Node current, Node destination) {
        float distance = 0;
        if (current != destination) {
            float deltaX = Math.abs(destination.getX()) - Math.abs(current.getX());
            float deltaY = Math.abs(destination.getY()) - Math.abs(current.getY());
            distance = (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        }
        return distance * 111.320f * Model.scalingConstant;
    }

    public String getTotalDistance() {
        String result = "Distance: ";
        double distance = Math.round(totalDistance * 10.0) / 10.0;
        if (distance < 1) {
            result += distance * 1000 + " m";
        } else {
            result += distance + " km";
        }
        return result;
    }

    public String getTotalTime() {
        String result = "Estimated Time: ";
        double time = totalTime * 60;

        if (time < 1) {
            result += "1 min";
        } else {
            int timeInMinutes = (int) time;
            int minutes = timeInMinutes % 60;
            int hours = timeInMinutes / 60;

            if (hours >= 1) {
                result += hours + " hours";
            }
            if (hours >= 1 && minutes != 0) {
                result += " and ";
            }
            if (minutes != 0) {
                result += minutes + " min";
            }
        }
        return result;
    }

    public ArrayList<Tag> getDriveableTags() {
        return driveable;
    }

    public ArrayList<Tag> getCyclableTags() {
        return cyclable;
    }

    public ArrayList<Tag> getWalkableTags() {
        return walkable;
    }
}
