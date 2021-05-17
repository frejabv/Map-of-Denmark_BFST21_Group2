package bfst21;

import bfst21.Rtree.Rectangle;
import bfst21.Rtree.Rtree;
import bfst21.Rtree.RtreeLeaf;
import bfst21.osm.Drawable;
import bfst21.osm.Node;
import bfst21.osm.Way;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RTreeTest {
    private final Model model;

    public RTreeTest() throws Exception {
        model = new Model("data/TEST_RTREE_STRUCTURE.osm", false);
    }

    @Test
    public void RTreeLeafTest() {
        ArrayList<Drawable> drawableList = new ArrayList<>();

        RtreeLeaf testLeaf = new RtreeLeaf(drawableList);

        assertEquals(drawableList, testLeaf.getDrawables());
    }

    @Test
    public void RtreeNodeBoundingBox() {
        Node c = new Node(0, 0, 1); //center node

        Node n1 = new Node(-1, -1, 8);
        Node n2 = new Node(-1, 0, 2);
        Node n3 = new Node(-1, 1, 3);
        Node n4 = new Node(0, -1, 4);
        Node n5 = new Node(0, -1, 5);
        Node n6 = new Node(1, -1, 6);
        Node n7 = new Node(1, 0, 7);
        Node n8 = new Node(1, 1, 123);

        Way wayc1 = new Way();
        wayc1.addNode(c);
        wayc1.addNode(n1);
        wayc1.createRectangle();

        Way wayc2 = new Way();
        wayc2.addNode(c);
        wayc2.addNode(n2);
        wayc2.createRectangle();

        Way wayc3 = new Way();
        wayc3.addNode(c);
        wayc3.addNode(n3);
        wayc3.createRectangle();

        Way wayc4 = new Way();
        wayc4.addNode(c);
        wayc4.addNode(n4);
        wayc4.createRectangle();

        Way wayc5 = new Way();
        wayc5.addNode(c);
        wayc5.addNode(n5);
        wayc5.createRectangle();

        Way wayc6 = new Way();
        wayc6.addNode(c);
        wayc6.addNode(n6);
        wayc6.createRectangle();

        Way wayc7 = new Way();
        wayc7.addNode(c);
        wayc7.addNode(n7);
        wayc7.createRectangle();

        Way wayc8 = new Way();
        wayc8.addNode(c);
        wayc8.addNode(n8);
        wayc8.createRectangle();

        ArrayList<Drawable> testAllDescendantList = new ArrayList<>();
        testAllDescendantList.add(wayc1);
        testAllDescendantList.add(wayc2);
        testAllDescendantList.add(wayc3);
        testAllDescendantList.add(wayc4);
        testAllDescendantList.add(wayc5);
        testAllDescendantList.add(wayc6);
        testAllDescendantList.add(wayc7);
        testAllDescendantList.add(wayc8);

        Rtree allWays = new Rtree(testAllDescendantList);

        Rectangle expectedRectAll = new Rectangle(-1, -1 / Model.scalingConstant, 1, 1 / Model.scalingConstant);
        Rectangle testRect = allWays.getRoot().getRect();
        assertEquals(expectedRectAll.getMinX(), testRect.getMinX());
        assertEquals(expectedRectAll.getMinY(), testRect.getMinY());
        assertEquals(expectedRectAll.getMaxX(), testRect.getMaxX());
        assertEquals(expectedRectAll.getMaxY(), testRect.getMaxY());

        ArrayList<Drawable> testAllDescendantListBackwards = new ArrayList<>();
        testAllDescendantListBackwards.add(wayc8);
        testAllDescendantListBackwards.add(wayc7);
        testAllDescendantListBackwards.add(wayc6);
        testAllDescendantListBackwards.add(wayc5);
        testAllDescendantListBackwards.add(wayc4);
        testAllDescendantListBackwards.add(wayc3);
        testAllDescendantListBackwards.add(wayc2);
        testAllDescendantListBackwards.add(wayc1);

        Rtree allWaysBackwards = new Rtree(testAllDescendantList);

        Rectangle expectedRectAllBackwards = new Rectangle(-1, -1 / Model.scalingConstant, 1, 1 / Model.scalingConstant);
        Rectangle testRectBackwards = allWaysBackwards.getRoot().getRect();
        assertEquals(expectedRectAllBackwards.getMinX(), testRectBackwards.getMinX());
        assertEquals(expectedRectAllBackwards.getMinY(), testRectBackwards.getMinY());
        assertEquals(expectedRectAllBackwards.getMaxX(), testRectBackwards.getMaxX());
        assertEquals(expectedRectAllBackwards.getMaxY(), testRectBackwards.getMaxY());
    }

    @Test
    public void testNearestWay() {
        Point2D testPoint = new Point2D(1.5, 1.5 / -Model.scalingConstant);

        assertEquals(54, model.getRoadRTree().nearestWay(testPoint).getId());
    }

    @Test
    public void testGetAllDrawables() {
        ArrayList<Drawable> testList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Way testway = new Way(i);
            testway.addNode(new Node(i, i, i));
            testway.addNode(new Node(-i, -i, -i));
            testway.createRectangle();
            testList.add(testway);
        }

        Rtree testTree = new Rtree(testList);
        List<Drawable> resultList = testTree.getAllDrawables(testTree.getRoot());

        for (Drawable d : testList) {
            assertTrue(resultList.contains(d));
        }
    }

    @Test
    public void testQuery() {
        Rtree testTree = model.getRoadRTree();

        Rectangle bounds = model.getRoadRTree().getRoot().getRect();
        List<Drawable> queryResult = testTree.query(bounds);

        List<Drawable> expectedResult = testTree.getAllDrawables(testTree.getRoot());

        for (Drawable d : queryResult) {
            assertTrue(expectedResult.contains(d));
        }

        Rectangle smallerQueryWindow = new Rectangle(
                0.1f, 1.75f / -Model.scalingConstant,
                3.5f, 1.05f / -Model.scalingConstant);

        queryResult.clear();
        queryResult = testTree.query(smallerQueryWindow);

        //sorting method taken from RtreeNode class. Puts them in order from least to greatest of y-coordinate
        queryResult.sort((a, b) -> {
            float aVal = a.getRect().getMinY();
            float bVal = b.getRect().getMinY();
            return Float.compare(bVal, aVal);
        });

        assertEquals(9, queryResult.size());

        for (int i = 0; i < 9; i++) {
            assertEquals(i + 50, ((Way) queryResult.get(i)).getId());
        }
    }
}
