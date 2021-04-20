package bfst21;

import bfst21.Rtree.Rectangle;
import bfst21.osm.Node;
import bfst21.osm.Relation;
import bfst21.osm.Way;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RectangleTest {
    public final Model RTreeModel = new Model("data/TEST_RTREE_RECTANGLE.osm", false);
    public final float scalingConstant = -0.56f;

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
        assertEquals(0/scalingConstant, way1234.getRect().getMinY());
        assertEquals(-3/scalingConstant, way1234.getRect().getMaxY());

        Way way4321 = new Way();
        way4321.addNode(n4);
        way4321.addNode(n3);
        way4321.addNode(n2);
        way4321.addNode(n1);
        way4321.createRectangle();
        assertEquals(0, way4321.getRect().getMinX());
        assertEquals(3, way4321.getRect().getMaxX());
        assertEquals(0/scalingConstant, way4321.getRect().getMinY());
        assertEquals(-3/scalingConstant, way4321.getRect().getMaxY());
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
        assertEquals(0/scalingConstant, testRelation.getRect().getMinY());
        assertEquals(-7/scalingConstant, testRelation.getRect().getMaxY());
    }

    @Test
    public void testRectangleFromOSM() {
        Rectangle testRect1234 = new Rectangle(1, 9/-0.56f, 6, 3/-0.56f);
        Rectangle OSMRect1234 = RTreeModel.getWayIndex().getMember(1234).getRect();
        assertEquals(testRect1234.getMinX(), OSMRect1234.getMinX());
        assertEquals(testRect1234.getMinY(), OSMRect1234.getMinY());
        assertEquals(testRect1234.getMaxX(), OSMRect1234.getMaxX());
        assertEquals(testRect1234.getMaxY(), OSMRect1234.getMaxY());

        Rectangle testRect567 = new Rectangle(3, 10/-0.56f, 9, 1/-0.56f);
        Rectangle OSMRect567 = RTreeModel.getWayIndex().getMember(567).getRect();
        assertEquals(testRect567.getMinX(), OSMRect567.getMinX());
        assertEquals(testRect567.getMinY(), OSMRect567.getMinY());
        assertEquals(testRect567.getMaxX(), OSMRect567.getMaxX());
        assertEquals(testRect567.getMaxY(), OSMRect567.getMaxY());

        Rectangle testRect127 = new Rectangle(1, 10/-0.56f, 9, 1/-0.56f);
        Rectangle OSMRect127 = RTreeModel.getRelationIndex().getMember(127).getRect();
        assertEquals(testRect127.getMinX(), OSMRect127.getMinX());
        assertEquals(testRect127.getMinY(), OSMRect127.getMinY());
        assertEquals(testRect127.getMaxX(), OSMRect127.getMaxX());
        assertEquals(testRect127.getMaxY(), OSMRect127.getMaxY());
    }
}
