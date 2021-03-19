package bfst21.osm;

import java.util.ArrayList;
import java.util.List;

public class NodeIndex {
    private List<Node> nodes;
    private boolean isSorted;

    public NodeIndex() {
        this.nodes = new ArrayList<>();
        this.isSorted = true;
    }

    public void addNode(Node node) {
        nodes.add(node);
        isSorted = false;
    }

    public Node getNode(long id) {
        if (!isSorted) {
            nodes.sort((a, b) -> Long.compare(a.getId(), b.getId()));
            isSorted = true;
        }

        long lo = 0;
        long hi = nodes.size();
        while (lo + 1 < hi) {
            long mid = (lo + hi) / 2;
            if (nodes.get((int) mid).getId() <= id) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        Node node = nodes.get((int) lo);

        if (node.getId() == id) {
            return node;
        } else {
            return null;
        }
    }

    public int size() {
        return nodes.size();
    }
}
