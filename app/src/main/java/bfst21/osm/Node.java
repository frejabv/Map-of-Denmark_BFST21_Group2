package bfst21.osm;
import bfst21.osm.KDTree.RectHV;
import javafx.geometry.Point2D;


public class Node extends Member{
    private float x;
    private float y;
    // Scale nodes lattitude to account for the curvature of the earth
    private static float scalingConstant = 0.56f;

    private Node left;
    private Node right;
    private RectHV rect;

    public Node(Point2D p, RectHV rect, Node left, Node right, long id) {
        super(id);
        this.x = (float) p.getX();
        this.y = (float) p.getY() / scalingConstant;
        this.rect = rect;
        this.left = left;
        this.right = right;
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }

    public Node getLeft() { return left; }
    public Node getRight() { return right; }

    public RectHV getRect() {
        return rect;
    }

    public void setRect(RectHV rect) {
        this.rect = rect;
    }

    public void setLeft(Node n) { left = n; }
    public void setRight(Node n){ right = n; }
}
