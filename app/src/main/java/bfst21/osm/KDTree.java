package bfst21.osm;

import bfst21.Model;
import javafx.geometry.Point2D;

import java.awt.*;


public class KDTree {
    Model model;
    private Node root;
    private int size;

    public KDTree(Model model) {
        this.model = model;
        this.root = null;
        size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Add point p to the tree, if it is not null and does not exist in the tree already.
     */
    public Node insert(Point2D p) {
        if (p == null) {
            throw new NullPointerException("Point2D is null upon insertion into KDTree");
        }
        if (!contains(p)) {
            root = insert(root, null, p, true);
        }
        return null;
    }

    /**
     * This is a recursive call that inserts a node into the correct empty spot.
     *
     * @param n           is the element of which we want to insert.
     * @param parent      is the current parent of out element.
     * @param p           are the coordinates of our node.
     * @param orientation flips every recursion
     * @returns the Node at with its correct parent and left/right rectangle/domain
     */
    private Node insert(Node n, Node parent, Point2D p, boolean orientation) {
        if (n == null) {
            RectHV r = null;

            if (parent == null) {
                r = new RectHV(model.getMinX(), model.getMinY(), model.getMaxX(), model.getMaxY());
            } else {
                float xmin = parent.getRect().getXmin();
                float ymin = parent.getRect().getYmin();
                float xmax = parent.getRect().getXmax();
                float ymax = parent.getRect().getYmax();

                if (orientation) {
                    if (p.getY() < parent.getY()) {
                        ymax = parent.getY();
                    } else {
                        ymin = parent.getY();
                    }
                } else {
                    if (p.getX() < parent.getX()) {
                        xmax = parent.getX();
                    } else {
                        xmin = parent.getX();
                    }
                }

                r = new RectHV(xmin, ymin, xmax, ymax);
            }
            size++;
            return new Node(p, r, null, null, 0);
        } else {
            boolean areCoordinatesLessThan = false;
            if (orientation) {
                areCoordinatesLessThan = p.getX() < n.getX();
            } else {
                areCoordinatesLessThan = p.getY() < n.getY();
            }

            if (areCoordinatesLessThan) {
                n.setLeft(insert(n.getLeft(), n, p, !orientation));
            } else {
                n.setRight(insert(n.getRight(), n, p, !orientation));
            }

            return n;
        }
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new NullPointerException("null key at KdTree.contians(Point2D p)");
        }

        return contains(root, p, true);
    }

    private boolean contains(Node n, Point2D p, boolean orientation) {
        if (n == null) {
            return false;
        }
        if (n.getX() == p.getX() && n.getY() == p.getY()) {
            return true;
        }

        boolean areCoodinatesLessThan = false;
        if (orientation) {
            areCoodinatesLessThan = p.getX() < n.getX();
        } else {
            areCoodinatesLessThan = p.getY() < n.getY();
        }

        if (areCoodinatesLessThan) {
            return contains(n.getLeft(), p, !orientation);
        } else {
            return contains(n.getRight(), p, !orientation);
        }
    }

    /**
     * begins the recursive call to nearest.
     *
     * @param p the point we are querying about
     * @return the nearest Node
     */
    public Node nearest(Point2D p) {
        if (isEmpty()) {
            return null;
        }

        Node closest = root;
        return nearest(root, closest, p, true);
    }

    /**
     * The recursive part of the nearest function.
     *
     * @param n       the current root.
     * @param closest our current closest node.
     * @param p       the point we are querying about.
     * @return returns the closest node when there are no other candidates.
     */
    private Node nearest(Node n, Node closest, Point2D p, boolean orientation) {
        Node c = closest;

        if (n == null) {
            return c;
        }

        double minDistance = Math.sqrt(p.distance(c.getX(), c.getY()));
        double thisDistance = Math.sqrt(p.distance(n.getX(), n.getY()));

        if(n.getRect().distanceSquaredTo(p) >= minDistance) { return c; }

        if (thisDistance < minDistance) { c = n; }

        boolean areCoordinatesLessThan = false;
        if (orientation) {
            areCoordinatesLessThan = p.getX() < n.getX();
        } else {
            areCoordinatesLessThan = p.getY() < n.getY();
        }

        if (areCoordinatesLessThan) {
            c = nearest(n.getLeft(), c, p, !orientation);
            c = nearest(n.getLeft(), c, p, !orientation);
        } else {
            c = nearest(n.getRight(), c, p, !orientation);
            c = nearest(n.getLeft(), c, p, !orientation);
        }

        return c;
    }

    //taken from Sedgewick and Wayne
    public class RectHV {
        private final float xmin, ymin;
        private final float xmax, ymax;

        public RectHV(float xmin, float ymin, float xmax, float ymax) {
            this.xmin = xmin;
            this.ymin = ymin;
            this.xmax = xmax;
            this.ymax = ymax;
            if (Float.isNaN(xmin) || Float.isNaN(xmax)) {
                throw new IllegalArgumentException("x-coordinate is NaN: " + toString());
            }
            if (Float.isNaN(ymin) || Float.isNaN(ymax)) {
                throw new IllegalArgumentException("y-coordinate is NaN: " + toString());
            }
            if (xmax < xmin) {
                throw new IllegalArgumentException("xmax < xmin: " + toString());
            }
            if (ymax < ymin) {
                throw new IllegalArgumentException("ymax < ymin: " + toString());
            }
        }

        public float getXmin() {
            return xmin;
        }

        public float getYmin() {
            return ymin;
        }

        public float getXmax() {
            return xmax;
        }

        public float getYmax() {
            return ymax;
        }

        public boolean intersects(RectHV that) {
            return this.xmax >= that.xmin && this.ymax >= that.ymin
                    && that.xmax >= this.xmin && that.ymax >= this.ymin;
        }

        public boolean contains(Point2D p) {
            return (p.getX() >= xmin) && (p.getX() <= xmax)
                    && (p.getY() >= ymin) && (p.getY() <= ymax);
        }

        public double distanceSquaredTo(Point2D p) {
            double dx = 0.0, dy = 0.0;
            if      (p.getX() < xmin) dx = p.getX() - xmin;
            else if (p.getX() > xmax) dx = p.getX() - xmax;
            if      (p.getY() < ymin) dy = p.getY() - ymin;
            else if (p.getY() > ymax) dy = p.getY() - ymax;
            return dx*dx + dy*dy;
        }
    }
}
