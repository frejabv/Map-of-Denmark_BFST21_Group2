package bfst21.Rtree;

import bfst21.osm.Drawable;
import javafx.geometry.Point2D;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RtreeNode implements Comparable<RtreeNode>{
    private Rectangle rect;
    protected List<RtreeNode> children;
    private double distTo;

    public RtreeNode(List<Drawable> descendants) {
        rect = createBoundingBox(descendants);
    };

    public RtreeNode(List<Drawable> descendants, boolean vertical) {
        this(descendants, vertical, Rtree.maxChildren);
    }

    public RtreeNode(List<Drawable> descendants, boolean vertical, int sliceSize) {
        children = new ArrayList<>();

        rect = createBoundingBox(descendants);

        sortDrawables(descendants, vertical);

        int splitSize = (int) Math.floor(descendants.size() / sliceSize);
        int currentOffset = 0;
        if (descendants.size() > (sliceSize * sliceSize)) {
            for (int i = 0; i < sliceSize; i++) {
                var toIndex = Math.min(currentOffset + splitSize, descendants.size() - 1);
                children.add(new RtreeNode(descendants.subList(currentOffset, toIndex), !vertical));
                currentOffset += splitSize;
            }
        } else {
            for (int i = 0; i < sliceSize; i++) {
                var toIndex = Math.min(currentOffset + splitSize, descendants.size() - 1);
                children.add(new RtreeLeaf(descendants.subList(currentOffset, toIndex)));
                currentOffset += splitSize;
            }
        }
    }

    public Rectangle getRect() {
        return rect;
    }

    public List<RtreeNode> getChildren(){
        return children;
    }

    /**
     * Sorts the provided drawable list using either their x or y coordinate dictated by the vertical boolean
     * THIS METHOD MUTATES THE PROVIDED LIST
     * @param toSort - The list to sort. Other references to this list will be mutated
     * @param vertical - Dictates whether which axis the items should be sorted in
     */
    protected void sortDrawables(List<Drawable> toSort, boolean vertical) {
        toSort.sort((a, b) -> {
            float aVal = vertical ? a.getRect().minY : a.getRect().minX;
            float bVal = vertical ? b.getRect().minY : b.getRect().minX;
            return Float.compare(aVal, bVal);
        });
    }
    
    protected Rectangle createBoundingBox(List<Drawable> descendants) {
        float minX = 1800, minY = 1800, maxX = -1800, maxY = -1800;
        
        for (var descendant : descendants) {
            Rectangle descendantBoundingBox = descendant.getRect();
            
            if (minX > descendantBoundingBox.getMinX()) minX = descendantBoundingBox.getMinX();

            if (minY > descendantBoundingBox.getMinY()) minY = descendantBoundingBox.getMinY();

            if (maxX < descendantBoundingBox.getMaxX()) maxX = descendantBoundingBox.getMaxX();

            if (maxY < descendantBoundingBox.getMaxY()) maxY = descendantBoundingBox.getMaxY();
        }

        return new Rectangle(minX, minY, maxX, maxY);
    }

    public void setDistTo(Point2D p) {
        this.distTo = rect.distanceSquaredTo(p);
    }

    public double getDistTo() {
        return distTo;
    }

    @Override
    public int compareTo(@NotNull RtreeNode that) {
        return Double.compare(this.distTo, that.distTo);
    }
}
