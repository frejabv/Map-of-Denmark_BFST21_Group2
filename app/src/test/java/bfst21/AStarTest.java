package bfst21;

import bfst21.osm.Node;
import bfst21.pathfinding.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AStarTest {
    private Model model;

    @BeforeEach
    public void setUp() throws Exception {
        model = new Model("/bfst21/data/dk-AstarTest.osm", false);
        model.setUpAStar();
    }

    @Test
    public void testCreateAStar() {
        Vertex testVertex = model.getAStar().getVertex(11);
        assertEquals(2, testVertex.getAdjacencies().size());
        testVertex = model.getAStar().getVertex(2);
        assertEquals(3, testVertex.getAdjacencies().size());
    }

    @Test
    public void testAdjecencies() {
        //for Odense, has Korsør and Vejle as adjecencies
        Vertex testVertex = model.getAStar().getVertex(11);
        assertEquals(2, testVertex.getAdjacencies().size());
        assertEquals(model.getAStar().getVertex(12), testVertex.getAdjacencies().get(0).getTarget());
        assertEquals(model.getAStar().getVertex(8), testVertex.getAdjacencies().get(1).getTarget());
    }

    @Test
    public void testPathDescription() {
        //route from Skagen to Randers
        AStar astar = model.getAStar();
        Node skagen = model.getNodeIndex().getMember(1);
        Node aalborg = model.getNodeIndex().getMember(2);
        Node randers = model.getNodeIndex().getMember(5);
        astar.AStarSearch(skagen, randers, model.getCurrentTransportType());
        ArrayList<Step> steps = astar.getPathDescription();

        double deltaX = Math.abs(skagen.getX() - aalborg.getX());
        double deltaY = Math.abs(skagen.getY() - aalborg.getY());
        double distance1 = (Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2))) * 111.320 * Model.scalingConstant;

        deltaX = Math.abs(aalborg.getX() - randers.getX());
        deltaY = Math.abs(aalborg.getY() - randers.getY());
        double distance2 = (Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2))) * 111.320 * Model.scalingConstant;

        double totalDistance = Math.round((distance1 + distance2) * 10.0) / 10.0;

        assertEquals("Arrived at Skagenvej", steps.get(1).toString());
        assertEquals("Distance: " + totalDistance + " km", astar.getTotalDistance());
    }

    @Test
    public void testDriveable() {
        //Odense to Korsør is a primary highway
        Vertex testVertex = model.getAStar().getVertex(11);
        assertEquals(true, testVertex.getAdjacencies().get(0).isDriveable());
        assertEquals(false, testVertex.getAdjacencies().get(0).isWalkable());
    }

    @Test
    public void testCycleRoute() {
        //from Århus to Viborg there is a direct cycleway
        AStar astar = model.getAStar();
        Node århus = model.getNodeIndex().getMember(7);
        Node viborg = model.getNodeIndex().getMember(4);

        astar.AStarSearch(århus, viborg, model.getCurrentTransportType());
        ArrayList<Step> steps = astar.getPathDescription();
        assertEquals(3, steps.size());

        model.setCurrentTransportType(TransportType.BICYCLE);
        astar.AStarSearch(model.getNodeIndex().getMember(7), model.getNodeIndex().getMember(4), model.getCurrentTransportType());
        steps = astar.getPathDescription();
        assertEquals(2, steps.size());
    }

    @Test
    public void testWalkRoute() {
        //from Roskilde to Helsingør there is a footway
        AStar astar = model.getAStar();
        Node roskilde = model.getNodeIndex().getMember(13);
        Node helsingør = model.getNodeIndex().getMember(15);

        model.setCurrentTransportType(TransportType.WALK);
        astar.AStarSearch(helsingør, roskilde, model.getCurrentTransportType());
        ArrayList<Step> steps = astar.getPathDescription();

        assertEquals(3, steps.size());
        assertEquals("unknown road", steps.get(0).getRoadName());

        //there is no walk route to korsør
        Node korsør = model.getNodeIndex().getMember(12);
        astar.AStarSearch(helsingør, korsør, model.getCurrentTransportType());
        steps = astar.getPathDescription();
        assertEquals("No path was found", steps.get(0).toString());
    }

    @Test
    public void testDistance() {
        //distance from Odense to Korsør
        Vertex testVertex = model.getAStar().getVertex(11);
        Vertex destination = model.getAStar().getVertex(12);

        float deltaX = Math.abs(testVertex.getX() - destination.getX());
        float deltaY = Math.abs(testVertex.getY() - destination.getY());
        float step = (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        float distance = step * 111.320f * Model.scalingConstant;

        assertEquals(distance, testVertex.getAdjacencies().get(0).getWeight());
    }

    @Test
    public void testTotalDistance() {
        //distance from Odense to Korsør
        AStar astar = model.getAStar();
        Node testNode = model.getNodeIndex().getMember(11);
        Node destination = model.getNodeIndex().getMember(12);

        double deltaX = Math.abs(testNode.getX() - destination.getX());
        double deltaY = Math.abs(testNode.getY() - destination.getY());
        double distance = (Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2))) * 111.320 * Model.scalingConstant;
        double totalDistance = Math.round(distance * 10.0) / 10.0;

        astar.AStarSearch(testNode, destination, TransportType.CAR);
        astar.getPathDescription();
        assertEquals("Distance: " + totalDistance + " km", astar.getTotalDistance());
    }

    @Test
    public void testTotalTime() {
        //distance from Odense to Korsør
        AStar astar = model.getAStar();
        Vertex testVertex = model.getAStar().getVertex(11);
        Vertex destination = model.getAStar().getVertex(12);

        double deltaX = Math.abs(testVertex.getX() - destination.getX());
        double deltaY = Math.abs(testVertex.getY() - destination.getY());
        double distance = (Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2))) * 111.320 * Model.scalingConstant;
        double totalDistance = Math.round(distance * 10.0) / 10.0;
        long wayID = 0;
        for (Edge e : testVertex.getAdjacencies()) {
            if (e.getTarget() == destination) {
                wayID = e.getWayID();
            }
        }
        double totalTime = totalDistance / model.getWayIndex().getMember(wayID).getSpeed();
        int time = (int) (totalTime * 60);
        int minutes = time % 60;
        int hours = time / 60;

        astar.AStarSearch(model.getNodeIndex().getMember(testVertex.getId()), model.getNodeIndex().getMember(destination.getId()), TransportType.CAR);
        astar.getPathDescription();
        assertEquals("Estimated Time: " + hours + " hours and " + minutes + " min", astar.getTotalTime());
    }

    @Test
    public void testRoundaboutExitDescription() throws Exception {
        Model modelRoundabout = null;
        modelRoundabout = new Model("/bfst21/data/roundabout-simple.osm", false);

        modelRoundabout.setUpAStar();
        AStar astar = modelRoundabout.getAStar();
        Node testNode = modelRoundabout.getNodeIndex().getMember(12);
        Node destination = modelRoundabout.getNodeIndex().getMember(1);

        astar.AStarSearch(testNode, destination, TransportType.CAR);
        assertEquals("Take the 3rd exit in the roundabout and follow unknown road for 24932km", astar.getPathDescription().get(2).toString());
    }

    @Test
    public void testRoundaboutExitType() throws Exception {
        Model modelRoundabout = null;
        modelRoundabout = new Model("/bfst21/data/roundabout-simple.osm", false);

        modelRoundabout.setUpAStar();
        AStar astar = modelRoundabout.getAStar();
        Node testNode = modelRoundabout.getNodeIndex().getMember(12);
        Node destination = modelRoundabout.getNodeIndex().getMember(1);

        astar.AStarSearch(testNode, destination, TransportType.CAR);
        assertEquals("roundabout_other_exit", astar.getPathDescription().get(2).getDirection().toString().toLowerCase());
    }
}
