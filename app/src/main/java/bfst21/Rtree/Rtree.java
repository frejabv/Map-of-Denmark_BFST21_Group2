package bfst21.Rtree;
import bfst21.Model;
import bfst21.osm.Drawable;
import bfst21.osm.Way;

import javafx.geometry.Point2D;

import java.util.Collections;
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

        root = new RtreeNode(drawables, true, rootSlices);
    }

    public Way nearestRoad(Point2D p){
        //TODO find nearest way
        return null;
    }


    private double log(double num)  {
        return Math.log(Rtree.maxChildren) / Math.log(num);
    }
}
