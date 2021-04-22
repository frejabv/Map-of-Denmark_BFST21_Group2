package bfst21.Rtree;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;


public class Rectangle {
    float minX, minY, maxX, maxY;

    public Rectangle(float minX, float minY, float maxX, float maxY){
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public boolean intersects(Rectangle that) {
        if (that == null) return false;
        return this.maxX >= that.minX && this.maxY >= that.minY
                && that.maxX >= this.minX && that.maxY >= this.minY;
    }

    public double distanceSquaredTo(Point2D p) {
        double dx = 0.0, dy = 0.0;
        if (p.getX() < minX) dx = p.getX() - minX;
        else if (p.getX() > maxX) dx = p.getX() - maxX;
        if (p.getY() < minY) dy = p.getY() - minY;
        else if (p.getY() > maxY) dy = p.getY() - maxY;
        return dx * dx + dy * dy;
    }

    public void draw(GraphicsContext gc) {
        gc.beginPath();
        gc.moveTo(minX, minY);
        gc.lineTo(minX, maxY);
        gc.lineTo(maxX, maxY);
        gc.lineTo(maxX, minY);
        gc.lineTo(minX, minY);
        gc.stroke();
    }
}
