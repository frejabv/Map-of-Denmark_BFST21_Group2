package bfst21;

public class Model {
    private NodeIndex nodeIndex;

    public Model(String filepath) {
        nodeIndex = new NodeIndex();
        try {
            OSMParser.readMapElements(filepath, this);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println(nodeIndex.size());
        }
    }

    public void addToNodeIndex(Node node) {
        nodeIndex.addNode(node);
    }

    public NodeIndex getNodeIndex() {
        return nodeIndex;
    }

}
