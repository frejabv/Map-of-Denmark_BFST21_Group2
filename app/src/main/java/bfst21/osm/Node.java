package bfst21.osm;

import javafx.geometry.Point2D;

public class Node extends Member{
    private float x;
    private float y;
    // Scale nodes lattitude to account for the curvature of the earth
    private static float scalingConstant = 0.56f;

    public Node(float x, float y, long id) {
        super(id);
        this.x = x;
        this.y = -y / scalingConstant;
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
