package bfst21;

import bfst21.osm.*;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OSMObjectsTest {
    @Test
    public void testNewNode() {
        Node testNode1 = new Node(1000, 560, 123);
        assertEquals(1000, testNode1.getX());
        assertEquals(-1000, testNode1.getY());
        assertEquals(123, testNode1.getId());
    }

    @Test
    public void testAddingNodeToWay() {
        Way testWay1 = new Way(1);
        Node testNode1 = new Node(100, 200, 300);
        Node testNode2 = new Node(200, 300, 400);
        testWay1.addNode(testNode1);
        testWay1.addNode(testNode2);
        assertEquals(testNode1, testWay1.first());
        assertEquals(testNode2, testWay1.last());
    }

    @Test
    public void testMerging2Ways() {
        Way testWay1 = new Way(1);
        Node testNode1 = new Node(100, 200, 300);
        Node testNode2 = new Node(200, 300, 400);
        testWay1.addNode(testNode1);
        testWay1.addNode(testNode2);
        Way testWay2 = new Way(1);
        Node testNode3 = new Node(300, 400, 500);
        Node testNode4 = new Node(400, 500, 600);
        testWay2.addNode(testNode3);
        testWay2.addNode(testNode4);
        Way mergedTestWay = Way.merge(testWay1, testWay2);
        assertEquals(mergedTestWay.first(), testNode1);
        assertEquals(mergedTestWay.last(), testNode4);
    }

    @Test
    public void testMerging3Ways() {
        Way testWay1 = new Way(1);
        Node testNode1 = new Node(100, 200, 300);
        Node testNode2 = new Node(200, 300, 400);
        testWay1.addNode(testNode1);
        testWay1.addNode(testNode2);

        Way testWay2 = new Way(1);
        Node testNode3 = new Node(300, 400, 500);
        Node testNode4 = new Node(400, 500, 600);
        testWay2.addNode(testNode3);
        testWay2.addNode(testNode4);

        Way testWay3 = new Way(1);
        Node testNode5 = new Node(500, 600, 700);
        Node testNode6 = new Node(600, 700, 800);
        testWay3.addNode(testNode5);
        testWay3.addNode(testNode6);

        Way mergedTestWay = Way.merge(Way.merge(testWay1, testWay2), testWay3);
        assertEquals(mergedTestWay.first(), testNode1);
        assertEquals(mergedTestWay.last(), testNode6);
    }

    //Consider renaming to MemberIndex
    @Test
    public void testAddingNodeToNodeIndex() {
        MemberIndex<Node> testNodeIndex = new MemberIndex<>();
        Node testNode1 = new Node(1000, 560, 123);
        Node testNode2 = new Node(500, 600, 456);
        Node testNode3 = new Node(600, 700, 789);
        testNodeIndex.addMember(testNode1);
        testNodeIndex.addMember(testNode2);
        testNodeIndex.addMember(testNode3);
        assertEquals(3, testNodeIndex.size());
    }

    //Consider renaming to MemberIndex
    @Test
    public void testGettingNodesFromNodeIndex() {
        MemberIndex<Node> testNodeIndex = new MemberIndex<>();
        Node testNode1 = new Node(1000, 560, 123);
        Node testNode2 = new Node(500, 600, 456);
        Node testNode3 = new Node(600, 700, 789);
        testNodeIndex.addMember(testNode1);
        testNodeIndex.addMember(testNode2);
        testNodeIndex.addMember(testNode3);
        assertEquals(testNode1, testNodeIndex.getMember(123));
        assertEquals(testNode2, testNodeIndex.getMember(456));
        assertEquals(testNode3, testNodeIndex.getMember(789));
    }

    @Test
    public void testNewRelation() {
        Relation testRelation = new Relation(123);
        assertEquals(123, testRelation.getId());
    }

    @Test
    public void testWayRoles() {
        Relation testRelation1 = new Relation(111);
        Relation testRelation2 = new Relation(222);
        Way testWay = new Way(123);
        testWay.addRole(111, "inner");
        testWay.addRole(222, "outer");
        testRelation1.addWay(testWay);
        testRelation2.addWay(testWay);
        HashMap<Long, String> roles = testWay.getRoleMap();
        assertEquals("inner", roles.get(Long.valueOf(111)));
        assertEquals("outer", roles.get(Long.valueOf(222)));
    }

    @Test
    public void testMergingOuterWaysInRelation() {
        Relation testRelation = new Relation(222);

        Way testWay1 = new Way(1);
        testWay1.addRole(222, "inner");
        Node testNode1 = new Node(100, 200, 300);
        Node testNode2 = new Node(200, 300, 400);
        testWay1.addNode(testNode1);
        testWay1.addNode(testNode2);

        Node testNode3 = new Node(200, 300, 500);
        Node testNode4 = new Node(300, 400, 600);
        Node testNode5 = new Node(300, 400, 700);

        Way testWay2 = new Way(2);
        testWay2.addRole(222, "outer");
        testWay2.addNode(testNode3);
        testWay2.addNode(testNode4);

        Way testWay3 = new Way(3);
        testWay3.addRole(222, "outer");
        testWay3.addNode(testNode4);
        testWay3.addNode(testNode5);

        Way testWay4 = new Way(4);
        testWay4.addRole(222, "outer");
        testWay4.addNode(testNode5);
        testWay4.addNode(testNode3);

        testRelation.addWay(testWay1);
        testRelation.addWay(testWay2);
        testRelation.addWay(testWay3);
        testRelation.addWay(testWay4);

        HashMap<Long, String> roles = testWay1.getRoleMap();
        assertEquals("inner", roles.get(222l));

        ArrayList<Way> outers = new ArrayList<>();
        outers.add(testWay2);
        outers.add(testWay3);
        outers.add(testWay4);

        ArrayList<Way> mergedList = testRelation.mergeOuter(outers);

        assertEquals(1, mergedList.size());

        Way mergedTestWay = mergedList.get(0);
        assertEquals(mergedTestWay.first(), testNode3);
        assertEquals(mergedTestWay.last(), testNode3);
    }

    @Test
    public void testMemberIndex() {
        MemberIndex<Member> testMemberIndex = new MemberIndex<>();
        Relation testRelation = new Relation(123);
        Node testNode = new Node(10, 10, 456);
        Way testWay = new Way(789);
        testMemberIndex.addMember(testRelation);
        testMemberIndex.addMember(testNode);
        testMemberIndex.addMember(testWay);
        assertEquals(3, testMemberIndex.size());
        assertEquals(testRelation, testMemberIndex.getMember(123));
        assertEquals(testNode, testMemberIndex.getMember(456));
        assertEquals(testWay, testMemberIndex.getMember(789));
    }

    @Test
    public void testRenderingStyleGetDrawStyle() {
        RenderingStyle testStyle = new RenderingStyle();
        assertEquals(DrawStyle.FILL, testStyle.getDrawStyleByTag(Tag.BUILDING));
        assertEquals(DrawStyle.STROKE, testStyle.getDrawStyleByTag(Tag.PRIMARY));
    }

    @Test
    public void testNodeDistanceToSquared() {
        Node testNode = new Node(1, 5, 1);
        Point2D testPoint = new Point2D(4, 2 / -Model.scalingConstant);
        double expectedDistance = ((1 - 4) * (1 - 4)) + ((5 - 2) / -Model.scalingConstant * (5 - 2) / -Model.scalingConstant);
        assertEquals(expectedDistance, testNode.distanceToSquared(testPoint), 0.00001);
    }

    @Test
    public void testNearestNodeInWay() {
        Way testWay = new Way(1);
        Node n0 = new Node(1, 1, 1);
        Node n1 = new Node(2, 2, 2);
        Node n2 = new Node(3, 3, 3);
        Node n3 = new Node(4, 4, 4);
        testWay.addNode(n0);
        testWay.addNode(n1);
        testWay.addNode(n2);
        testWay.addNode(n3);
        testWay.createRectangle();

        Point2D p0 = new Point2D(0, 0);
        Point2D p1 = new Point2D(5, 5 / -Model.scalingConstant);

        assertEquals(n0, testWay.nearestNode(p0));
        assertEquals(n3, testWay.nearestNode(p1));
    }

    @Test
    public void testWayMinimumDistanceToSquared() {
        Way testWay = new Way(1);
        Node n0 = new Node(1, 1, 1);
        Node n1 = new Node(2, 2, 2);
        Node n2 = new Node(3, 3, 3);
        Node n3 = new Node(4, 4, 4);
        testWay.addNode(n0);
        testWay.addNode(n1);
        testWay.addNode(n2);
        testWay.addNode(n3);
        testWay.createRectangle();

        Point2D p0 = new Point2D(0, 0);
        Point2D p1 = new Point2D(4, 2 / -Model.scalingConstant);
        Point2D p2 = new Point2D(2, -7);

        //calculated with GeoGebra
        double p0Dist = 2.046649825985;
        double p1Dist = 1.745012031899;
        double p2Dist = 1.675211550623;

        assertEquals(p0Dist, testWay.minimumDistanceToSquared(p0), 0.0000001);
        assertEquals(p1Dist, testWay.minimumDistanceToSquared(p1), 0.0000001);
        assertEquals(p2Dist, testWay.minimumDistanceToSquared(p2), 0.0000001);
    }
}
