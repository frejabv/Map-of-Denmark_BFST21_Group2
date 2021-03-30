package bfst21;

import org.junit.jupiter.api.Test;

import bfst21.osm.Node;
import bfst21.osm.KDTree;
import bfst21.osm.KDTree.RectHV;

import static org.junit.jupiter.api.Assertions.*;

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
    public void testBounds(){
        KDTree kdTree = new KDTree(model);
        assertNull(kdTree.getBounds());
        kdTree.setBounds();
        assertNotNull(kdTree.getBounds());
    }

    @Test
    public void testKDTreeRoot(){
        //assert that root is null, and then set to first node inserted, and not second node.
        KDTree kdTree = new KDTree(model);
        kdTree.setBounds();
        assertTrue(kdTree.isEmpty());
        Node node = new Node(1, 2, 3);
        kdTree.insert(node);
        assertTrue(kdTree.contains(node));
        //assert that the size is correct and that there are no exceptions
        assertEquals(1, kdTree.getSize());
        assertEquals(0, kdTree.IAE3Counter);
        assertEquals(0, kdTree.IAE4Counter);
    }

    @Test
    public void testInsertWithContains(){
        //assert that the tree contains
        KDTree kdTree = new KDTree(model);
        kdTree.setBounds();
        Node node = new Node(1, 2, 3);
        kdTree.insert(node);
        Node node1 = new Node(22, 22, 222);
        kdTree.insert(node1);
        assertTrue(kdTree.contains(node));
        assertTrue(kdTree.contains(node1));
        assertEquals(0, kdTree.IAE3Counter);
        assertEquals(0, kdTree.IAE4Counter);
    }

    @Test
    public void testInsertPosition(){
        //assert that children are placed correctly
        KDTree kdTree = new KDTree(model);
        kdTree.setBounds();
        Node node = new Node(5, 5, 1);
        kdTree.insert(node);
        Node node1 = new Node(1, 2, 2);
        kdTree.insert(node1); //should be left of root
        Node node2 = new Node(6, 3, 3);
        kdTree.insert(node2); //should be right of root
        Node node3 = new Node(1, 4, 4); // THIS SHIT FAILS??? making y smaller means it goes right?
        kdTree.insert(node3); //should be right on left child

        System.out.println(node1.getLeft() + " " + node1.getRight());
        assertEquals(node1, node.getLeft());
        assertEquals(node2, node.getRight());
        assertEquals(node3, node1.getRight()); //this fails because it is left

        assertEquals(0, kdTree.IAE3Counter);
        assertEquals(0, kdTree.IAE4Counter);
    }

    @Test
    public void testKDTreeInsertLinkedList(){
        //assert that output is a linked list
        KDTree kdTree = new KDTree(model);
        kdTree.setBounds();
        Node node = new Node(10, 10, 1);
        kdTree.insert(node);
        Node node1 = new Node(7, 9, 2);
        kdTree.insert(node1);
        Node node2 = new Node(8, 7, 3);
        kdTree.insert(node2);
        Node node3 = new Node(1, 2, 4);
        kdTree.insert(node3);

        System.out.println(node1.getLeft() + " " + node1.getRight());
        assertEquals(node1, node.getLeft());
        assertEquals(node2, node1.getLeft());  //this fails, because it is right
        assertEquals(node3, node2.getLeft());

        assertEquals(0, kdTree.IAE3Counter);
        assertEquals(0, kdTree.IAE4Counter);
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
        KDTree kdTree = new KDTree(model);
        kdTree.setBounds();
    }


}
