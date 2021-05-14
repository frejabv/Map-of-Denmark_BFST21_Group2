package bfst21.Rtree;

import bfst21.osm.Drawable;
import bfst21.osm.Way;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * The Rtree class is an R-tree with bulk loading.
 * The R-tree generally works by giving it a list, and then letting the Nodes of the Rtree split
 * the list into smaller and smaller lists, that then repeat the process. Effectively this creates
 * the tree structure.
 * The amount of "splits" that are made (the amount of children a node can have) is defined by the
 * "maxChildren" field. This field is hard coded in our version, and is 5 due to it being good enough,
 * and also small enough for us to visually be able to analyse.
 */
public class Rtree {
    public final static int maxChildren = 5;
    private final boolean vertical = false;
    private RtreeNode root;

    /**
     * Rtree Constructor. Works by simply giving it a list of Drawables.
     * @param drawables The list of drawables that are given to the root, which then starts the
     *                  splitting process.
     */
    public Rtree(@org.jetbrains.annotations.NotNull List<Drawable> drawables) {
        if (!drawables.isEmpty()) {
            root = new RtreeNode(drawables, vertical, maxChildren);
        }
    }

    /**
     * Nearest way starts works by moving down the Rtree, much like Prim's algorithm, where it looks further
     * down the closest branch/node. This is done via a priority queue, where each node and
     * @param p The Point of which we want to find the nearest way in the current RTree.
     * @return
     */
    public Way nearestWay(Point2D p) {
        Way nearest = null;
        double currentNearestDist = Double.POSITIVE_INFINITY;

        PriorityQueue<RtreeNode> pQueue = new PriorityQueue<>();
        root.setDistTo(p);
        pQueue.add(root);

        while (!pQueue.isEmpty()) {
            var current = pQueue.poll();

            if (current.getDistTo() < currentNearestDist) {
                if (current.children != null) {
                    for (var child : current.children) {
                        child.setDistTo(p);
                        pQueue.add(child);
                    }
                }

                if (current instanceof RtreeLeaf) {
                    if (current.getRect().distanceSquaredTo(p) < currentNearestDist) {
                        for (Drawable way : ((RtreeLeaf) current).getDrawables()) {
                            if (way instanceof Way) {
                                double wayDistance = ((Way) way).minimumDistanceToSquared(p);
                                if (currentNearestDist > wayDistance) {
                                    currentNearestDist = wayDistance;
                                    nearest = (Way) way;
                                }
                            }
                        }
                    }
                }
            } else {
                pQueue.clear();
            }
        }
        return nearest;
    }

    public List<Drawable> query(Rectangle queryRect) {
        ArrayList<Drawable> result = new ArrayList<>();

        LinkedList<RtreeNode> explorationQueue = new LinkedList<>();
        explorationQueue.add(root);

        while (!explorationQueue.isEmpty()) {
            var current = explorationQueue.removeFirst();

            if (current != null && current.children != null) {
                for (var child : current.children) {
                    if (queryRect.contains(child.getRect())) {
                        result.addAll(getAllDrawables(child));
                    } else if (queryRect.intersects(child.getRect())) {
                        explorationQueue.add(child);
                    }
                }
            }

            if (current instanceof RtreeLeaf) {
                if (queryRect.intersects(current.getRect())) {
                    result.addAll(((RtreeLeaf) current).getDrawables());
                }
            }
        }

        return result;
    }

    public void drawRTree(Rectangle window, GraphicsContext gc) {
        LinkedList<RtreeNode> explorationQueue = new LinkedList<>();
        explorationQueue.add(root);

        while (!explorationQueue.isEmpty()) {
            var current = explorationQueue.removeFirst();
            current.getRect().draw(gc);

            if (current.children != null) {
                for (var child : current.children) {
                    if (window.intersects(child.getRect())) {
                        explorationQueue.add(child);
                    }
                }
            }
        }
    }

    public void drawRoadRectangles(Rectangle window, GraphicsContext gc) {
        for (Drawable d : query(window)) {
            d.getRect().draw(gc);
        }
    }

    public List<Drawable> getAllDrawables(RtreeNode startNode) {
        List<Drawable> allDrawables = new ArrayList<>();

        LinkedList<RtreeNode> explorationQueue = new LinkedList<>();
        explorationQueue.add(startNode);
        while (!explorationQueue.isEmpty()) {
            RtreeNode current = explorationQueue.removeFirst();

            if (current.getChildren() != null) {
                explorationQueue.addAll(current.getChildren());
            } else if (current instanceof RtreeLeaf) {
                allDrawables.addAll(((RtreeLeaf) current).getDrawables());
            }
        }
        return allDrawables;
    }

    public RtreeNode getRoot() {
        return root;
    }
}
