package bfst21;

import bfst21.POI.POI;
import bfst21.POI.POI_KDTree;
import bfst21.Rtree.Rectangle;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class POI_KDTreeTest {
    private final Model model;

    public POI_KDTreeTest() throws Exception {
        model = new Model("data/kdTreeTest.osm", false);
    }

    @Test
    public void testPOIInit() {
        POI testPOI = new POI("testPOI", "test", "none", 1, 2);
        assertEquals(1, testPOI.getX());
        assertEquals(2, testPOI.getY());
        assertEquals("test", testPOI.getType());
        assertEquals("none", testPOI.getImageType());
        assertNull(testPOI.getLeft());
        assertNull(testPOI.getRight());
        assertNull(testPOI.getRect());
    }

    @Test
    public void testBounds() {
        POI_KDTree kdTree = new POI_KDTree(model);
        assertNull(kdTree.getBounds());
        kdTree.setBounds();
        assertNotNull(kdTree.getBounds());
        //Bounds are [(0,0) -> (10, 10)]

        POI node = new POI("1", "test", "none", 5, 5 / -Model.scalingConstant);
        kdTree.insert(node);
        POI node1 = new POI("2", "test", "none", 0, 0 / -Model.scalingConstant);
        kdTree.insert(node1);
        POI node2 = new POI("3", "test", "none", 0, 10 / -Model.scalingConstant);
        kdTree.insert(node2);
        POI node3 = new POI("4", "test", "none", 10, 10 / -Model.scalingConstant);
        kdTree.insert(node3);
        POI node4 = new POI("5", "test", "none", 10, 0 / -Model.scalingConstant);
        kdTree.insert(node4);
        assertEquals(5, kdTree.getSize());

        //Test each corner to assert that bounds work
        POI node5 = new POI("6", "test", "none", 0, -1 / -Model.scalingConstant);
        kdTree.insert(node5);
        POI node6 = new POI("7", "test", "none", -1, 0 / -Model.scalingConstant);
        kdTree.insert(node6);
        POI node7 = new POI("8", "test", "none", -1, -1 / -Model.scalingConstant);
        kdTree.insert(node7);
        assertEquals(5, kdTree.getSize());

        POI node8 = new POI("9", "test", "none", 0, 11 / -Model.scalingConstant);
        kdTree.insert(node8);
        POI node9 = new POI("10", "test", "none", -1, 10 / -Model.scalingConstant);
        kdTree.insert(node9);
        POI node10 = new POI("11", "test", "none", -1, 11 / -Model.scalingConstant);
        kdTree.insert(node10);
        assertEquals(5, kdTree.getSize());

        POI node11 = new POI("12", "test", "none", 11, 0 / -Model.scalingConstant);
        kdTree.insert(node11);
        POI node12 = new POI("13", "test", "none", 10, -1 / -Model.scalingConstant);
        kdTree.insert(node12);
        POI node13 = new POI("14", "test", "none", 11, -1 / -Model.scalingConstant);
        kdTree.insert(node13);
        assertEquals(5, kdTree.getSize());

        POI node14 = new POI("15", "test", "none", 10, 11 / -Model.scalingConstant);
        kdTree.insert(node14);
        POI node15 = new POI("16", "test", "none", 11, 10 / -Model.scalingConstant);
        kdTree.insert(node15);
        POI node16 = new POI("17", "test", "none", 11, 11 / -Model.scalingConstant);
        kdTree.insert(node16);
        assertEquals(5, kdTree.getSize());
    }

    @Test
    public void testPOI_KDTreeRoot() {
        //assert that root is null, and then set to first node inserted, and not second node.
        POI_KDTree kdTree = model.getPOITree();
        kdTree.setBounds();
        assertTrue(kdTree.isEmpty());
        POI node = new POI("test node", "test", "none", 1, 2 / -Model.scalingConstant);
        kdTree.insert(node);
        assertTrue(kdTree.contains(node));
        assertEquals(1, kdTree.getSize());
    }

    @Test
    public void testContains() {
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();

        POI node = new POI("1", "test", "none", 1, 1 / -Model.scalingConstant);
        kdTree.insert(node);
        POI node1 = new POI("2", "test", "none", 2, 2 / -Model.scalingConstant);
        kdTree.insert(node1);
        POI node2 = new POI("3", "test", "none", 100, 100 / -Model.scalingConstant);
        kdTree.insert(node2);
        POI node3 = new POI("4", "test", "none", 2, 4 / -Model.scalingConstant);
        kdTree.insert(node3);
        POI node4 = new POI("5", "test", "none", 6, 7 / -Model.scalingConstant);
        kdTree.insert(node4);
        POI node5 = new POI("6", "test", "none", 5, 5 / -Model.scalingConstant);

        assertTrue(kdTree.contains(node));
        assertTrue(kdTree.contains(node1));
        assertTrue(kdTree.contains(node3));
        assertTrue(kdTree.contains(node4));
        assertFalse(kdTree.contains(node2)); //out of bounds
        assertFalse(kdTree.contains(node5)); //not added to tree

        assertEquals(4, kdTree.getSize());
    }

    @Test
    public void testInsertPosition() {
        //assert that children are placed correctly
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();

        POI node = new POI("1", "test", "none", 5, 5 / -Model.scalingConstant);
        kdTree.insert(node);
        POI node1 = new POI("2", "test", "none", 1, 3 / -Model.scalingConstant);
        kdTree.insert(node1); //should be left of root
        POI node2 = new POI("3", "test", "none", 6, 2 / -Model.scalingConstant);
        kdTree.insert(node2); //should be right of root
        POI node3 = new POI("4", "test", "none", 4, 2 / -Model.scalingConstant);
        kdTree.insert(node3); //should be right on left child

        assertEquals(node1, node.getLeft());
        assertEquals(node2, node.getRight());
        assertEquals(node3, node1.getRight());

        assertEquals(4, kdTree.getSize());
    }

    @Test
    public void testPOI_KDTreeInsertLinkedList() {
        //assert that output is a linked list
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();
        POI node = new POI("1", "test", "none", 10, 10 / -Model.scalingConstant);
        kdTree.insert(node);
        POI node1 = new POI("2", "test", "none", 9, 1 / -Model.scalingConstant);
        kdTree.insert(node1);
        POI node2 = new POI("3", "test", "none", 8, 2 / -Model.scalingConstant);
        kdTree.insert(node2);
        POI node3 = new POI("4", "test", "none", 7, 3 / -Model.scalingConstant);
        kdTree.insert(node3);

        assertEquals(node1, node.getLeft());
        assertEquals(node2, node1.getLeft());
        assertEquals(node3, node2.getLeft());

        assertEquals(4, kdTree.getSize());
    }

    @Test
    public void testInsertNullPOI() {
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();

        boolean success = false;
        try {
            kdTree.insert(null);
        } catch (NullPointerException e) {
            success = true;
        }
        assertTrue(success);

        assertEquals(0, kdTree.getSize());
    }

    @Test
    public void testRectContains() {
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();

        POI node = new POI("1", "test", "none", 5, 5 / -Model.scalingConstant);
        kdTree.insert(node); //root
        POI node1 = new POI("2", "test", "none", 3, 3 / -Model.scalingConstant);
        kdTree.insert(node1); // left of root
        POI node2 = new POI("3", "test", "none", 1, 9 / -Model.scalingConstant);
        kdTree.insert(node2); // left of node1
        POI node3 = new POI("4", "test", "none", 3, 6 / -Model.scalingConstant);
        kdTree.insert(node3); // right of node2
        POI node4 = new POI("5", "test", "none", 2, 4 / -Model.scalingConstant);
        kdTree.insert(node4); // right of node3
        POI node5 = new POI("6", "test", "none", 6, 7 / -Model.scalingConstant);
        kdTree.insert(node5); // right of root

        Point2D p1 = new Point2D(2, 5 / -0.56f); //within node4's square
        assertTrue(node.getRect().contains(p1));
        assertTrue(node1.getRect().contains(p1));
        assertTrue(node2.getRect().contains(p1));
        assertTrue(node3.getRect().contains(p1));
        assertTrue(node4.getRect().contains(p1));
        assertFalse(node5.getRect().contains(p1));

        Point2D p2 = new Point2D(2, 8 / -0.56f);
        assertTrue(node.getRect().contains(p2));
        assertTrue(node1.getRect().contains(p2));
        assertTrue(node2.getRect().contains(p2));
        assertTrue(node3.getRect().contains(p2));
        assertFalse(node4.getRect().contains(p2));
        assertFalse(node5.getRect().contains(p2));

        assertEquals(6, kdTree.getSize());
    }

    @Test
    public void testContainsNull() {
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();

        boolean nullContainsSuccess = false;
        try {
            assertTrue(kdTree.contains(null));
        } catch (NullPointerException e) {
            nullContainsSuccess = true;
        }
        assertTrue(nullContainsSuccess);

        assertEquals(0, kdTree.getSize());
    }

    @Test
    public void testNearest() {
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();

        Point2D testEmpty = new Point2D(1, 1);
        assertNull(kdTree.nearest(testEmpty));

        POI node = new POI("1", "test", "none", 5, 5 / -Model.scalingConstant);
        kdTree.insert(node);
        POI node1 = new POI("2", "test", "none", 3, 3 / -Model.scalingConstant);
        kdTree.insert(node1);
        POI node2 = new POI("3", "test", "none", 1, 9 / -Model.scalingConstant);
        kdTree.insert(node2);
        POI node3 = new POI("4", "test", "none", 3, 6 / -Model.scalingConstant);
        kdTree.insert(node3);
        POI node4 = new POI("5", "test", "none", 2, 4 / -Model.scalingConstant);
        kdTree.insert(node4);
        POI node5 = new POI("6", "test", "none", 6, 7 / -Model.scalingConstant);
        kdTree.insert(node5);
        POI node6 = new POI("7", "test", "none", 9, 1 / -Model.scalingConstant);
        kdTree.insert(node6);
        POI node7 = new POI("8", "test", "none", 5.1f, 10 / -Model.scalingConstant);
        kdTree.insert(node7);
        POI node8 = new POI("9", "test", "none", 0, 0 / -Model.scalingConstant);
        kdTree.insert(node8);
        POI node9 = new POI("10", "test", "none", 1, 0 / -Model.scalingConstant);
        kdTree.insert(node9);

        //right beside node7, but to the left of root
        Point2D test1 = new Point2D(4.9, 10 / -Model.scalingConstant);
        assertEquals(node7, kdTree.nearest(test1));

        //POI that is equally close to node8 and node9, should return node8
        Point2D test2 = new Point2D(0.5, 0);
        assertEquals(node8, kdTree.nearest(test2));

        //out of bounds
        Point2D test3 = new Point2D(-1, -1 / -Model.scalingConstant);
        assertNull(kdTree.nearest(test3));

        //next to node 4
        Point2D test4 = new Point2D(2.7, 3.7 / -Model.scalingConstant);
        assertEquals(node4, kdTree.nearest(test4));

        //next to node 6
        Point2D test5 = new Point2D(8, 2 / -Model.scalingConstant);
        assertEquals(node6, kdTree.nearest(test5));

        //between nodes 9 and 10
        Point2D test6 = new Point2D(0.5, 0);
        ArrayList<POI> result = kdTree.nearest(test6, 2);
        assertTrue(result.contains(node8));
        assertTrue(result.contains(node9));

        assertEquals(10, kdTree.getSize());
    }

    @Test
    public void testQuery() {
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();

        POI node = new POI("1", "test", "none", 5, 5 / -Model.scalingConstant);
        kdTree.insert(node); // inside query
        POI node1 = new POI("2", "test", "none", 3, 3 / -Model.scalingConstant);
        kdTree.insert(node1); // inside query
        POI node2 = new POI("3", "test", "none", 1, 9 / -Model.scalingConstant);
        kdTree.insert(node2); // outside query
        POI node3 = new POI("4", "test", "none", 3, 6 / -Model.scalingConstant);
        kdTree.insert(node3); // inside query
        POI node4 = new POI("5", "test", "none", 2, 4 / -Model.scalingConstant);
        kdTree.insert(node4); // inside query
        POI node5 = new POI("6", "test", "none", 6, 7 / -Model.scalingConstant);
        kdTree.insert(node5); //outside query

        ArrayList<POI> expectedResult = new ArrayList<>();

        //test query can get everything in bounds
        Rectangle kdTreeBounds = kdTree.getBounds();
        expectedResult.add(node);
        expectedResult.add(node1);
        expectedResult.add(node2);
        expectedResult.add(node3);
        expectedResult.add(node4);
        expectedResult.add(node5);
        ArrayList<POI> result1 = kdTree.query(kdTreeBounds);
        for (POI p : expectedResult) {
            assertTrue(result1.contains(p));
        }

        //test query can get everything in a smaller viewport
        Rectangle testViewport = new Rectangle(2, 6 / -Model.scalingConstant, 6, 2 / -Model.scalingConstant);
        expectedResult.remove(node5);
        expectedResult.remove(node2);
        ArrayList<POI> result2 = kdTree.query(testViewport);
        for (POI p : expectedResult) {
            assertTrue(result2.contains(p));
        }

        //test that POI's can be added and show up in query
        POI node6 = new POI("7", "test", "none", 4, 4 / -Model.scalingConstant);
        kdTree.insert(node6);
        assertTrue(kdTree.query(testViewport).contains(node6));

        //test for only 1 element
        Rectangle onlyRoot = new Rectangle(4.9f, 5.1f / -Model.scalingConstant, 5.1f, 4.9f / -Model.scalingConstant);
        ArrayList<POI> result3 = kdTree.query(onlyRoot);
        assertEquals(1, result3.size());
        assertTrue(result3.contains(node));
    }

    @Test
    public void testRemoveMethods() {
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();

        POI node = new POI("1", "test", "none", 5, 5 / -Model.scalingConstant);
        kdTree.insert(node);
        POI node1 = new POI("2", "test", "none", 3, 3 / -Model.scalingConstant);
        kdTree.insert(node1);
        POI node2 = new POI("3", "test", "none", 1, 9 / -Model.scalingConstant);
        kdTree.insert(node2); // closest node to testPoint
        POI node3 = new POI("4", "test", "none", 3, 6 / -Model.scalingConstant);
        kdTree.insert(node3);
        POI node4 = new POI("5", "test", "none", 2, 4 / -Model.scalingConstant);
        kdTree.insert(node4);
        POI node5 = new POI("6", "test", "none", 6, 7 / -Model.scalingConstant);
        kdTree.insert(node5); // second closest to testPoint

        Point2D testPoint = new Point2D(1.5, 9.5 / -Model.scalingConstant);
        assertFalse(kdTree.isRemoved(node2));
        assertTrue(kdTree.contains(node2));
        assertEquals(node2, kdTree.nearest(testPoint));
        assertTrue(kdTree.query(kdTree.getBounds()).contains(node2));

        kdTree.remove(node2);
        assertEquals(1, kdTree.getRemovedPOIList().size());
        assertTrue(kdTree.isRemoved(node2));
        assertFalse(kdTree.contains(node2));
        assertEquals(node5, kdTree.nearest(testPoint));
        assertFalse(kdTree.query(kdTree.getBounds()).contains(node2));
    }
}
