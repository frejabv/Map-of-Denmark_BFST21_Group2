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
    public void testInsertWithContains(){
        //assert that the tree contains
        KDTree kdTree = new KDTree(model);
        kdTree.setBounds();

        Node node = new Node(1, 1, 3);
        kdTree.insert(node);
        Node node1 = new Node(2, 2, 222);
        kdTree.insert(node1);
        Node node2 = new Node(100, 100, 0);
        kdTree.insert(node2); //out of bounds

        assertTrue(kdTree.contains(node));
        assertTrue(kdTree.contains(node1));
        assertFalse(kdTree.contains(node2));

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
        assertEquals(node2, node1.getLeft());  //this fails, because it is right
        assertEquals(node3, node2.getLeft());

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
    }

    //TODO
    @Test
    public void testNearest(){
        KDTree kdTree = new KDTree(model);
        kdTree.setBounds();

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

        Point2D test1 = new Point2D(4.9, 10/-0.56);
        assertEquals(node7, kdTree.nearest(test1));
    }

    @Test
    public void testKDTreeBounds(){

    }

}
