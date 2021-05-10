package bfst21.POI;

import bfst21.Rtree.Rectangle;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class POI implements Comparable<POI> {

    private String name;
    private String type;
    private String imageType;
    protected float x, y;

    private POI left;
    private POI right;
    private Rectangle rect;
    private double distTo;

    boolean deleted;

    public POI(String name, String type, float x, float y) {
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public POI(String name, String type, String imageType, float x, float y){
        this.name = name;
        this.type = type;
        this.imageType = imageType;
        this.x = x;
        this.y = y;
    }

    public void changeName(String newName) {
        name = newName + " " + type;
    }

    public String getName() {
        return name;
    }

    public void changeType (String newType) {
        type = newType;
    }

    public String getType() {
        return type;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public POI getLeft() { return left; }
    public POI getRight() { return right; }

    public Rectangle getRect() {
        return rect;
    }
    public void setRect(Rectangle rect) {
        this.rect = rect;
    }

    public void setLeft(POI n) { left = n; }
    public void setRight(POI n){ right = n; }

    public void drawKDTLine(boolean orientation, GraphicsContext gc) {
        gc.beginPath();
        if (orientation){
            gc.setStroke(Color.RED);
            gc.moveTo(x, rect.getMinY());
            gc.lineTo(x, rect.getMaxY());
        } else {
            gc.setStroke(Color.BLUE);
            gc.moveTo(rect.getMinX(), y);
            gc.lineTo(rect.getMaxX(), y);
        }
        gc.stroke();
    }

    @Override
    public int compareTo(POI that) {
        return Double.compare(this.distTo, that.distTo);
    }

    public void setDistTo(Point2D p) {
        distTo = (x-p.getX()) * (x-p.getX()) + (y-p.getY()) * (y-p.getY());
    }

    public double getDistTo() {
        return distTo;
    }

    public String getImageType() {
        return imageType;
    }

    public boolean isDeleted() { return deleted; }

    public void setDeleted() { deleted = true; }
}
