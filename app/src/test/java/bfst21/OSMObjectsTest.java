/*package bfst21;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OSMObjectsTest {*/
package bfst21;

import static org.junit.jupiter.api.Assertions.assertEquals;
import bfst21.osm.Node;
import bfst21.osm.NodeIndex;
import bfst21.osm.Way;
import org.junit.jupiter.api.Test;

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

            Way mergedTestWay = Way.merge(Way.merge(testWay1,testWay2),testWay3);
            assertEquals(mergedTestWay.first(),testNode1);
            assertEquals(mergedTestWay.last(),testNode6);
        }

        @Test
        public void testAddingNodeToNodeIndex(){
            NodeIndex testNodeIndex = new NodeIndex();
            Node testNode1 = new Node(1000,560 ,123);
            Node testNode2 = new Node(500,600,456);
            Node testNode3 = new Node(600,700,789);
            testNodeIndex.addNode(testNode1);
            testNodeIndex.addNode(testNode2);
            testNodeIndex.addNode(testNode3);
            assertEquals(3,testNodeIndex.size());
        }

        @Test
        public void testGettingNodesFromNodeIndex(){
            NodeIndex testNodeIndex = new NodeIndex();
            Node testNode1 = new Node(1000,560 ,123);
            Node testNode2 = new Node(500,600,456);
            Node testNode3 = new Node(600,700,789);
            testNodeIndex.addNode(testNode1);
            testNodeIndex.addNode(testNode2);
            testNodeIndex.addNode(testNode3);
            assertEquals(testNode1,testNodeIndex.getNode(123));
            assertEquals(testNode2,testNodeIndex.getNode(456));
            assertEquals(testNode3,testNodeIndex.getNode(789));
        }

    }

