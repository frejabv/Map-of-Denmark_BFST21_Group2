package bfst21.pathfinding;

import java.util.*;

import bfst21.Model;
import bfst21.osm.*;

public class AStar {
    Model model;
    List<Node> initialisedNodes;

    public AStar(Model model){ //Node start, Node end
        this.model = model;

        //Read data
        readData(); //end

        //Gets the path elements
        //List<Node> path = printPath(end);
        //System.out.println("dk");
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
                        Edge edge = new Edge(nextNode, distanceToNode(node, nextNode)); ///wayButNowCasted.getSpeed()
                        edge.setPathTypes(wayButNowCasted);
                        node.addAdjecencies(edge);
                    }
                    if (i > 0 && !wayButNowCasted.isOneway()) {
                        Node previousNode = wayButNowCasted.getNodes().get(i - 1);
                        Edge edge = new Edge(previousNode, distanceToNode(node, previousNode)); ///wayButNowCasted.getSpeed()
                        edge.setPathTypes(wayButNowCasted);
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
        return distance * 111.320;
    }

    public String printPath(Node target){
        List<Node> path = new ArrayList<Node>();
        String result = "";

        for(Node node = target; node!=null; node = node.parent){//Starts on the target and work back to start
            path.add(node);
        }

        Collections.reverse(path); //To print in the order from start to target
        for (Node temp:  path){
            result += temp.getId() + " -> ";
        }
        model.setAStarPath(path);
        return result;
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
                Node child = e.target;
                double cost = e.weight;
                double temp_g_scores = current.g_scores + cost;
                double temp_f_scores = temp_g_scores + child.h_scores;


                //Checks if child node has been evaluated and the newer f_score is higher, skip
                if((child.explored) && (temp_f_scores >= child.f_scores)){
                    continue;
                }

                //else if child node is not in queue (add it) or newer f_score is lower (Update them)
                else if((!pq.contains(child)) || (temp_f_scores < child.f_scores)){
                    child.parent = current;
                    child.g_scores = temp_g_scores;
                    child.f_scores = temp_f_scores;

                    if(pq.contains(child)){
                        pq.remove(child);
                    }
                    pq.add(child);
                }
            }
        }
        //todo maybe move this
        printPath(end);
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
