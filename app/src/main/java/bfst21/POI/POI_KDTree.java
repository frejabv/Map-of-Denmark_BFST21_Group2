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
     * @returns           the currentNode with its' correct parent and left/right child/domain
     */
    private POI insert(POI currentNode, POI parent, POI qNode, boolean orientation) {
        //if space is available, fill space
        if (currentNode == null) {
            Rectangle r = null;

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

        if (!bounds.contains(new Point2D(qNode.getX(),qNode.getY())) || removedPOIList.contains(qNode))
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

    public void remove(POI poi) {
        removedPOIList.add(poi);
    }


    public POI nearest(Point2D p) {
        ArrayList<POI> momentaryList = nearest(p, 1);
        if (momentaryList == null || momentaryList.isEmpty()) {
            return null;
        } else {
            return momentaryList.get(0);
        }
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
        if (!removedPOIList.contains(currentNode)) {
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
        boolean areCoordinatesLessThan = false;
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

    public ArrayList<POI> query(final Rectangle viewport) {
        ArrayList<POI> result = new ArrayList<>();
        result = query(root, viewport, result);

        result.removeAll(removedPOIList);
        return result;
    }

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
