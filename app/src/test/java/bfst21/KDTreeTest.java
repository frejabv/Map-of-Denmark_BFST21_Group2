package bfst21;

import org.junit.jupiter.api.Test;

import bfst21.osm.Node;
import bfst21.osm.KDTree;
import bfst21.osm.KDTree.RectHV;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

public class KDTreeTest {
    Model model = new Model("data/kdTreeTest.osm", false);

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
    public void testKDTreeRoot(){
        //assert that root is null, and then set to first node inserted, and not second node.
        KDTree kdTree = new KDTree(model);
        assertTrue(kdTree.isEmpty());
        Node node = new Node(1, 2, 3);
        kdTree.insert(node);
        assertTrue(kdTree.contains(node));
        //assert that the size is correct and that there are no exceptions
        //assertEquals(3, kdTree.size);
        assertEquals(0, kdTree.IAE3Counter);
        //assertEquals(0, kdTree.IAE4Counter);
    }

    @Test
    public void testInsertWithContains(){
        //assert that the tree contains
        KDTree kdTree = new KDTree(model);
        Node node = new Node(1, 2, 3);
        kdTree.insert(node);
        Node node1 = new Node(22, 22, 222);
        kdTree.insert(node1);
        assertTrue(kdTree.contains(node));
        assertTrue(kdTree.contains(node1));
        assertEquals(0, kdTree.IAE3Counter);
        //assertEquals(0, kdTree.IAE4Counter);
    }

    @Test
    public void testInsertPosition(){
        //assert that children are placed correctly
        KDTree kdTree = new KDTree(model);
        Node node = new Node(5, 5, 1);
        kdTree.insert(node);
        Node node1 = new Node(1, 2, 2);
        kdTree.insert(node1); //should be left of root
        Node node2 = new Node(6, 3, 3);
        kdTree.insert(node2); //should be right of root
        Node node3 = new Node(1, 5, 4);
        kdTree.insert(node3); //should be right on left child
        assertTrue(kdTree.contains(node));
        assertTrue(kdTree.contains(node1));
        assertTrue(kdTree.contains(node2));
        assertTrue(kdTree.contains(node3));
        assertEquals(0, kdTree.IAE3Counter);
        //assertEquals(0, kdTree.IAE4Counter);
    }

    @Test
    public void testKDTreeInsertLinkedList(){
        //assert that output is a linked list
        KDTree kdTree = new KDTree(model);
        Node node = new Node(10, 10, 1);
        kdTree.insert(node);
        Node node1 = new Node(7, 9, 2);
        kdTree.insert(node1);
        Node node2 = new Node(8, 7, 3);
        kdTree.insert(node2);
        Node node3 = new Node(1, 2, 4);
        kdTree.insert(node3);
        assertTrue(kdTree.contains(node));
        assertTrue(kdTree.contains(node1));
        assertTrue(kdTree.contains(node2));
        assertTrue(kdTree.contains(node3));
        assertEquals(0, kdTree.IAE3Counter);
        //assertEquals(0, kdTree.IAE4Counter);
    }

    @Test
    public void testRectInit(){

    }

    @Test
    public void testChildRect(){

    }

    @Test
    public void testRectContains(){

    }

    @Test
    public void testRectDistTo(){

    }

    @Test
    public void testNearest(){
        Model model = new Model("data/kdTreeTest.osm",false);
        KDTree kdTree = new KDTree(model);
    }


}
