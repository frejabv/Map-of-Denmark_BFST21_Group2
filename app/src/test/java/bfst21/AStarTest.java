package bfst21;

import bfst21.osm.Node;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bfst21.pathfinding.AStar;
import bfst21.pathfinding.Step;
import bfst21.pathfinding.TransportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class AStarTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new Model("dk-AstarTest.osm",false);
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
    public void testPathDescription() {
        //route from Skagen to Randers
        AStar astar = model.getAStar();
        Node skagen = model.getNodeIndex().getMember(1);
        Node aalborg = model.getNodeIndex().getMember(2);
        Node randers = model.getNodeIndex().getMember(5);
        astar.AStarSearch(skagen, randers, model.getCurrentTransportType());
        astar.createPath(randers);
        ArrayList<Step> steps = astar.getPathDescription();

        double deltaX = Math.abs(skagen.getX() - aalborg.getX());
        double deltaY = Math.abs(skagen.getY() - aalborg.getY());
        double distance1 = (Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2))) * 111.320  * Model.scalingConstant;

        deltaX = Math.abs(aalborg.getX() - randers.getX());
        deltaY = Math.abs(aalborg.getY() - randers.getY());
        double distance2 = (Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2))) * 111.320  * Model.scalingConstant;

        double totalDistance = Math.round((distance1 + distance2) * 10.0) / 10.0;

        assertEquals("Arrived at Skagenvej", steps.get(1).toString());
        assertEquals("Distance: " + totalDistance + " km", astar.getTotalDistance());
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
        Node århus = model.getNodeIndex().getMember(7);
        Node viborg = model.getNodeIndex().getMember(4);

        astar.AStarSearch(århus,viborg, model.getCurrentTransportType());
        astar.createPath(viborg);
        ArrayList<Step> steps = astar.getPathDescription();
        assertEquals(3, steps.size());

        model.setCurrentTransportType(TransportType.BICYCLE);
        astar.AStarSearch(model.getNodeIndex().getMember(7),model.getNodeIndex().getMember(4), model.getCurrentTransportType());
        astar.createPath(viborg);
        steps = astar.getPathDescription();
        assertEquals(2, steps.size());
    }

    @Test
    public void testDistance() {
        //distance from Odense to Korsør
        Node testNode = model.getNodeIndex().getMember(11);
        Node destination = model.getNodeIndex().getMember(12);

        double deltaX = Math.abs(testNode.getX() - destination.getX());
        double deltaY = Math.abs(testNode.getY() - destination.getY());
        double distance = (Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2))) * 111.320 * Model.scalingConstant;

        assertEquals(distance, testNode.getAdjecencies().get(0).weight);
    }

    @Test
    public void testTotalDistance() {
        //distance from Odense to Korsør
        AStar astar = model.getAStar();
        Node testNode = model.getNodeIndex().getMember(11);
        Node destination = model.getNodeIndex().getMember(12);

        double deltaX = Math.abs(testNode.getX() - destination.getX());
        double deltaY = Math.abs(testNode.getY() - destination.getY());
        double distance = (Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2))) * 111.320  * Model.scalingConstant;
        double totalDistance = Math.round(distance * 10.0) / 10.0;

        astar.AStarSearch(testNode, destination, TransportType.CAR);
        astar.getPathDescription();
        assertEquals("Distance: " + totalDistance + " km", astar.getTotalDistance());
    }
}
