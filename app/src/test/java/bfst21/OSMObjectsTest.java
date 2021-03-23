package bfst21;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bfst21.osm.*;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

public class OSMObjectsTest {
        @Test
        public void testNewNode(){
            Node testNode1 = new Node(1000,560 ,123);
            assertEquals(1000,testNode1.getX());
            assertEquals(-1000,testNode1.getY());
            assertEquals(123,testNode1.getId());
        }
        @Test
        public void testAddingNodeToWay(){
            Way testWay1 = new Way(1);
            Node testNode1 = new Node(100,200,300);
            Node testNode2 = new Node(200,300,400);
            testWay1.addNode(testNode1);
            testWay1.addNode(testNode2);
            assertEquals(testNode1,testWay1.first());
            assertEquals(testNode2,testWay1.last());
        }
        @Test
        public void testMerging2Ways(){
            Way testWay1 = new Way(1);
            Node testNode1 = new Node(100,200,300);
            Node testNode2 = new Node(200,300,400);
            testWay1.addNode(testNode1);
            testWay1.addNode(testNode2);
            Way testWay2 = new Way(1);
            Node testNode3 = new Node(300,400,500);
            Node testNode4 = new Node(400,500,600);
            testWay2.addNode(testNode3);
            testWay2.addNode(testNode4);
            Way mergedTestWay = Way.merge(testWay1,testWay2);
            assertEquals(mergedTestWay.first(),testNode1);
            assertEquals(mergedTestWay.last(),testNode4);
        }

        @Test
        public void testMerging3Ways(){
            Way testWay1 = new Way(1);
            Node testNode1 = new Node(100,200,300);
            Node testNode2 = new Node(200,300,400);
            testWay1.addNode(testNode1);
            testWay1.addNode(testNode2);

            Way testWay2 = new Way(1);
            Node testNode3 = new Node(300,400,500);
            Node testNode4 = new Node(400,500,600);
            testWay2.addNode(testNode3);
            testWay2.addNode(testNode4);

            Way testWay3 = new Way(1);
            Node testNode5 = new Node(500,600,700);
            Node testNode6 = new Node(600,700,800);
            testWay3.addNode(testNode5);
            testWay3.addNode(testNode6);

        Way mergedTestWay = Way.merge(Way.merge(testWay1, testWay2), testWay3);
        assertEquals(mergedTestWay.first(), testNode1);
        assertEquals(mergedTestWay.last(), testNode6);
    }

    //Consider renaming to MemberIndex
    @Test
    public void testAddingNodeToNodeIndex() {
        MemberIndex testNodeIndex = new MemberIndex();
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
        MemberIndex testNodeIndex = new MemberIndex();
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
    public void testAddMemberToRelation() {
        Relation testRelation1 = new Relation(123);
        Relation testRelation2 = new Relation(321);
        Node testNode = new Node(10,10,456);
        Way testWay = new Way(789);
        testRelation1.addMember(testNode);
        testRelation1.addMember(testWay);
        testRelation1.addMember(testRelation2);
        List<Member> members = testRelation1.getMembers();
        assertEquals(testNode, members.get(0));
        assertEquals(testWay, members.get(1));
        assertEquals(testRelation2, members.get(2));
    }

    @Test
    public void testMemberRoles() {
            Relation testRelation1 = new Relation(111);
            Relation testRelation2 = new Relation(222);
            Member testNode = new Node(10, 10, 123);
            testNode.addRole(111, "inner");
            testNode.addRole(222, "outer");
            testRelation1.addMember(testNode);
            testRelation2.addMember(testNode);
            HashMap<Long, String> roles = testNode.getRoleMap();
            assertEquals("inner", roles.get(Long.valueOf(111)));
            assertEquals("outer", roles.get(Long.valueOf(222)));
    }

    @Test
    public void testMemberIndex() {
        MemberIndex testMemberIndex = new MemberIndex();
        Relation testRelation = new Relation(123);
        Node testNode = new Node(10,10,456);
        Way testWay = new Way(789);
        testMemberIndex.addMember(testRelation);
        testMemberIndex.addMember(testNode);
        testMemberIndex.addMember(testWay);
        assertEquals(3,testMemberIndex.size());
        assertEquals(testRelation,testMemberIndex.getMember(123));
        assertEquals(testNode,testMemberIndex.getMember(456));
        assertEquals(testWay,testMemberIndex.getMember(789));
    }

    @Test
    public void testRenderingStyleGetColor() {
            RenderingStyle testStyle = new RenderingStyle();
            testStyle.darkMode();
            assertEquals(Color.LIGHTBLUE, testStyle.getColorByTag(Tag.WATER));
            assertEquals(Color.rgb(128, 142, 155), testStyle.getColorByTag(Tag.PRIMARY));
            testStyle.defaultMode();
            assertEquals(Color.LIGHTBLUE, testStyle.getColorByTag(Tag.WATER));
            assertEquals(Color.WHITE, testStyle.getColorByTag(Tag.PRIMARY));
    }

    @Test
    public void testRenderingStyleGetDrawStyle() {
        RenderingStyle testStyle = new RenderingStyle();
        assertEquals(DrawStyle.FILL, testStyle.getDrawStyleByTag(Tag.BUILDING));
        assertEquals(DrawStyle.STROKE, testStyle.getDrawStyleByTag(Tag.PRIMARY));
    }

    @Test
    public void testColorModes() {
        RenderingStyle testStyle = new RenderingStyle();
        testStyle.deuteranopeColorMode();
        testStyle.tritanopeColorMode();
        testStyle.protanopeColorMode();
        assertEquals(Color.LIGHTBLUE, testStyle.getColorByTag(Tag.WATER));
    }

}
