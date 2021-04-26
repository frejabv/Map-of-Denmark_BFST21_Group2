package bfst21.osm;

import bfst21.Model;
import bfst21.POI;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class KDTree {
    Model model;
    private RectHV bounds;
    private POI root;
    private int size;

    //fields for debugging and testing
    public static int IAE3Counter = 0;
    public static int IAE4Counter = 0;
    public static int outOfBoundsCounter = 0;

    public KDTree(Model model) {
        this.model = model;
        this.root = null;
        size = 0;
        IAE3Counter = 0;
        IAE4Counter = 0;
        outOfBoundsCounter = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void setBounds(){
        //note: maxY and minY are swapped, such that the negative scaling constant has no effect on the tree
        bounds = new RectHV(model.getMinX(), model.getMaxY(), model.getMaxX(), model.getMinY());
    }

    /**
     * insert query Node into the tree, if it is not null and does not exist in the tree already.
     */
    public void insert(POI qNode) {
        if (qNode == null) {
            throw new NullPointerException("Query Node is null upon insertion into KDTree");
        }
        //check if the node is outside the border
        if (!bounds.contains(new Point2D(qNode.getX(), qNode.getY())))
            outOfBoundsCounter++;
        //create root if tree is empty
        else if (isEmpty()) {
            root = qNode;
            root.setRect(bounds);
            size++;
        } else {
            insert(root, null, qNode, true);
        }
    }

    /**
     * This is a recursive call that inserts a node into the correct empty spot.
     * @param currentNode is the node position we want to try and insert qNode into.
     * @param parent      is the current parent of out element.
     * @param qNode       The node we want to insert: out query Node.
     * @param orientation flips every recursion
     * @returns           the Node at with its correct parent and left/right rectangle/domain
     */
    private POI insert(POI currentNode, POI parent, POI qNode, boolean orientation) {
        if (currentNode == null) {
            RectHV r = null;

            float minX = parent.getRect().getMinX();
            float minY = parent.getRect().getMinY();
            float maxX = parent.getRect().getMaxX();
            float maxY = parent.getRect().getMaxY();

            if (orientation) {
                if (qNode.getY() < parent.getY()) {
                    maxY = parent.getY();
                } else {
                    minY = parent.getY();
                }
            } else {
                if (qNode.getX() < parent.getX()) {
                    maxX = parent.getX();
                } else {
                    minX = parent.getX();
                }
            }
            r = new RectHV(minX, minY, maxX, maxY);
            size++;
            qNode.setRect(r);
            return qNode;
        }

        boolean areCoordinatesLessThan = false;
        if (orientation) {
            areCoordinatesLessThan = qNode.getX() < currentNode.getX();
        } else {
            areCoordinatesLessThan = qNode.getY() < currentNode.getY();
        }

        if (areCoordinatesLessThan) {
            currentNode.setLeft(insert(currentNode.getLeft(), currentNode, qNode, !orientation));
        } else {
            currentNode.setRight(insert(currentNode.getRight(), currentNode, qNode, !orientation));
        }

        return currentNode;
    }

    public boolean contains(POI qNode) {
        if (qNode == null) {
            throw new NullPointerException("null key at KdTree.contains(Point2D p)");
        }

        if (!bounds.contains(new Point2D(qNode.getX(),qNode.getY())))
            return false;

        return contains(root, qNode, true);
    }

    private boolean contains(POI currentNode, POI qNode, boolean orientation) {
        if (currentNode == null) {
            return false;
        }

        if (currentNode.getName().equals(qNode.getName())) {
            return true;
        }

        boolean areCoordinatesLessThan = false;
        if (orientation) {
            areCoordinatesLessThan = qNode.getX() < currentNode.getX();
        } else {
            areCoordinatesLessThan = qNode.getY() < currentNode.getY();
        }

        if (areCoordinatesLessThan) {
            return contains(currentNode.getLeft(), qNode, !orientation);
        } else {
            return contains(currentNode.getRight(), qNode, !orientation);
        }
    }

    /**
     * begins the recursive call to nearest.
     * @param p the point we are querying about
     * @return the nearest Node
     */
    public ArrayList<POI> nearestK(Point2D p, int amount) {
        if (isEmpty()) {
            return null;
        }

        if (!bounds.contains(p)){
            return null;
        }

        ArrayList<POI> closestList = new ArrayList<>();
        closestList.add(root);
        root.setDistTo(p);
        return nearest(root, closestList, p, true, amount);
    }

    /**
     * The recursive part of the nearest function.
     * @param currentNode       the current root.
     * @param closest our current closest node.
     * @param p       the point we are querying about.
     * @return returns the closest node when there are no other candidates.
     */
    private ArrayList<POI> nearest(POI currentNode, ArrayList<POI> closest, Point2D p, boolean orientation, int amount) {
        ArrayList<POI> clist = closest;
        POI c = clist.get(clist.size()-1);

        if (currentNode == null) {
            return clist;
        }

        double minDistance = c.distTo;

        if (currentNode.getRect().distanceSquaredTo(p) >= minDistance) {
            return clist;
        }

        currentNode.setDistTo(p);
        if (clist.size() < amount && !clist.contains(currentNode)) {
            clist.add(currentNode);
            Collections.sort(clist);
        } else if (currentNode.distTo < minDistance) {
            clist.remove(c);
            clist.add(currentNode);
            Collections.sort(clist);
        }

        boolean areCoordinatesLessThan = false;
        if (orientation) {
            areCoordinatesLessThan = p.getX() < currentNode.getX();
        } else {
            areCoordinatesLessThan = p.getY() < currentNode.getY();
        }

        if (areCoordinatesLessThan) {
            clist = nearest(currentNode.getLeft(), clist, p, !orientation, amount);
            clist = nearest(currentNode.getRight(), clist, p, !orientation, amount);
        } else {
            clist = nearest(currentNode.getRight(), clist, p, !orientation, amount);
            clist = nearest(currentNode.getLeft(), clist, p, !orientation, amount);
        }

        return clist;
    }

    public void drawLines(GraphicsContext gc) {
        if (!isEmpty()) {
            //draw border
            gc.setStroke(Color.BLACK);
            gc.beginPath();
            gc.moveTo(bounds.getMinX(), bounds.getMinY());
            gc.lineTo(bounds.getMaxX(), bounds.getMinY());
            gc.lineTo(bounds.getMaxX(), bounds.getMaxY());
            gc.lineTo(bounds.getMinX(), bounds.getMaxY());
            gc.lineTo(bounds.getMinX(), bounds.getMinY());
            gc.stroke();

            //begin lines
            root.drawKDTLine(true, gc);
            if (root.getRight() != null) {
                drawLines(root.getRight(), gc, false);
            }
            if (root.getLeft() != null) {
                drawLines(root.getLeft(), gc, false);
            }
        }
    }

    private void drawLines(POI currentNode, GraphicsContext gc, boolean orientation) {
        //draw lines
        currentNode.drawKDTLine(orientation, gc);
        if (currentNode.getRight() != null) {
            drawLines(currentNode.getRight(), gc, !orientation);
        }
        if (currentNode.getLeft() != null) {
            drawLines(currentNode.getLeft(), gc, !orientation);
        }
    }

    public int getSize(){
        return size;
    }

    public RectHV getBounds(){
        return bounds;
    }

    //taken from Sedgewick and Wayne
    public static class RectHV {
        private final float minX, minY;
        private final float maxX, maxY;

        public RectHV(float minX, float minY, float maxX, float maxY) {
            this.minX = minX;
            this.maxY = maxY;
            this.maxX = maxX;
            this.minY = minY;
            if (Float.isNaN(minX) || Float.isNaN(maxX)) {
                System.out.println("if 1 returned true: x coordinate NaN");
                throw new IllegalArgumentException("x-coordinate is NaN: " + toString());
            }
            if (Float.isNaN(minY) || Float.isNaN(maxY)) {
                System.out.println("if 2 returned true: y coordinate is NaN");
                throw new IllegalArgumentException("y-coordinate is NaN: " + toString());
            }
            if (maxX < minX) {
                IAE3Counter++;
                System.out.println("if 3 returned true");
                System.out.println(maxX + " < " + minX + " is " + (maxX < minX));
                System.out.println("ILLEGAL ARGUMENT EXCEPTION");
                //throw new IllegalArgumentException("maxX < minX: " + toString());
            }
            if (maxY < minY) {
                IAE4Counter++;
                System.out.println("if 4 returned true");
                System.out.println(maxY + " < " + minY + " is " + (maxY < minY));
                System.out.println("ILLEGAL ARGUMENT EXCEPTION");
                //throw new IllegalArgumentException("maxY < minY: " + toString());
            }
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

        public boolean intersects(RectHV that) {
            return this.maxX >= that.minX && this.maxY >= that.minY
                    && that.maxX >= this.minX && that.maxY >= this.minY;
        }

        public boolean contains(Point2D p) {
            return (p.getX() >= minX) && (p.getX() <= maxX)
                    && (p.getY() >= minY) && (p.getY() <= maxY);
        }

        public double distanceSquaredTo(Point2D p) {
            double dx = 0.0, dy = 0.0;
            if (p.getX() < minX) dx = p.getX() - minX;
            else if (p.getX() > maxX) dx = p.getX() - maxX;
            if (p.getY() < minY) dy = p.getY() - minY;
            else if (p.getY() > maxY) dy = p.getY() - maxY;
            return dx * dx + dy * dy;
        }
    }
}
