package bfst21;

import bfst21.Rtree.Rectangle;
import bfst21.Rtree.Rtree;
import bfst21.Rtree.RtreeNode;
import bfst21.osm.Drawable;
import bfst21.osm.Node;
import bfst21.osm.Relation;
import bfst21.osm.Way;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class RectangleTest {

    @Test
    public void WayRectangleTest() {
        Node n1 = new Node(0, -0, 1); //minX and minY
        Node n2 = new Node(1,-1,2);
        Node n3 = new Node(2, -2, 3);
        Node n4 = new Node(3,-3,4); //maxX and maxY
        Way way1234 = new Way();
        way1234.addNode(n1);
        way1234.addNode(n2);
        way1234.addNode(n3);
        way1234.addNode(n4);
        way1234.createRectangle();

        assertEquals(0, way1234.getRect().getMinX());
        assertEquals(3, way1234.getRect().getMaxX());
        assertEquals(0/-Model.scalingConstant, way1234.getRect().getMinY());
        assertEquals(-3/-Model.scalingConstant, way1234.getRect().getMaxY());

        Way way4321 = new Way();
        way4321.addNode(n4);
        way4321.addNode(n3);
        way4321.addNode(n2);
        way4321.addNode(n1);
        way4321.createRectangle();
        assertEquals(0, way4321.getRect().getMinX());
        assertEquals(3, way4321.getRect().getMaxX());
        assertEquals(0/-Model.scalingConstant, way4321.getRect().getMinY());
        assertEquals(-3/-Model.scalingConstant, way4321.getRect().getMaxY());
    }

    @Test
    public void RelationRectangleTest() {
        Node n1 = new Node(0, -0, 1); //minX and minY
        Node n2 = new Node(1, -1, 2);
        Node n3 = new Node(2, -2, 3);
        Node n4 = new Node(3, -3, 4);
        Node n5 = new Node(4, -4, 5);
        Node n6 = new Node(5, -5, 6);
        Node n7 = new Node(6, -6, 7);
        Node n8 = new Node(7, -7, 8); //maxX and maxY
        Way way1234 = new Way();
        way1234.addNode(n1);
        way1234.addNode(n2);
        way1234.addNode(n3);
        way1234.addNode(n4);
        way1234.createRectangle();
        Way way5678 = new Way();
        way5678.addNode(n5);
        way5678.addNode(n6);
        way5678.addNode(n7);
        way5678.addNode(n8);
        way5678.createRectangle();

        Relation testRelation = new Relation(1);
        testRelation.addWay(way1234);
        testRelation.addWay(way5678);
        testRelation.createRectangle();
        assertEquals(0, testRelation.getRect().getMinX());
        assertEquals(7, testRelation.getRect().getMaxX());
        assertEquals(0/-Model.scalingConstant, testRelation.getRect().getMinY());
        assertEquals(-7/-Model.scalingConstant, testRelation.getRect().getMaxY());
    }

    @Test
    public void testRectangleIntersects(){
        Rectangle testBigRect = new Rectangle(-3,-3,3,3);
        Rectangle testSmallRect = new Rectangle(-1,-1,1,1);

        Rectangle r1 = new Rectangle(-4,-4,-2,-2);
        Rectangle r2 = new Rectangle(-1,-4, 1,-2);
        Rectangle r3 = new Rectangle(2,-4,4,-2);
        Rectangle r4 = new Rectangle(-4,-1,-2,1);
        Rectangle r5 = new Rectangle(2,-1,4,1);
        Rectangle r6 = new Rectangle(-4,2,-2,4);
        Rectangle r7 = new Rectangle(-1,2,1,4);
        Rectangle r8 = new Rectangle(2,2,4,4);


        assertTrue(testBigRect.intersects(r1));
        assertTrue(testBigRect.intersects(r2));
        assertTrue(testBigRect.intersects(r3));
        assertTrue(testBigRect.intersects(r4));
        assertTrue(testBigRect.intersects(r5));
        assertTrue(testBigRect.intersects(r6));
        assertTrue(testBigRect.intersects(r7));
        assertTrue(testBigRect.intersects(r8));

        assertFalse(testSmallRect.intersects(r1));
        assertFalse(testSmallRect.intersects(r2));
        assertFalse(testSmallRect.intersects(r3));
        assertFalse(testSmallRect.intersects(r4));
        assertFalse(testSmallRect.intersects(r5));
        assertFalse(testSmallRect.intersects(r6));
        assertFalse(testSmallRect.intersects(r7));
        assertFalse(testSmallRect.intersects(r8));

        assertTrue(testBigRect.intersects(testSmallRect));
        assertTrue(testSmallRect.intersects(testBigRect));

        assertTrue(testBigRect.intersects(testBigRect));
        assertFalse(r1.intersects(null));
    }

    @Test
    public void testDistanceTo(){
        Rectangle testRect = new Rectangle(1,1,3,3);

        Point2D p1 = new Point2D(0,0);
        Point2D p2 = new Point2D(2,0);
        Point2D p3 = new Point2D(4,0);
        Point2D p4 = new Point2D(0,2);
        Point2D p5 = new Point2D(2,2);
        Point2D p6 = new Point2D(4,2);
        Point2D p7 = new Point2D(0,4);
        Point2D p8 = new Point2D(2,4);
        Point2D p9 = new Point2D(4,4);

        assertEquals(2, testRect.distanceSquaredTo(p1), 0.0001);
        assertEquals(1, testRect.distanceSquaredTo(p2), 0.0001);
        assertEquals(2, testRect.distanceSquaredTo(p3), 0.0001);
        assertEquals(1, testRect.distanceSquaredTo(p4), 0.0001);
        assertEquals(0, testRect.distanceSquaredTo(p5));
        assertEquals(1, testRect.distanceSquaredTo(p6), 0.0001);
        assertEquals(2, testRect.distanceSquaredTo(p7), 0.0001);
        assertEquals(1, testRect.distanceSquaredTo(p8), 0.0001);
        assertEquals(2, testRect.distanceSquaredTo(p9), 0.0001);
    }
    
    @Test
    public void testContainsPoint() {
        Rectangle testBigRect = new Rectangle(-3,-3,3,3);
        Rectangle testSmallRect = new Rectangle(-1,-1,1,1);

        //3x3 grid from (-2,-2) to (2,2)
        Point2D p1 = new Point2D(-2,-2);
        Point2D p2 = new Point2D(-2,0);
        Point2D p3 = new Point2D(-2,2);

        Point2D p4 = new Point2D(0,-2);
        Point2D p5 = new Point2D(0,0);
        Point2D p6 = new Point2D(0,2);

        Point2D p7 = new Point2D(2,-2);
        Point2D p8 = new Point2D(2,0);
        Point2D p9 = new Point2D(2,2);

        assertTrue(testBigRect.contains(p1));
        assertTrue(testBigRect.contains(p2));
        assertTrue(testBigRect.contains(p3));
        assertTrue(testBigRect.contains(p4));
        assertTrue(testBigRect.contains(p5));
        assertTrue(testBigRect.contains(p6));
        assertTrue(testBigRect.contains(p7));
        assertTrue(testBigRect.contains(p8));
        assertTrue(testBigRect.contains(p9));

        assertFalse(testSmallRect.contains(p1));
        assertFalse(testSmallRect.contains(p2));
        assertFalse(testSmallRect.contains(p3));
        assertFalse(testSmallRect.contains(p4));
        assertTrue(testSmallRect.contains(p5));
        assertFalse(testSmallRect.contains(p6));
        assertFalse(testSmallRect.contains(p7));
        assertFalse(testSmallRect.contains(p8));
        assertFalse(testSmallRect.contains(p9));

        assertFalse(testSmallRect.contains((Point2D) null));
    }

    @Test
    public void testContainsRect() {
        Rectangle testBigRect = new Rectangle(-3,-3,3,3);
        Rectangle testMediumRect = new Rectangle(-2,-2,2,2);
        Rectangle testSmallRect = new Rectangle(-1,-1,1,1);

        assertTrue(testSmallRect.contains(testSmallRect));
        assertFalse(testSmallRect.contains(testMediumRect));
        assertFalse(testSmallRect.contains(testBigRect));

        assertTrue(testMediumRect.contains(testSmallRect));
        assertTrue(testMediumRect.contains(testMediumRect));
        assertFalse(testMediumRect.contains(testBigRect));

        assertTrue(testBigRect.contains(testSmallRect));
        assertTrue(testBigRect.contains(testMediumRect));
        assertTrue(testBigRect.contains(testBigRect));

        Rectangle r1 = new Rectangle(-4,-4,-2,-2);
        Rectangle r2 = new Rectangle(-1,-4, 1,-2);
        Rectangle r3 = new Rectangle(2,-4,4,-2);
        Rectangle r4 = new Rectangle(-4,-1,-2,1);
        Rectangle r5 = new Rectangle(2,-1,4,1);
        Rectangle r6 = new Rectangle(-4,2,-2,4);
        Rectangle r7 = new Rectangle(-1,2,1,4);
        Rectangle r8 = new Rectangle(2,2,4,4);

        assertFalse(testBigRect.contains(r1));
        assertFalse(testBigRect.contains(r2));
        assertFalse(testBigRect.contains(r3));
        assertFalse(testBigRect.contains(r4));
        assertFalse(testBigRect.contains(r5));
        assertFalse(testBigRect.contains(r6));
        assertFalse(testBigRect.contains(r7));
        assertFalse(testBigRect.contains(r8));

        assertFalse(testSmallRect.contains(r1));
        assertFalse(testSmallRect.contains(r2));
        assertFalse(testSmallRect.contains(r3));
        assertFalse(testSmallRect.contains(r4));
        assertFalse(testSmallRect.contains(r5));
        assertFalse(testSmallRect.contains(r6));
        assertFalse(testSmallRect.contains(r7));
        assertFalse(testSmallRect.contains(r8));

        assertFalse(testSmallRect.contains((Rectangle) null));
    }

    @Test
    public void testGetArea() {
        Rectangle testRect1 = new Rectangle(0, 0, 2, 2);
        assertEquals(4, testRect1.getArea());

        Rectangle testRect2 = new Rectangle(0, 0, 5, 5);
        assertEquals(25, testRect2.getArea());
    }
}
