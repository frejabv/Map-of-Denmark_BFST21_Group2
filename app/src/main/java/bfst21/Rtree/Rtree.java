package bfst21.Rtree;
import bfst21.Model;
import bfst21.osm.Drawable;
import bfst21.osm.Way;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Rtree {
    private Model model;
    private RtreeNode root;
    public final static int maxChildren = 5;
    private int height;
    private int rootSlices;
    private boolean vertical = false;

    public Rtree(Model model, @org.jetbrains.annotations.NotNull List<Drawable> drawables){
        this.model = model;

        height = (int) Math.ceil(log(drawables.size()));
        var subtreeHeight  = Math.pow(maxChildren, height - 1);

        rootSlices = (int) Math.floor(Math.sqrt(Math.ceil(height / subtreeHeight)));

        root = new RtreeNode(drawables, vertical, rootSlices);
    }

    public Way nearestRoad(Point2D p){
        //TODO find nearest way
        return null;
    }

    public List<Drawable> query(Rectangle queryRect) {
        ArrayList<Drawable> result = new ArrayList<>();

        LinkedList<RtreeNode>  explorationQueue = new LinkedList<>();
        explorationQueue.add(root);

        while (!explorationQueue.isEmpty()) {
            var current = explorationQueue.removeFirst();

            if (current.children != null) {
                for (var child : current.children) {
                    if (queryRect.intersects(child.getRect())) {
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
}
