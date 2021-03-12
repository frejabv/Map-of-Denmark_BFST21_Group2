package bfst21.osm;

public class Node {
    private float x;
    private float y;
    private long id;
    // Scale nodes lattitude to account for the curvature of the earth
    private static float scalingConstant = 0.56f;

    public Node(float x, float y, long id) {
        this.x = x;
        this.y = -y / scalingConstant;
        this.id = id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public long getId() {
        return id;
    }
}
