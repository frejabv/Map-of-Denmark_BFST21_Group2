package bfst21.osm;

import bfst21.Model;

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
}
