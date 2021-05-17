package bfst21.POI;

import bfst21.Model;
import bfst21.Rtree.Rectangle;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;

public class POI_KDTree {
    Model model;
    private Rectangle bounds;
    private POI root;
    private int size;

    private ArrayList<POI> removedPOIList;

    public POI_KDTree(Model model) {
        this.model = model;
        this.root = null;
        size = 0;
        removedPOIList = new ArrayList<>();
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isRemoved(POI poi) {
        return removedPOIList.contains(poi);
    }

    public ArrayList<POI> getRemovedPOIList() {
        return removedPOIList;
    }

    public void setBounds(){
        //note: maxY and minY are swapped, such that the negative scaling constant has no effect on the tree
        bounds = new Rectangle(model.getMinX(), model.getMaxY(), model.getMaxX(), model.getMinY());
    }

    public Rectangle getBounds(){
        return bounds;
    }

    /**
     * insert query Node into the tree, if it is not null and does not exist in the tree already.
     */
    public void insert(POI qNode) {
        if (qNode == null) {
            throw new NullPointerException("Query Node is null upon insertion into KDTree");
        }
        //insert only if new node is in bounds
        if (bounds.contains(new Point2D(qNode.getX(), qNode.getY()))) {
            //create root if tree is empty
            if (isEmpty()) {
                root = qNode;
                root.setRect(bounds);
                size++;
            } else {
                insert(root, null, qNode, true);
            }
        }
    }

    /**
     * This is a recursive call that inserts a node into the correct empty spot.
     * @param currentNode is the node position we want to try and insert qNode into.
     * @param parent      is the current parent of our element.
     * @param qNode       The node we want to insert: out query Node.
     * @param orientation flips every recursion
     * @return           the currentNode with its' correct parent and left/right child/domain
     */
    private POI insert(POI currentNode, POI parent, POI qNode, boolean orientation) {
        //if space is available, fill space
        if (currentNode == null) {
            Rectangle r;

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
            r = new Rectangle(minX, minY, maxX, maxY);
            size++;
            qNode.setRect(r);
            return qNode;
        }

        //if space is taken, look for another
        boolean areCoordinatesLessThan;
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

    /**
     * This method is primarily used for testing, and recursively moved down the branches of the tree until it has either:
     * a) found the node, and then returns true
     * or b) found a null element, which means the node is not in the tree and the method returning false.
     * @param qNode     the node (POI) we want to find
     * @return          whether or not the node is within the tree
     */
    public boolean contains(POI qNode) {
        if (qNode == null) {
            throw new NullPointerException("null key at KdTree.contains(Point2D p)");
        }

        if (!bounds.contains(new Point2D(qNode.getX(),qNode.getY())) || isRemoved(qNode))
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

        boolean areCoordinatesLessThan;
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
     * The remove method add a POI to the list of removed POis.
     * This is a very fast and simple way of removing things from the POI tree.
     * The removedPOIList is utilized in all other methods, to ensure that the removed POI's are not included in anything.
     * This method therefore effectively creates a 'scar' where the removed POI was, and the POI is still used in moving
     * throughout the tree: if you view KDLines, a removed POI will still have it's line drawn.
     *
     * If there was a need for optimization with this method (of which there is plenty of room for), then the remove
     * method would have to do the following:
     * 1. locate the POI that we want to remove in the tree
     * 2. find the descendant of that POI that come the closest to it's x og y value, depending on which axis it itself
     * has split
     * 3. place the POI found in part 2 at the location where the removed POI was: removing the POI we want to remove
     * in the process
     * 4. mutate all effected POI's in the area such that they now line up with the change:
     * this means that the parent of the new POI must adapt it's rectangle to be bigger, and all descendant of the new
     * POI must change their Rectangles to be smaller.
     * @param poi   the POI of which is to be removed from the tree.
     */
    public void remove(POI poi) {
        removedPOIList.add(poi);
    }


    /**
     * begins the recursive call to nearest.
     * @param p         the point we are querying about
     * @param listSize  size of the created list of POI's
     * @return          the nearest Node
     */
    public ArrayList<POI> nearest(Point2D p, int listSize) {
        if (isEmpty()) {
            return null;
        }

        if (!bounds.contains(p)){
            return new ArrayList<>();
        }

        ArrayList<POI> closestList = new ArrayList<>();
        closestList.add(root);
        root.setDistTo(p);
        return nearest(root, closestList, p, true, listSize);
    }

    /**
     * The recursive part of the nearest function.
     * @param currentNode       the current root.
     * @param currentList       our current list of closest nodes.
     * @param p                 the point we are querying about.
     * @return                  returns the closestList when there are no other candidates in this branch.
     */
    private ArrayList<POI> nearest(POI currentNode, ArrayList<POI> currentList, Point2D p, boolean orientation, int listSize) {
        ArrayList<POI> closestList = currentList;
        POI worstClosest = closestList.get(closestList.size()-1);
        double worstDistance = worstClosest.getDistTo();

        //does the currentNode exist?
        if (currentNode == null) {
            return closestList;
        }
        //is currentNode's rectangle close enough?
        if (currentNode.getRect().distanceSquaredTo(p) >= worstDistance) {
            return closestList;
        }

        //if currentNode is not deleted, is it closer than worstClosest?
        if (!isRemoved(currentNode)) {
            currentNode.setDistTo(p);
            if (closestList.size() < listSize && !closestList.contains(currentNode)) {
                closestList.add(currentNode);
                Collections.sort(closestList);
            } else if (currentNode.getDistTo() < worstDistance) {
                closestList.remove(worstClosest);
                closestList.add(currentNode);
                Collections.sort(closestList);
            }
        }

        //move further down the tree
        boolean areCoordinatesLessThan;
        if (orientation) {
            areCoordinatesLessThan = p.getX() < currentNode.getX();
        } else {
            areCoordinatesLessThan = p.getY() < currentNode.getY();
        }

        if (areCoordinatesLessThan) {
            closestList = nearest(currentNode.getLeft(), closestList, p, !orientation, listSize);
            closestList = nearest(currentNode.getRight(), closestList, p, !orientation, listSize);
        } else {
            closestList = nearest(currentNode.getRight(), closestList, p, !orientation, listSize);
            closestList = nearest(currentNode.getLeft(), closestList, p, !orientation, listSize);
        }

        return closestList;
    }

    /**
     * This nearest method return the single nearest POI, instead of a list.
     */
    public POI nearest(Point2D p) {
        ArrayList<POI> momentaryList = nearest(p, 1);
        if (momentaryList == null || momentaryList.isEmpty()) {
            return null;
        } else {
            return momentaryList.get(0);
        }
    }


    /**
     * this query methods begins the recursive query method.
     * when the recursive query method has finished and returned the list of all POI's within the viewport, we remove
     * all POI's that are removed from the tree, and return the list.
     * @param viewport  the Rectangle of which we want to find all POI's within
     * @return          The list of all POI's within the viewport.
     */
    public ArrayList<POI> query(final Rectangle viewport) {
        ArrayList<POI> result = new ArrayList<>();
        result = query(root, viewport, result);

        result.removeAll(removedPOIList);
        return result;
    }

    /**
     * @param qNode
     * @param viewport
     * @param result
     * @return
     */
    public ArrayList<POI> query(POI qNode, Rectangle viewport, ArrayList<POI> result){
        if (qNode == null) {
            return result;
        }

        if (qNode.getRect().intersects(viewport)) {
            Point2D p = new Point2D(qNode.getX(), qNode.getY());
            if (viewport.contains(p)) {
                result.add(qNode);
            }
            result = query(qNode.getLeft(), viewport, result);
            result = query(qNode.getRight(), viewport, result);
        }

        return result;
    }

    /**
     * DrawLines draws a line from one end of a POI's rectangle to the other, slicing through the POI itself on the path.
     * The line is also a straight line witha constant x or y value, depending on which axis the POI effectively slices.
     * This method recursively iterates through all POIs in the tree (including those outside of the viewport) and
     * draws all of their split lines.
     */
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
}
