package bfst21.Rtree;

public class Rectangle {
    float minX, minY, maxX, maxY;

    public Rectangle(float minX, float minY, float maxX, float maxY){
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public boolean intersects(Rectangle qRect){
        return false;
    }
}
