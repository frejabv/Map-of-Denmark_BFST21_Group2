package bfst21.osm;
import bfst21.osm.KDTree.RectHV;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Node extends Member{
    private float x;
    private float y;
    // Scale node's lattitude to account for the curvature of the earth
    private static float scalingConstant = -0.56f;

    private Node left;
    private Node right;
    private RectHV rect;

    public Node(float x, float y, long id) {
        super(id);
        this.x = x;
        this.y = y / scalingConstant;
        this.rect = null;
        this.left = null;
        this.right = null;
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

    public void drawKDTLine(boolean orientation, GraphicsContext gc) {
        gc.beginPath();
        if (orientation){
            gc.setStroke(Color.RED);
            gc.moveTo(x, rect.getMinY());
            gc.lineTo(x, rect.getMaxY());
        } else {
            gc.setStroke(Color.BLUE);
            gc.moveTo(rect.getMinX() , y);
            gc.lineTo(rect.getMaxX(), y);
        }
        gc.stroke();
    }
}
