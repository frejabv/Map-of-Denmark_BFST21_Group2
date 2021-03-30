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
        assertEquals(2, testNode.getY());
        assertEquals(3, testNode.getId());
        assertNull(testNode.getLeft());
        assertNull(testNode.getRight());
        assertNull(testNode.getRect());
    }

    @Test
    public void testKDTreeRoot(){
        //assert that root is null, and then set to first node inserted, and not second node.

        //assert that the size is correct and that there are no exceptions
        assertEquals(3, kdTree.size);
        assertEquals(0, kdTree.IAE3Counter);
        assertEquals(0, kdTree.IAE4Counter);

    }

    @Test
    public void testInsertWithContains(){
        //assert that the tree contains
        assertTrue();
    }

    @Test
    public void testInsertPosition(){
        //assert that children are placed correctly
    }

    @Test
    public void testKDTreeInsertLinkedList(){
        //assert that output is a linked list
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
