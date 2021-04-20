package bfst21.Rtree;
import bfst21.Model;
import bfst21.osm.Drawable;
import bfst21.osm.Way;

import javafx.geometry.Point2D;

import java.util.List;

public class Rtree {
    Model model;
    final static int maxChildren = 5;


    public Rtree(Model model, List<Drawable> drawables){
        this.model = model;





    }

    public Way nearestRoad(Point2D p){
        //TODO find nearest way
        return null;
    }
}
