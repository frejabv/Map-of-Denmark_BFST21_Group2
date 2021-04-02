package bfst21.osm;

import bfst21.Model;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class KDTree {
    Model model;
    private RectHV bounds;
    private Node root;
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
    public void insert(Node qNode) {
        if (qNode == null) {
            throw new NullPointerException("Query Node is null upon insertion into KDTree");
        }
        //check if the node is outside the border
        if (!bounds.contains(new Point2D(qNode.getX(), qNode.getY())))
            outOfBoundsCounter++;
        else if (isEmpty()) {
            //create root if tree is empty
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
    private Node insert(Node currentNode, Node parent, Node qNode, boolean orientation) {
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


    public boolean contains(Node qNode) {
        if (qNode == null) {
            throw new NullPointerException("null key at KdTree.contains(Point2D p)");
        }

        if (bounds.contains(new Point2D(qNode.getX(),qNode.getY())))
            return false;

        return contains(root, qNode, true);
    }

    private boolean contains(Node currentNode, Node qNode, boolean orientation) {
        if (currentNode == null) {
            return false;
        }

        if (currentNode.getId() == qNode.getId()) {
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
    public Node nearest(Point2D p) {
        if (isEmpty()) {
            return null;
        }

        if (!root.getRect().contains(p)){
            return null;
        }

        Node closest = root;
        return nearest(root, closest, p, true);
    }

    /**
     * The recursive part of the nearest function.
     * @param currentNode       the current root.
     * @param closest our current closest node.
     * @param p       the point we are querying about.
     * @return returns the closest node when there are no other candidates.
     */
    private Node nearest(Node currentNode, Node closest, Point2D p, boolean orientation) {
        Node c = closest;

        if (currentNode == null) {
            return c;
        }

        double minDistance = Math.sqrt(p.distance(c.getX(), c.getY()));

        if (currentNode.getRect().distanceSquaredTo(p) >= minDistance) {
            return c;
        }

        double thisDistance = Math.sqrt(p.distance(currentNode.getX(), currentNode.getY()));

        if (thisDistance < minDistance) {
            c = currentNode;
        }

        boolean areCoordinatesLessThan = false;
        if (orientation) {
            areCoordinatesLessThan = p.getX() < currentNode.getX();
        } else {
            areCoordinatesLessThan = p.getY() < currentNode.getY();
        }

        if (areCoordinatesLessThan) {
            c = nearest(currentNode.getLeft(), c, p, !orientation);
            c = nearest(currentNode.getRight(), c, p, !orientation);
        } else {
            c = nearest(currentNode.getRight(), c, p, !orientation);
            c = nearest(currentNode.getLeft(), c, p, !orientation);
        }

        return c;
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

    private void drawLines(Node currentNode, GraphicsContext gc, boolean orientation) {
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
