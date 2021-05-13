package bfst21;

import bfst21.POI.POI;
import bfst21.POI.POI_KDTree;
import bfst21.Rtree.Rectangle;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KDTreeTest {
    private final Model model = new Model("data/kdTreeTest.osm", false);

    @Test
    public void testPOIInit(){
        POI testPOI = new POI("tesPOI", "test", 1, 2);
        assertEquals(1,testPOI.getX());
        assertEquals(2, testPOI.getY());
        assertEquals("test", testPOI.getType());
        assertNull(testPOI.getLeft());
        assertNull(testPOI.getRight());
        assertNull(testPOI.getRect());
    }

    @Test
    public void testBounds(){
        POI_KDTree kdTree = new POI_KDTree(model);
        assertNull(kdTree.getBounds());
        kdTree.setBounds();
        assertNotNull(kdTree.getBounds());
        //Bounds are [(0,0) -> (10, 10)]

        POI node = new POI("1", "test", 5, 5 / -Model.scalingConstant);
        kdTree.insert(node);
        POI node1 = new POI("2", "test", 0, 0 / -Model.scalingConstant);
        kdTree.insert(node1);
        POI node2 = new POI("3", "test",0, 10 / -Model.scalingConstant);
        kdTree.insert(node2);
        POI node3 = new POI("4", "test",10, 10 / -Model.scalingConstant);
        kdTree.insert(node3);
        POI node4 = new POI("5", "test",10, 0 / -Model.scalingConstant);
        kdTree.insert(node4);
        assertEquals(5, kdTree.getSize());

        //Test each corner to assert that bounds work
        POI node5 = new POI("6", "test",0, -1  / -Model.scalingConstant);
        kdTree.insert(node5);
        POI node6 = new POI("7", "test", -1, 0  / -Model.scalingConstant);
        kdTree.insert(node6);
        POI node7 = new POI("8", "test", -1, -1  / -Model.scalingConstant);
        kdTree.insert(node7);
        assertEquals(5, kdTree.getSize());

        POI node8 = new POI("9", "test", 0, 11 / -Model.scalingConstant);
        kdTree.insert(node8);
        POI node9 = new POI("10", "test", -1, 10 / -Model.scalingConstant);
        kdTree.insert(node9);
        POI node10 = new POI("11", "test", -1, 11 / -Model.scalingConstant);
        kdTree.insert(node10);
        assertEquals(5, kdTree.getSize());

        POI node11 = new POI("12", "test", 11, 0 / -Model.scalingConstant);
        kdTree.insert(node11);
        POI node12 = new POI("13", "test", 10, -1 / -Model.scalingConstant);
        kdTree.insert(node12);
        POI node13 = new POI("14", "test", 11, -1 / -Model.scalingConstant);
        kdTree.insert(node13);
        assertEquals(5, kdTree.getSize());

        POI node14 = new POI("15", "test", 10, 11 / -Model.scalingConstant);
        kdTree.insert(node14);
        POI node15 = new POI("16", "test", 11, 10 / -Model.scalingConstant);
        kdTree.insert(node15);
        POI node16 = new POI("17", "test", 11, 11 / -Model.scalingConstant);
        kdTree.insert(node16);
        assertEquals(5, kdTree.getSize());
    }

    @Test
    public void testPOI_KDTreeRoot(){
        //assert that root is null, and then set to first node inserted, and not second node.
        POI_KDTree kdTree = model.getPOITree();
        kdTree.setBounds();
        assertTrue(kdTree.isEmpty());
        POI node = new POI("test node", "test", 1, 2 / -Model.scalingConstant);
        kdTree.insert(node);
        assertTrue(kdTree.contains(node));
        assertEquals(1, kdTree.getSize());
    }

    @Test
    public void testContains(){
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();

        POI node = new POI("1", "test", 1, 1 / -Model.scalingConstant);
        kdTree.insert(node);
        POI node1 = new POI("2", "test", 2, 2 / -Model.scalingConstant);
        kdTree.insert(node1);
        POI node2 = new POI("3", "test", 100, 100 / -Model.scalingConstant);
        kdTree.insert(node2);
        POI node3 = new POI("4", "test", 2, 4 / -Model.scalingConstant);
        kdTree.insert(node3);
        POI node4 = new POI("5", "test", 6, 7 / -Model.scalingConstant);
        kdTree.insert(node4);
        POI node5 = new POI("6", "test", 5, 5 / -Model.scalingConstant);

        assertTrue(kdTree.contains(node));
        assertTrue(kdTree.contains(node1));
        assertTrue(kdTree.contains(node3));
        assertTrue(kdTree.contains(node4));
        assertFalse(kdTree.contains(node2)); //out of bounds
        assertFalse(kdTree.contains(node5)); //not added to tree

        assertEquals(4, kdTree.getSize());
    }

    @Test
    public void testInsertPosition(){
        //assert that children are placed correctly
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();

        POI node = new POI("1", "test", 5, 5 / -Model.scalingConstant);
        kdTree.insert(node);
        POI node1 = new POI("2", "test", 1, 3 / -Model.scalingConstant);
        kdTree.insert(node1); //should be left of root
        POI node2 = new POI("3", "test", 6, 2 / -Model.scalingConstant);
        kdTree.insert(node2); //should be right of root
        POI node3 = new POI("4", "test", 4, 2 / -Model.scalingConstant);
        kdTree.insert(node3); //should be right on left child

        assertEquals(node1, node.getLeft());
        assertEquals(node2, node.getRight());
        assertEquals(node3, node1.getRight());

        assertEquals(4, kdTree.getSize());
    }

    @Test
    public void testPOI_KDTreeInsertLinkedList(){
        //assert that output is a linked list
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();
        POI node = new POI("1", "test", 10, 10 / -Model.scalingConstant);
        kdTree.insert(node);
        POI node1 = new POI("2", "test", 9, 1 / -Model.scalingConstant);
        kdTree.insert(node1);
        POI node2 = new POI("3", "test", 8, 2 / -Model.scalingConstant);
        kdTree.insert(node2);
        POI node3 = new POI("4", "test", 7, 3 / -Model.scalingConstant);
        kdTree.insert(node3);

        assertEquals(node1, node.getLeft());
        assertEquals(node2, node1.getLeft());
        assertEquals(node3, node2.getLeft());

        assertEquals(4, kdTree.getSize());
    }

    @Test
    public void testInsertNullPOI(){
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();

        boolean success = false;
        try {
            kdTree.insert(null);
        } catch (NullPointerException e){
            success = true;
        }
        assertTrue(success);

        assertEquals(0, kdTree.getSize());
    }

    @Test
    public void testRectContains(){
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();

        POI node = new POI("1", "test", 5, 5 / -Model.scalingConstant);
        kdTree.insert(node); //root
        POI node1 = new POI("2", "test", 3, 3 / -Model.scalingConstant);
        kdTree.insert(node1); // left of root
        POI node2 = new POI("3", "test", 1, 9 / -Model.scalingConstant);
        kdTree.insert(node2); // left of node1
        POI node3 = new POI("4", "test", 3, 6 / -Model.scalingConstant);
        kdTree.insert(node3); // right of node2
        POI node4 = new POI("5", "test", 2, 4 / -Model.scalingConstant);
        kdTree.insert(node4); // right of node3
        POI node5 = new POI("6", "test", 6, 7 / -Model.scalingConstant);
        kdTree.insert(node5); // right of root

        Point2D p1 = new Point2D(2,5/-0.56f); //within node4's square
        assertTrue(node.getRect().contains(p1));
        assertTrue(node1.getRect().contains(p1));
        assertTrue(node2.getRect().contains(p1));
        assertTrue(node3.getRect().contains(p1));
        assertTrue(node4.getRect().contains(p1));
        assertFalse(node5.getRect().contains(p1));

        Point2D p2 = new Point2D(2,8/-0.56f);
        assertTrue(node.getRect().contains(p2));
        assertTrue(node1.getRect().contains(p2));
        assertTrue(node2.getRect().contains(p2));
        assertTrue(node3.getRect().contains(p2));
        assertFalse(node4.getRect().contains(p2));
        assertFalse(node5.getRect().contains(p2));

        assertEquals(6, kdTree.getSize());
    }

    @Test
    public void testContainsNull(){
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();

        boolean nullContainsSuccess = false;
        try {
            assertTrue(kdTree.contains(null));
        } catch (NullPointerException e){
            nullContainsSuccess = true;
        }
        assertTrue(nullContainsSuccess);

        assertEquals(0, kdTree.getSize());
    }

    @Test
    public void testNearest(){
        POI_KDTree kdTree = new POI_KDTree(model);
        kdTree.setBounds();

        Point2D testEmpty = new Point2D(1,1);
        assertNull(kdTree.nearest(testEmpty));

        POI node = new POI("1", "test", 5, 5 / -Model.scalingConstant);
        kdTree.insert(node);
        POI node1 = new POI("2", "test", 3, 3 / -Model.scalingConstant);
        kdTree.insert(node1);
        POI node2 = new POI("3", "test", 1, 9 / -Model.scalingConstant);
        kdTree.insert(node2);
        POI node3 = new POI("4", "test", 3, 6 / -Model.scalingConstant);
        kdTree.insert(node3);
        POI node4 = new POI("5", "test", 2, 4 / -Model.scalingConstant);
        kdTree.insert(node4);
        POI node5 = new POI("6", "test", 6, 7 / -Model.scalingConstant);
        kdTree.insert(node5);
        POI node6 = new POI("7", "test", 9, 1 / -Model.scalingConstant);
        kdTree.insert(node6);
        POI node7 = new POI("8", "test", 5.1f, 10 / -Model.scalingConstant);
        kdTree.insert(node7);
        POI node8 = new POI("9", "test", 0, 0 / -Model.scalingConstant);
        kdTree.insert(node8);
        POI node9 = new POI("10", "test", 1, 0 / -Model.scalingConstant);
        kdTree.insert(node9);

        //right beside node7, but to the left of root
        Point2D test1 = new Point2D(4.9, 10 / -Model.scalingConstant);
        assertEquals(node7, kdTree.nearest(test1));

        //POI that is equally close to node8 and node9, should return node8
        Point2D test2 = new Point2D(0.5,0);
        assertEquals(node8, kdTree.nearest(test2));

        //out of bounds
        Point2D test3 = new Point2D(-1,-1 / -Model.scalingConstant);
        assertNull(kdTree.nearest(test3));

        //next to node 4
        Point2D test4 = new Point2D(2.7,3.7 / -Model.scalingConstant);
        assertEquals(node4, kdTree.nearest(test4));

        //next to node 6
        Point2D test5 = new Point2D(8,2 / -Model.scalingConstant);
        assertEquals(node6, kdTree.nearest(test5));

        assertEquals(10, kdTree.getSize());
    }
}
