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
     * Nearest way works by moving down the Rtree, much like Prim's algorithm, where it looks further
     * down the closest branch/node. This is done via a priority queue, where each node that is close enough
     * has all its children added to the queue. This continues, with RtreeLeaves adding their children as well
     * when we get far enough down. a Drawable is checked, and there is no closer element to the point,
     * then we know we have found the nearest Way.
     *
     * @param p The Point of which we want to find the nearest way in the current RTree.
     * @return  The nearest way to p.
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


    /**
     * The query method works by having a queue (called the explorationQueue) of Nodes that are iterated over, starting with
     * the root. All drawables that are inside the rectangle are added to the result arraylist.
     * When a node is checked, all its children are checked for whether or not they intersect the queryRect,
     * or are contained. If they are contained, we know that all children of the given node also are contained,
     * so we add all drawables to the result list, that are below the node - this let's us not check any of its children.
     * If a node child only intersects, it is added to the explorationQueue.
     * If a node is a leafNode, then we check whether its drawaables intersect the queryRect, and add those that do
     * to the list we return.
     * When the explorationQueue is empty, we have checked all nodes and return the result list.
     *
     * @param queryRect The rectangle that we are are looking for drawables inside (our viewport)
     * @return          The list of drawables inside the queryRect, called result.
     */
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


    /**
     * The draw method works much like the query method, where we have an explorationQueue that is explored, starting
     * with the root. If a node is in the exploration Queue, we draw its rectangle and add all of its children that
     * that intersect the window. This is done until the exploration Queue is empty.
     * Notice that this method does not draw the rectangles of all drawables within the window. This method only draws
     * the rectangles for RtreeNode objects, of which Drawables are not. The drawing of Drawable rectangles has it's own
     * method: {@link bfst21.Rtree.Rtree#drawRoadRectangles(Rectangle, GraphicsContext)}:
     * Note: Unlike the query method, we don't have the contains check. This is simply due to this method not being as
     * important to optimize, and the code for it would be longer than that of the query method.
     *
     * @param window    the rectangle of which we want to draw all RTree rectangles within.
     */
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

    /**
     * DrawRoadRectangles simply queries an area, and then draws each drawable's rectangle post-query.
     * @param window    the rectangle of which we want to draw all drawables' rectangles within.
     */
    public void drawRoadRectangles(Rectangle window, GraphicsContext gc) {
        for (Drawable d : query(window)) {
            d.getRect().draw(gc);
        }
    }

    /**
     * getAllDrawables iterates through all descendants of the startNode, and adds all Drawables to a list.
     * This list is then returned once everything has been added to it.
     * @param startNode     The node from which all descendant drawables are added to the returned list.
     * @return              the list of all descendant drawables from startNode
     */
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
