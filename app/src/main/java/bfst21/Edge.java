package bfst21;

class Edge{
    public final double weight;
    public final Vertex target;

    public Edge(Vertex targetNode, double costVal){
        target = targetNode;
        weight = costVal;
    }
}
