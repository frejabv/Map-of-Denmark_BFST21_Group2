package bfst21.Rtree;

import bfst21.osm.Drawable;
import javafx.geometry.Point2D;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RtreeNode implements Comparable<RtreeNode>{
    private final Rectangle rect;
    protected List<RtreeNode> children;
    private double distTo;

    /**
     * The RtreeNode constructor is essentially the main piece of code for the entire R-tree. Essentially, it recursively
     * creates more RtreeNodes that become smaller and smaller - creating the desired Tree structure.
     * This method does this in a few steps:
     * firstly, it creates the RtreeNodes bounding box, which is the rectangle in which all its' children reside within.
     * secondly, it sorts its' array of drawables on either the x or y axis, swapping between them on each recursive call.
     * thirdly, it determines the amount of drawables each child will be responsible for.
     * and finally, it gives that many drawables to each of its' children, creating them in the process. The type of
     * child that is created (either a node of a leaf) is determined by how many descendants are left.
     * If the amount of descendants that a Node has is less than the sliceSize squared, then there is no need to create
     * any more RtreeNodes, instead it will create an RtreeLeaf. And if there are more, a new RtreeNode is created, which
     * then has to determine whether or not it's children will be leaves or nodes.
     * The method call finishes when all children of the node are completed: effectively, this is when the entire tree
     * structure is finished.
     * @param descendants   The current list of Drawables that are to be split
     * @param vertical      whether the list is sorted on the x or y coordinates
     * @param sliceSize     the amount of children that will be made: the amount of slices that are done
     */
    public RtreeNode(List<Drawable> descendants, boolean vertical, int sliceSize) {
        children = new ArrayList<>();

        rect = createBoundingBox(descendants);

        sortDrawables(descendants, vertical);

        int splitSize = descendants.size() / sliceSize;

        if (descendants.size() % sliceSize != 0) {
            // Integer division in java always throws away the decimal place, essentially floor the number.
            // If the number is not divisible by the sliceSize, we want to ceil the number instead.
            splitSize++;
        }

        int currentOffset = 0;
        if (descendants.size() > (sliceSize * sliceSize)) {
            for (int i = 0; i < sliceSize; i++) {
                var toIndex = Math.min(currentOffset + splitSize, descendants.size());
                children.add(new RtreeNode(descendants.subList(currentOffset, toIndex), !vertical, sliceSize));
                currentOffset = Math.min(currentOffset + splitSize, descendants.size());
            }
        } else {
            for (int i = 0; i < sliceSize; i++) {
                var toIndex = Math.min(currentOffset + splitSize, descendants.size());
                children.add(new RtreeLeaf(descendants.subList(currentOffset, toIndex)));
                currentOffset = Math.min(currentOffset + splitSize, descendants.size());
            }
        }
    }

    /**
     * This constructor is for RtreeLeaves:
     * It only creates the bounding box, as there is not need for further splitting.
     */
    public RtreeNode(List<Drawable> descendants) {
        rect = createBoundingBox(descendants);
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
