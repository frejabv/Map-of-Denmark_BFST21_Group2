package bfst21.pathfinding;

import bfst21.Model;
import bfst21.osm.Drawable;
import bfst21.osm.Node;
import bfst21.osm.Tag;
import bfst21.osm.Way;

import java.util.*;

public class AStar {
    Model model;
    List<Node> initialisedNodes;
    boolean isRoundabout;
    double totalDistance = 0;
    double totalTime = 0;
    int exits = 0;
    List<Node> path;
    TransportType type;

    public AStar(Model model) {
        this.model = model;
        readData();
    }

    private void readData() {
        initialisedNodes = new ArrayList<>();

        for (Map.Entry<Tag, List<Drawable>> entry : model.getDrawableMap().entrySet()) {
            List<Drawable> value = entry.getValue();
            for (Drawable way : value) {
                Way wayButNowCasted = (Way) way;
                for (int i = 0; i < wayButNowCasted.getNodes().size(); i++) {
                    Node node = wayButNowCasted.getNodes().get(i);
                    if (i != (wayButNowCasted.getNodes().size() - 1)) {
                        Node nextNode = wayButNowCasted.getNodes().get(i + 1);
                        Edge edge = new Edge(nextNode, distanceToNode(node, nextNode), wayButNowCasted.getId()); ///wayButNowCasted.getSpeed()
                        edge.setPathTypes(wayButNowCasted, model);
                        node.addAdjecencies(edge);
                    }
                    if (i > 0 && !wayButNowCasted.isOneway()) {
                        Node previousNode = wayButNowCasted.getNodes().get(i - 1);
                        Edge edge = new Edge(previousNode, distanceToNode(node, previousNode), wayButNowCasted.getId()); ///wayButNowCasted.getSpeed()
                        edge.setPathTypes(wayButNowCasted, model);
                        node.addAdjecencies(edge);
                    }
                    initialisedNodes.add(node);
                }
            }
        }
    }

    private double distanceToNode(Node current, Node destination) {
        double distance = 0;
        if (current != destination) {
            double deltaX = Math.abs(destination.getX()) - Math.abs(current.getX());
            double deltaY = Math.abs(destination.getY()) - Math.abs(current.getY());
            distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        }
        return distance * 111.320 * Model.scalingConstant;
    }

    public void createPath(Node target){
        float minX = 100;
        float maxX = -100;
        float minY = 100;
        float maxY = -100;
        path = new ArrayList<Node>();
        for (Node node = target; node != null; node = node.parent) { //Starts on the target and work back to start
            if (node.getX() < minX){
                minX = node.getX();
            }
            if (node.getX() > maxX){
                maxX = node.getX();
            }
            if (node.getY() < minY){
                minY = node.getY();
            }
            if (node.getY() > maxY){
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

            for (Edge e : previousNode.getAdjecencies()) {
                if (e.target == node) {
                    firstId = e.getWayID();
                }
            }
            for (Edge e : node.getAdjecencies()) {
                if (e.target == nextNode) {
                    secondId = e.getWayID();
                }
            }
            int currentMaxSpeed = 1;

            String lastRoadName;
            if (model.getWayIndex().getMember(firstId).getName().equals("")) {
                lastRoadName = "unknown road";
            } else {
                lastRoadName = model.getWayIndex().getMember(firstId).getName();
            }

            //count exits in roundabout
            if (isRoundabout && node.getAdjecencies().size() > 1) {
                int count = 0;
                int limit = 0;
                for (Edge e : node.getAdjecencies()) {
                    if (e.isDriveable() && e.isCyclable() && e.isWalkable()) {
                        limit = 3;
                    } else if (e.isDriveable() && e.isCyclable() || e.isDriveable() && e.isWalkable()) {
                        limit = 2;
                    } else {
                        limit = 1;
                    }
                    count++;
                }
                if (count > limit) exits++;
            }

            //the next way is different than the current
            if (firstId != secondId && !lastRoadName.equals(model.getWayIndex().getMember(secondId).getName())) {
                currentMaxSpeed = model.getWayIndex().getMember(firstId).getSpeed();
                if (type.equals(TransportType.WALK)){
                    currentMaxSpeed = 5;
                } else if (type.equals(TransportType.BICYCLE)){
                    currentMaxSpeed = 15;
                }
                if (!isRoundabout) {
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
                Step step = new Step(direction, lastRoadName, currentDistance);
                if (exits > 0) {
                    step.setExits(exits);
                }
                routeDescription.add(step);
                routeDescription.add(new Step(Direction.ARRIVAL, lastRoadName, 0));
            }
        }

        if (2 == path.size()) {
            currentDistance += distanceToNode(path.get(0), path.get(1));
            totalDistance += currentDistance;

            long firstId = 0;
            for (Edge e : path.get(0).getAdjecencies()) {
                if (e.target == path.get(1)) {
                    firstId = e.getWayID();
                }
            }

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
        return routeDescription;
    }

    private Direction getDirection(Node currentNode, Node previousNode, Node nextNode) {
        Direction direction = null;
        double theta = Math.atan2(nextNode.getY() - currentNode.getY(), nextNode.getX() - currentNode.getX()) -
                Math.atan2(previousNode.getY() - currentNode.getY(), previousNode.getX() - currentNode.getX());

        double result = Math.toDegrees(theta); //the same as multiplying theta by 180/pi

        //todo find better solution
        if (result > 0) result = 360 - result;
        result = Math.abs(result);

        if (result > 190) {
            direction = Direction.LEFT;
        } else if (result < 165) {
            for (Edge e : currentNode.getAdjecencies()) {
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

    public void AStarSearch(Node start, Node end, TransportType type) {
        this.type = type;
        for (Node node : initialisedNodes) {
            node.setHScores(distanceToNode(node, end));
        }

        PriorityQueue<Node> pq = new PriorityQueue<Node>(20, new NodeComparator()); //Maybe set initial capacity based on educated guess?

        //cost from start
        start.g_scores = 0;

        pq.add(start);

        boolean found = false;

        while ((!pq.isEmpty()) && (!found)) { //While content in PQ and goal not found
            //the node in having the lowest f_score value
            Node current = pq.poll(); //Gets the first element in the PQ

            current.explored = true; //Adds current to the explored set to remember that it is explored

            //Checks if current is goal
            if (current.getId() == end.getId()) {
                found = true;
            }

            //Checks every child of current node
            for (Edge e : current.getAdjecencies()) {
                if (type == TransportType.CAR && e.isDriveable() || type == TransportType.BICYCLE && e.isCyclable() || type == TransportType.WALK && e.isWalkable()) {
                    Node child = e.target;
                    double cost = e.weight;
                    double temp_g_scores = current.g_scores + cost;
                    double temp_f_scores = temp_g_scores + child.h_scores;


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

        List<Node> path = new ArrayList<Node>();
        for (Node node : initialisedNodes) {
            if (node.explored) {
                path.add(node);
                node.explored = false;
            }
        }
        model.setAStarDebugPath(path);

        createPath(end);
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

    public String getTotalDistance() {
        String result = "Distance: ";
        double distance = Math.round(totalDistance * 10.0) / 10.0;
        if (distance < 1){
            result += distance*1000 + " m";
        }
        else{
            result += distance + " km";
        }
        return result;
    }

    public String getTotalTime() {
        String result = "Estimated Time: ";
        System.out.println(totalTime);
        double time = totalTime * 60;

        if (time < 1) {
            result += "1 min";
        } else {
            int timeInMinutes = (int) time;
            int minutes = timeInMinutes % 60;
            int hours = timeInMinutes / 60;

            if (hours >= 1){
                result += hours + " hours";
            }
            if (hours >= 1 && minutes != 0){
                result += " and ";
            }
            if (minutes != 0){
                result += minutes + " min";
            }
        }

        return result;
    }
}
