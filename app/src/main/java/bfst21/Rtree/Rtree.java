package bfst21.Rtree;

import bfst21.osm.Drawable;
import bfst21.osm.Way;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class Rtree {
    public final static int maxChildren = 5;
    private RtreeNode root;
    private boolean vertical = false;

    public Rtree(@org.jetbrains.annotations.NotNull List<Drawable> drawables) {
        if (!drawables.isEmpty()) {
            root = new RtreeNode(drawables, vertical, maxChildren);
        }
    }

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

            if (current.children != null) {
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
        for (Drawable d: query(window)) {
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
