package bfst21;


import bfst21.osm.Drawable;
import bfst21.osm.Tag;

import java.util.*;

public class AStar {
    //Model model;
    //Replace with Model
    public static void main(String[] args) {
        /*ArrayList<Drawable> ways = model.getDrawableMap().get(Tag.PRIMARY);
        System.out.println(Math.sqrt(Math.pow(700,2)+Math.pow(500,2)));*/

        //Initialize the graph base on the Danish map
        Vertex n1 = new Vertex("København",4784);
        Vertex n2 = new Vertex("Roskilde",4703);
        Vertex n3 = new Vertex("Helsingør",4282);
        Vertex n4 = new Vertex("Korsør", 5008);
        Vertex n5 = new Vertex("Odense",4860);
        Vertex n6 = new Vertex("Vejle", 4360);
        Vertex n7 = new Vertex("Esbjerg",5248);
        Vertex n8 = new Vertex("Aarhus",3380);
        Vertex n9 = new Vertex("Randers",2661);
        Vertex n10 = new Vertex("Viborg",2955);
        Vertex n11 = new Vertex("Aalborg", 1553);
        Vertex n12 = new Vertex("Skagen",0);

        //Initialize the edges

        //København
        n1.adjacencies = new ArrayList<>();
        n1.adjacencies.add(new Edge(n2,560)); //Roskilde
        n1.adjacencies.add(new Edge(n3,637));  //Helsingør

        //Roskilde
        n2.adjacencies = new ArrayList<>();
        n2.adjacencies.add(new Edge(n1,560)); //København
        n2.adjacencies.add(new Edge(n4,1232)); //Korsør

        //Helsingør
        n3.adjacencies = new ArrayList<>();
        n3.adjacencies.add(new Edge(n1,637)); //København

        //Korsør
        n4.adjacencies = new ArrayList<>();
        n4.adjacencies.add(new Edge(n5,916)); //Odense
        n4.adjacencies.add(new Edge(n2,1232)); //Roskilde

        //Odense
        n5.adjacencies = new ArrayList<>();
        n5.adjacencies.add(new Edge(n4,916)); //Korsør
        n5.adjacencies.add(new Edge(n6,1301)); //Vejle

        //Vejle
        n6.adjacencies = new ArrayList<>();
        n6.adjacencies.add(new Edge(n5,1301)); //Odense
        n6.adjacencies.add(new Edge(n7,1288)); //Esbjerg
        n6.adjacencies.add(new Edge(n8,1142)); //Aarhus

        //Esbjerg
        n7.adjacencies = new ArrayList<>();
        n7.adjacencies.add(new Edge(n6,1288)); //Vejle

        //Aarhus
        n8.adjacencies = new ArrayList<>();
        n8.adjacencies.add(new Edge(n6,1142)); //Vejle
        n8.adjacencies.add(new Edge(n9,747)); //Randers

        //Randers
        n9.adjacencies = new ArrayList<>();
        n9.adjacencies.add(new Edge(n10,886)); //Viborg
        n9.adjacencies.add(new Edge(n8,747)); //Aarhus
        n6.adjacencies.add(new Edge(n11,1227)); //Aalborg

        //Viborg
        n10.adjacencies = new ArrayList<>();
        n10.adjacencies.add(new Edge(n9,886)); //Randers
        n10.adjacencies.add(new Edge(n8,747)); //Aarhus

        //Aalborg
        n11.adjacencies = new ArrayList<>();
        n11.adjacencies.add(new Edge(n9,1227)); //Randers
        n11.adjacencies.add(new Edge(n10,1369)); //Viborg
        n11.adjacencies.add(new Edge(n12,1586)); //Skagen

        //Skagen
        n12.adjacencies = new ArrayList<>();
        n12.adjacencies.add(new Edge(n11,120)); //Aalborg

        AStarSearch(n1,n12); //Sets up the search

        List<Vertex> path = printPath(n12); //Gets the path elements

        System.out.println("Path: " + path); //Prints the path
    }

    //Static for now
    public static List<Vertex> printPath(Vertex target){
        List<Vertex> path = new ArrayList<Vertex>();

        for(Vertex node = target; node!=null; node = node.parent){//Starts on the target and work back to start
            path.add(node);
        }

        Collections.reverse(path); //To print in the order from start to target

        return path;
    }

    //Static for now
    public static void AStarSearch(Vertex start, Vertex end){
        PriorityQueue<Vertex> pq = new PriorityQueue<Vertex>(20, new VertexComparator()); //Maybe set initial capacity based on educated guess?

        //cost from start
        start.g_scores = 0;

        pq.add(start);

        boolean found = false;

        while((!pq.isEmpty())&&(!found)) { //While content in PQ and goal not found
            //the node in having the lowest f_score value
            Vertex current = pq.poll(); //Gets the first element in the PQ

            current.explored = true; //Adds current to the explored set to remember that it is explored

            //Checks if current is goal
            if (current.value.equals(end.value)) {
                found = true;
            }

            //Checks every child of current node
            for (Edge e : current.adjacencies) {
                Vertex child = e.target;
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
    }

    private static class VertexComparator implements Comparator<Vertex>{
        //override compare method
        public int compare(Vertex i, Vertex j){
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
