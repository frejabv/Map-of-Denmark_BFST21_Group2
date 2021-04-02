package bfst21;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import bfst21.osm.Node;
import bfst21.osm.KDTree;
import bfst21.osm.KDTree.RectHV;

import static org.junit.jupiter.api.Assertions.*;

public class KDTreeTest {
    private final Model model = new Model("data/kdTreeTest.osm", false);

    @Test
    public void testNodeInit(){
        Node testNode = new Node(1,2,3);
        assertEquals(1,testNode.getX());
        assertEquals(2/-0.56f, testNode.getY());
        assertEquals(3, testNode.getId());
        assertNull(testNode.getLeft());
        assertNull(testNode.getRight());
        assertNull(testNode.getRect());
    }

    @Test
    public void testBounds(){
        KDTree kdTree = new KDTree(model);
        assertNull(kdTree.getBounds());
        kdTree.setBounds();
        assertNotNull(kdTree.getBounds());
    }

    @Test
    public void testKDTreeRoot(){
        //assert that root is null, and then set to first node inserted, and not second node.
        KDTree kdTree = model.getKdTree();
        kdTree.setBounds();
        assertTrue(kdTree.isEmpty());
        Node node = new Node(1, 2, 3);
        kdTree.insert(node);
        assertTrue(kdTree.contains(node));
        //assert that the size is correct and that there are no exceptions
        assertEquals(1, kdTree.getSize());
        assertEquals(0, kdTree.IAE3Counter);
        assertEquals(0, kdTree.IAE4Counter);
        assertEquals(0, kdTree.outOfBoundsCounter);
    }

    @Test
    public void testContains(){
        KDTree kdTree = new KDTree(model);
        kdTree.setBounds();

        Node node = new Node(1, 1, 0);
        kdTree.insert(node);
        Node node1 = new Node(2, 2, 1);
        kdTree.insert(node1);
        Node node2 = new Node(100, 100, 2);
        kdTree.insert(node2); //out of bounds
        Node node3 = new Node(2, 4, 3);
        kdTree.insert(node3);
        Node node4 = new Node(6,7,4);
        kdTree.insert(node4);
        Node node5 = new Node(5,5,5); //not in tree

        assertTrue(kdTree.contains(node));
        assertTrue(kdTree.contains(node1));
        assertFalse(kdTree.contains(node2));
        assertTrue(kdTree.contains(node3));
        assertTrue(kdTree.contains(node4));
        assertFalse(kdTree.contains(node5));

        assertEquals(0, kdTree.IAE3Counter);
        assertEquals(0, kdTree.IAE4Counter);
        assertEquals(1, kdTree.outOfBoundsCounter);
    }

    @Test
    public void testInsertPosition(){
        //assert that children are placed correctly
        KDTree kdTree = new KDTree(model);
        kdTree.setBounds();

        Node node = new Node(5, 5, 1);
        kdTree.insert(node);
        Node node1 = new Node(1, 3, 2);
        kdTree.insert(node1); //should be left of root
        Node node2 = new Node(6, 2, 3);
        kdTree.insert(node2); //should be right of root
        Node node3 = new Node(4, 2, 4);
        kdTree.insert(node3); //should be right on left child

        assertEquals(node1, node.getLeft());
        assertEquals(node2, node.getRight());
        assertEquals(node3, node1.getRight());

        assertEquals(0, kdTree.IAE3Counter);
        assertEquals(0, kdTree.IAE4Counter);
        assertEquals(0, kdTree.outOfBoundsCounter);
    }

    @Test
    public void testKDTreeInsertLinkedList(){
        //assert that output is a linked list
        KDTree kdTree = new KDTree(model);
        kdTree.setBounds();
        Node node = new Node(10, 10, 1);
        kdTree.insert(node);
        Node node1 = new Node(9, 1, 2);
        kdTree.insert(node1);
        Node node2 = new Node(8, 2, 3);
        kdTree.insert(node2);
        Node node3 = new Node(7, 3, 4);
        kdTree.insert(node3);

        assertEquals(node1, node.getLeft());
        assertEquals(node2, node1.getLeft());
        assertEquals(node3, node2.getLeft());

        assertEquals(0, kdTree.IAE3Counter);
        assertEquals(0, kdTree.IAE4Counter);
        assertEquals(0, kdTree.outOfBoundsCounter);
    }

    @Test
    public void testInsertNullNode(){
        KDTree kdTree = new KDTree(model);
        kdTree.setBounds();

        boolean success = false;
        try {
            kdTree.insert(null);
        } catch (NullPointerException e){
            success = true;
        }
        assertTrue(success);

        assertEquals(0, kdTree.IAE3Counter);
        assertEquals(0, kdTree.IAE4Counter);
        assertEquals(0, kdTree.outOfBoundsCounter);
    }

    @Test
    public void testRectContains(){
        KDTree kdTree = new KDTree(model);
        kdTree.setBounds();

        Node node = new Node(5, 5, 1);
        kdTree.insert(node); //root
        Node node1 = new Node(3, 3, 2);
        kdTree.insert(node1); // left of root
        Node node2 = new Node(1, 9, 3);
        kdTree.insert(node2); // left of node1
        Node node3 = new Node(3, 6, 4);
        kdTree.insert(node3); // right of node2
        Node node4 = new Node(2, 4, 5);
        kdTree.insert(node4); // right of node3
        Node node5 = new Node(6,7,6);
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

        boolean nullInsertSuccess = false;
        try {
            assertTrue(kdTree.contains(null));
        } catch (NullPointerException e){
            nullInsertSuccess = true;
        }
        assertTrue(nullInsertSuccess);

        assertEquals(0, kdTree.IAE3Counter);
        assertEquals(0, kdTree.IAE4Counter);
        assertEquals(0, kdTree.outOfBoundsCounter);
    }

    @Test
    public void testNearest(){
        KDTree kdTree = new KDTree(model);
        kdTree.setBounds();

        Point2D testEmpty = new Point2D(1,1);
        assertNull(kdTree.nearest(testEmpty));

        Node node = new Node(5, 5, 1);
        kdTree.insert(node);
        Node node1 = new Node(3, 3, 2);
        kdTree.insert(node1);
        Node node2 = new Node(1, 9, 3);
        kdTree.insert(node2);
        Node node3 = new Node(3, 6, 4);
        kdTree.insert(node3);
        Node node4 = new Node(2, 4, 5);
        kdTree.insert(node4);
        Node node5 = new Node(6, 7, 6);
        kdTree.insert(node5);
        Node node6 = new Node(9, 1, 7);
        kdTree.insert(node6);
        Node node7 = new Node(5.1f, 10, 8);
        kdTree.insert(node7);
        Node node8 = new Node(0,0, 9);
        kdTree.insert(node8);
        Node node9 = new Node(1,0,10);
        kdTree.insert(node9);

        //right beside node7, but to the left of root
        Point2D test1 = new Point2D(4.9, 10/-0.56f);
        assertEquals(node7, kdTree.nearest(test1));

        //Node that is equally close to node8 and node9, should return node8
        Point2D test2 = new Point2D(0.5,0);
        assertEquals(node8, kdTree.nearest(test2));

        //out of bounds
        Point2D test3 = new Point2D(-1,-1/-0.56f);
        assertNull(kdTree.nearest(test3));

        //next to node 4
        Point2D test4 = new Point2D(2.7,3.7/-0.56f);
        assertEquals(node4, kdTree.nearest(test4));

        //next to node 6
        Point2D test5 = new Point2D(8,2/-0.56f);
        assertEquals(node6, kdTree.nearest(test5));

        assertEquals(0, kdTree.IAE3Counter);
        assertEquals(0, kdTree.IAE4Counter);
        assertEquals(0, kdTree.outOfBoundsCounter);
    }

    @Test
    public void testRectExceptions(){
        KDTree kdTree = new KDTree(model);
        kdTree.setBounds();

        boolean minXNaN = false;
        boolean maxXNaN = false;
        boolean minYNaN = false;
        boolean maxYNaN = false;

        try {
            RectHV r1 = new RectHV((float) Math.sqrt(-1), 0, 1,1);
        } catch (IllegalArgumentException e) {
            minXNaN = true;
        }
        assertTrue(minXNaN);

        try {
            RectHV r2 = new RectHV(0,0, (float) Math.sqrt(-1),0);
        } catch (IllegalArgumentException e) {
            maxXNaN = true;
        }
        assertTrue(maxXNaN);

        try {
            RectHV r3 = new RectHV(0, (float) Math.sqrt(-1), 1, 1);
        } catch (IllegalArgumentException e) {
            minYNaN = true;
        }
        assertTrue(minYNaN);

        try {
            RectHV r4 = new RectHV(0,0,0, (float) Math.sqrt(-1));
        } catch (IllegalArgumentException e) {
            maxYNaN = true;
        }
        assertTrue(maxYNaN);

        RectHV r5 = new RectHV(1,0,0,1);
        RectHV r6 = new RectHV(0,1,1,0);

        assertEquals(1, kdTree.IAE3Counter);
        assertEquals(1, kdTree.IAE4Counter);
        assertEquals(0, kdTree.outOfBoundsCounter);
    }
}
