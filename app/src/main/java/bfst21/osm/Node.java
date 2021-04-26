package bfst21.osm;

import java.io.Serializable;
import bfst21.Model;

public class Node extends Member implements Serializable {
import javafx.geometry.Point2D;

public class Node extends Member{
    private float x;
    private float y;

    public Node(float x, float y, long id) {
        super(id);
        this.x = x;
        this.y = y / -Model.scalingConstant;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public double distanceToSquared(Point2D p) {
        return (x - p.getX() * x - p.getX()) + (y - p.getY() * y - p.getY());
    }
}
