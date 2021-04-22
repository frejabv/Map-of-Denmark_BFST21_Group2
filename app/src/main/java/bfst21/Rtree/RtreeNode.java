package bfst21.Rtree;

import bfst21.osm.Drawable;

import java.util.ArrayList;
import java.util.List;

public class RtreeNode {
    Rectangle rect;
    List<RtreeNode> children;

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
                children.add(new RtreeNode(descendants.subList(currentOffset, currentOffset + sliceSize), !vertical));
                currentOffset += splitSize;
            }
        } else {
            for (int i = 0; i < sliceSize; i++) {
                children.add(new RtreeLeaf(descendants.subList(currentOffset, currentOffset + sliceSize)));
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

    public void setChildren(){

    }

    /**
     * Sorts the provided drawable list using either their x or y coordinate dictated by the vertical boolean
     * THIS METHOD MUTATES THE PROVIDED LIST
     * @param toSort - The list to sort. Other references to this list will be mutated
     * @param vertical - Dictates whether which axis the items should be sorted in
     */
    protected void sortDrawables(List<Drawable> toSort, boolean vertical) {
        toSort.sort((a, b) -> {
            float aVal = vertical ? a.getRectangle().minY :  a.getRectangle().minX;
            float bVal = vertical ? b.getRectangle().minY : b.getRectangle().minX;

            return Math.round(bVal - aVal);
        });
    }
    
    protected Rectangle createBoundingBox(List<Drawable> descendants) {
        float minX = 180, minY = 90, maxX = -180, maxY = -90;
        
        for (var descendant : descendants) {
            Rectangle descendantBoundingBox = descendant.getRectangle();
            
            if (minX < descendantBoundingBox.minX) minX = descendantBoundingBox.minX;
            if (minY < descendantBoundingBox.minY) minY = descendantBoundingBox.minY;
            if (maxX < descendantBoundingBox.maxX) maxX = descendantBoundingBox.maxX;
            if (maxY < descendantBoundingBox.maxY) maxY = descendantBoundingBox.maxY;
        }

        return new Rectangle(minX, minY, maxX, maxY);
    }
}
