package bfst21;

import bfst21.osm.Node;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bfst21.pathfinding.AStar;
import bfst21.pathfinding.TransportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AStarTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new Model("data/dk-AstarTest.osm",false);
        model.setUpAStar();
    }
    
    @Test
    public void testCreateAStar() {
        Node testNode = model.getNodeIndex().getMember(11);
        assertEquals(2,testNode.getAdjecencies().size());
        testNode = model.getNodeIndex().getMember(2);
        assertEquals(3,testNode.getAdjecencies().size());
    }

    @Test
    public void testAdjecencies() {
        //for Odense, has Korsør and Vejle as adjecencies
        Node testNode = model.getNodeIndex().getMember(11);
        assertEquals(2,testNode.getAdjecencies().size());
        assertEquals(model.getNodeIndex().getMember(12),testNode.getAdjecencies().get(0).target);
        assertEquals(model.getNodeIndex().getMember(8),testNode.getAdjecencies().get(1).target);
    }

    @Test
    public void testPrintPath() {
        //route from Skagen to Randers
        AStar astar = model.getAStar();
        astar.AStarSearch(model.getNodeIndex().getMember(1),model.getNodeIndex().getMember(5), model.getCurrentTransportType());
        assertEquals("1 -> 2 -> 5 -> ", astar.printPath(model.getNodeIndex().getMember(5)));
    }

    @Test
    public void testDriveable() {
        //Odense to Korsør is a primary highway
        Node testNode = model.getNodeIndex().getMember(11);
        assertEquals(true,testNode.getAdjecencies().get(0).isDriveable());
        assertEquals(false,testNode.getAdjecencies().get(0).isWalkable());
    }

    @Test
    public void testCycleRoute() {
        //from Århus to Viborg there is a direct cycleway
        AStar astar = model.getAStar();

        astar.AStarSearch(model.getNodeIndex().getMember(7),model.getNodeIndex().getMember(4), model.getCurrentTransportType());
        assertEquals("7 -> 5 -> 4 -> ", astar.printPath(model.getNodeIndex().getMember(4)));

        model.setCurrentTransportType(TransportType.BICYCLE);
        astar.AStarSearch(model.getNodeIndex().getMember(7),model.getNodeIndex().getMember(4), model.getCurrentTransportType());
        assertEquals("7 -> 4 -> ", astar.printPath(model.getNodeIndex().getMember(4)));
    }

    @Test
    public void testDistance() {
        //distance from Odense to Korsør
        AStar astar = model.getAStar();
        Node testNode = model.getNodeIndex().getMember(11);
        Node destination = model.getNodeIndex().getMember(12);

        double deltaX = Math.abs(testNode.getX() - destination.getX());
        double deltaY = Math.abs(testNode.getY() - destination.getY());
        double distance = (Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2))) * 111.320;

        assertEquals(distance, testNode.getAdjecencies().get(0).weight);
    }
}
