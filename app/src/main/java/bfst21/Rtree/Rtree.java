package bfst21.Rtree;
import bfst21.MapCanvas;
import bfst21.Model;
import bfst21.osm.Drawable;
import bfst21.osm.Way;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Rtree {
    private Model model;
    private RtreeNode root;
    public final static int maxChildren = 5;
    private int height;
    private int rootSlices;
    private boolean vertical = true;
    private MapCanvas canvas;

    public Rtree(Model model, @org.jetbrains.annotations.NotNull List<Drawable> drawables){
        this.model = model;
        System.out.println(Math.ceil(log(drawables.size())));
        height = (int) Math.ceil(log(drawables.size()));
        var subtreeHeight  = Math.pow(maxChildren, height - 1);

        rootSlices = (int) Math.floor(Math.sqrt(Math.ceil(height / subtreeHeight)));

        root = new RtreeNode(drawables, vertical, rootSlices);
    }

    public Way NearestWay(Point2D p) {
        Way nearest = null;
        double currentNearestDist = 100000000;

        LinkedList<RtreeNode>  explorationQueue = new LinkedList<>();
        explorationQueue.add(root);

        while (!explorationQueue.isEmpty()) {
            var current = explorationQueue.removeFirst();

            if (current.children != null) {
                for (var child : current.children) {
                    if (child.getRect().contains(p)) {
                        explorationQueue.add(child);
                    }
                }
            }

            if(current instanceof RtreeLeaf) {
                if (current.getRect().contains(p)) {
                    for (Drawable way: ((RtreeLeaf) current).getDrawables()) {
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
        }
        return nearest;
    }

    public List<Drawable> query(Rectangle queryRect) {
        ArrayList<Drawable> result = new ArrayList<>();

        LinkedList<RtreeNode>  explorationQueue = new LinkedList<>();
        explorationQueue.add(root);

        while (!explorationQueue.isEmpty()) {
            var current = explorationQueue.removeFirst();

            if (current.children != null) {
                for (var child : current.children) {
                    if (queryRect.contains(child.getRect())){
                        result.addAll(getAllDrawables(child));
                    }
                    else if (queryRect.intersects(child.getRect())) {
                        explorationQueue.add(child);
                    }
                }
            }

            if(current instanceof RtreeLeaf) {
                if (queryRect.intersects(current.getRect())) {
                    result.addAll(((RtreeLeaf) current).getDrawables());
                }
            }
        }

        return result;
    }

    private double log(double num)  {
        return Math.log(Rtree.maxChildren) / Math.log(num);
    }

    public void drawRTree(Rectangle window, GraphicsContext gc) {
        gc.setStroke(Color.PURPLE);
        for (Drawable d: query(window)) {
            d.getRect().draw(gc);
        }
        gc.setStroke(Color.RED);

        LinkedList<RtreeNode>  explorationQueue = new LinkedList<>();
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
/*
            if(current instanceof RtreeLeaf) {
                if (window.intersects(current.getRect())) {
                    result.addAll(((RtreeLeaf) current).getDrawables());
                }
            } */
        }


        gc.setStroke(Color.BLACK);
        window.draw(gc);
    }

    public List<Drawable> getAllDrawables(RtreeNode startNode) {
        List<Drawable> AllDrawables = new ArrayList<>();

        LinkedList<RtreeNode>  explorationQueue = new LinkedList<>();
        explorationQueue.add(startNode);
        while (!explorationQueue.isEmpty()) {
            RtreeNode current = explorationQueue.removeFirst();

            if (current.getChildren() != null) {
                explorationQueue.addAll(current.getChildren());
            }

            else if (current instanceof RtreeLeaf) {
                AllDrawables.addAll(((RtreeLeaf) current).getDrawables());
            }
        }
        return AllDrawables;
    }
}
