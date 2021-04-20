package bfst21.pathfinding;

import java.util.*;

import bfst21.Model;
import bfst21.osm.*;

public class AStar {
    Model model;
    List<Node> initialisedNodes;
    boolean isRoundabout;

    public AStar(Model model){
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
                        Edge edge = new Edge(nextNode, distanceToNode(node, nextNode),wayButNowCasted.getId()); ///wayButNowCasted.getSpeed()
                        edge.setPathTypes(wayButNowCasted, model);
                        node.addAdjecencies(edge);
                    }
                    if (i > 0 && !wayButNowCasted.isOneway()) {
                        Node previousNode = wayButNowCasted.getNodes().get(i - 1);
                        Edge edge = new Edge(previousNode, distanceToNode(node, previousNode),wayButNowCasted.getId()); ///wayButNowCasted.getSpeed()
                        edge.setPathTypes(wayButNowCasted, model);
                        node.addAdjecencies(edge);
                    }
                    initialisedNodes.add(node);
                }
            }
        }
    }

    private double distanceToNode(Node current, Node destination){
        double distance = 0;
        if(current!=destination) {
            double deltaX = Math.abs(destination.getX()) - Math.abs(current.getX());
            double deltaY = Math.abs(destination.getY()) - Math.abs(current.getY());
            distance = Math.sqrt(Math.pow(deltaX,2)+Math.pow(deltaY,2));
        }
        return distance * 111.320 * 0.56;
    }

    public ArrayList<String> printPath(Node target){
        ArrayList<String> routeDescription = new ArrayList<>();
        List<Node> path = new ArrayList<Node>();
        double totalDistance = 0;
        double currentDistance = 0;

        for(Node node = target; node!=null; node = node.parent){//Starts on the target and work back to start
            path.add(node);
        }

        Collections.reverse(path);

        for (int i = 1; i < path.size()-1; i++) {
            Node node = path.get(i);
            Node nextNode = path.get(i+1);
            Node previousNode = path.get(i-1);
            currentDistance += distanceToNode(previousNode,node);

            long firstId = 0;
            long secondId = 0;

            for (Edge e: previousNode.getAdjecencies()){
                if(e.target == node){
                    firstId = e.getWayID();
                }
            }
            for (Edge e: node.getAdjecencies()){
                if(e.target == nextNode){
                    secondId = e.getWayID();
                }
            }
            if(firstId != secondId){
                totalDistance += currentDistance;
                routeDescription.add("Follow " + model.getWayIndex().getMember(firstId).getName() + " for " + getMetric(currentDistance));
                routeDescription.add(getDirection(node,previousNode,nextNode) + model.getWayIndex().getMember(secondId).getName());
                System.out.println("Follow " + model.getWayIndex().getMember(firstId).getName() + " for " + getMetric(currentDistance));
                System.out.println(getDirection(node,previousNode,nextNode) + model.getWayIndex().getMember(secondId).getName());
                currentDistance = 0;
            }
            if(i == path.size()-2){
                routeDescription.add("Follow " + model.getWayIndex().getMember(secondId).getName() + " for " + getMetric(currentDistance) + " until you arrive at your destination");
                System.out.println("Follow " + model.getWayIndex().getMember(secondId).getName() + " for " + getMetric(currentDistance) + " until you arrive at your destination");
            }
        }
        routeDescription.add("The routeDescription was in total: " + getMetric(totalDistance));
        System.out.println("The routeDescription was in total: " + getMetric(totalDistance));


        model.setAStarPath(path);
        return routeDescription;
    }

    private String getDirection(Node A, Node B, Node C) {
        String direction = "";
        double theta = Math.atan2(C.getY() - A.getY(), C.getX() - A.getX()) -
                Math.atan2(B.getY() - A.getY(), B.getX() - A.getX());

        double result = Math.toDegrees(theta); //the same as multiplying theta by 180/pi

        //todo find better solution
        if(result > 0) result = 360-result;
        result = Math.abs(result);

        if(result > 190) {
            direction = "Turn left at ";
        } else if(result < 170) {
            for(Edge e: A.getAdjecencies()){
                if(e.target == C){
                    if(model.getWayIndex().getMember(e.getWayID()).isJunction()){
                        isRoundabout = true;
                    }
                    else{
                        direction = "Turn right at ";
                    }
                }
            }
        } else{
            direction = "Continue at ";
        }

        return direction;
    }

    public void AStarSearch(Node start, Node end, TransportType type){
        for(Node node : initialisedNodes) {
            node.setHScores(distanceToNode(node, end));
        }

        PriorityQueue<Node> pq = new PriorityQueue<Node>(20, new NodeComparator()); //Maybe set initial capacity based on educated guess?

        //cost from start
        start.g_scores = 0;

        pq.add(start);

        boolean found = false;

        while((!pq.isEmpty())&&(!found)) { //While content in PQ and goal not found
            //the node in having the lowest f_score value
            Node current = pq.poll(); //Gets the first element in the PQ

            current.explored = true; //Adds current to the explored set to remember that it is explored

            //Checks if current is goal
            if (current.getId() == end.getId()) {
                found = true;
            }

            //Checks every child of current node
            for (Edge e : current.getAdjecencies()) {
                if(type == TransportType.CAR && e.isDriveable() || type == TransportType.BICYCLE && e.isCyclable() || type == TransportType.WALK && e.isWalkable()) {
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
        for(Node node : initialisedNodes) {
            if(node.explored) {
                path.add(node);
            }
        }
        model.setAStarDebugPath(path);

        //todo maybe move this
        printPath(end);
    }

    public String getMetric(double currentDistance){
        String metric = " km";
        if(currentDistance<1){
            currentDistance = currentDistance*1000;
            metric = " m";
        }
        return Math.round(currentDistance) + metric;
    }

    private static class NodeComparator implements Comparator<Node>{
        //override compare method
        public int compare(Node i, Node j){
            if(i.f_scores > j.f_scores){
                return 1;
            }

            else if (i.f_scores < j.f_scores){
                return -1;
            }

            else{
                return 0;
            }
        }
    }
}
