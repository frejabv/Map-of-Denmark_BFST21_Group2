package bfst21.osm;

import bfst21.Model;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import org.w3c.dom.css.Rect;

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
     * Add query Node to the tree, if it is not null and does not exist in the tree already.
     */
    public void insert(Node qNode) {
        if (qNode == null) {
            throw new NullPointerException("Point2D is null upon insertion into KDTree");
        }

        //create root
        if (isEmpty()) {
            RectHV r = new RectHV(model.getMinX(), model.getMinY(), model.getMaxX(), model.getMaxY());
            root = qNode;
            root.setRect(r);
            size++;
        } else {
            insert(root, null, qNode, true);
        }
    }

    /**
     * This is a recursive call that inserts a node into the correct empty spot.
     *
     * @param n           is the node position we want to try and insert p into.
     * @param parent      is the current parent of out element.
     * @param qNode       The node we want to insert: out query Node.
     * @param orientation flips every recursion
     * @returns           the Node at with its correct parent and left/right rectangle/domain
     */
    private Node insert(Node n, Node parent, Node qNode, boolean orientation) {
        if (n == null) {
            RectHV r = null;

            float xmin = parent.getRect().getMinX();
            float ymin = parent.getRect().getMinY();
            float xmax = parent.getRect().getMaxX();
            float ymax = parent.getRect().getMaxY();

            if (orientation) {
                if (qNode.getY() < parent.getY()) {
                    ymax = parent.getY();
                } else {
                    ymin = parent.getY();
                }
            } else {
                if (qNode.getX() < parent.getX()) {
                    xmax = parent.getX();
                } else {
                    xmin = parent.getX();
                }
            }
            r = new RectHV(xmin, ymin, xmax, ymax);
            size++;
            qNode.setRect(r);
            return qNode;
        }

        boolean areCoodinatesLessThan = false;
        if (orientation) {
            areCoodinatesLessThan = qNode.getX() < n.getX();
        } else {
            areCoodinatesLessThan = qNode.getY() < n.getY();
        }

        if (areCoodinatesLessThan) {
            n.setLeft(insert(n.getLeft(), n, qNode, !orientation));
        } else {
            n.setRight(insert(n.getRight(), n, qNode, !orientation));
        }

        return n;
    }


    public boolean contains(Node qNode) {
        if (qNode == null) {
            throw new NullPointerException("null key at KdTree.contians(Point2D p)");
        }
        return contains(root, qNode, true);
    }

    private boolean contains(Node n, Node qNode, boolean orientation) {
        if (n == null) {
            return false;
        }
        if (n.getId() == qNode.getId()) {
            return true;
        }

        boolean areCoodinatesLessThan = false;
        if (orientation) {
            areCoodinatesLessThan = qNode.getX() < n.getX();
        } else {
            areCoodinatesLessThan = qNode.getY() < n.getY();
        }

        if (areCoodinatesLessThan) {
            return contains(n.getLeft(), qNode, !orientation);
        } else {
            return contains(n.getRight(), qNode, !orientation);
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

        if (n.getRect().distanceSquaredTo(p) >= minDistance) {
            return c;
        }

        if (thisDistance < minDistance) {
            c = n;
        }

        boolean areCoordinatesLessThan = false;
        if (orientation) {
            areCoordinatesLessThan = p.getX() < n.getX();
        } else {
            areCoordinatesLessThan = p.getY() < n.getY();
        }

        if (areCoordinatesLessThan) {
            c = nearest(n.getLeft(), c, p, !orientation);
            c = nearest(n.getRight(), c, p, !orientation);
        } else {
            c = nearest(n.getRight(), c, p, !orientation);
            c = nearest(n.getLeft(), c, p, !orientation);
        }

        return c;
    }

    public void drawLines(GraphicsContext gc) {
        if (!isEmpty()) {
            root.drawKDTLine(true, gc);
            if (root.getRight() != null) {
                drawLines(root.getRight(), gc, false);
            }
            if (root.getLeft() != null) {
                drawLines(root.getLeft(), gc, false);
            }
        }
    }

    private void drawLines(Node n, GraphicsContext gc, boolean orientation) {
        n.drawKDTLine(orientation, gc);
        if (n.getRight() != null) {
            drawLines(n.getRight(), gc, !orientation);
        }
        if (n.getLeft() != null) {
            drawLines(n.getLeft(), gc, !orientation);
        }
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
                System.out.println("if 1 returned true: x coordinate NaN");
                throw new IllegalArgumentException("x-coordinate is NaN: " + toString());
            }
            if (Float.isNaN(ymin) || Float.isNaN(ymax)) {
                System.out.println("if 2 returned true: y coordinate is NaN");
                throw new IllegalArgumentException("y-coordinate is NaN: " + toString());
            }
            if (xmax < xmin) {
                System.out.println("if 3 returned true");
                System.out.println(xmax + " < " + xmin + " is " + (xmax < xmin));
                System.out.println("ILLEGAL ARGUMENT EXCEPTION");
                //throw new IllegalArgumentException("xmax < xmin: " + toString());
            }
            if (ymax < ymin) {
                System.out.println("if 4 returned true");
                System.out.println(ymax + " < " + ymin + " is " + (ymax < ymin));
                //throw new IllegalArgumentException("ymax < ymin: " + toString());
                System.out.println("ILLEGAL ARGUMENT EXCEPTION");
            }
        }

        public float getMinX() {
            return xmin;
        }

        public float getMinY() {
            return ymin;
        }

        public float getMaxX() {
            return xmax;
        }

        public float getMaxY() {
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
            if (p.getX() < xmin) dx = p.getX() - xmin;
            else if (p.getX() > xmax) dx = p.getX() - xmax;
            if (p.getY() < ymin) dy = p.getY() - ymin;
            else if (p.getY() > ymax) dy = p.getY() - ymax;
            return dx * dx + dy * dy;
        }
    }
}
