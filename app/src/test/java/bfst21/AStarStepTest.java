package bfst21;

import bfst21.pathfinding.Direction;
import bfst21.pathfinding.Step;
import org.junit.jupiter.api.Test;

import javax.annotation.concurrent.ThreadSafe;

import static org.junit.jupiter.api.Assertions.*;


public class AStarStepTest {

    @Test
    public void testPathInit() {
        Step testPath = new Step(Direction.CONTINUE, "test", 2);
        assertEquals(Direction.CONTINUE, testPath.getDirection());
        assertEquals("test", testPath.getRoadName());
        assertEquals(2, testPath.getDistance());
    }

    @Test
    public void testSetters() {
        Step testPath = new Step(Direction.CONTINUE, "test", 2);
        testPath.setDirection(Direction.ARRIVAL);
        assertEquals(Direction.ARRIVAL, testPath.getDirection());
        testPath.setRoadName("success");
        assertEquals("success", testPath.getRoadName());
        testPath.setDistance(3);
        assertEquals(3, testPath.getDistance());
    }

    @Test
    public void testToString() {
        Step testPath = new Step(Direction.RIGHT, "test", 1);
        //nt is the "not testing" part of the string
        String nt = "test for 1km";
        assertEquals("Turn right and follow " + nt, testPath.toString());

        testPath.setDirection(Direction.LEFT);
        assertEquals("Turn left and follow " + nt, testPath.toString());

        testPath.setDirection(Direction.CONTINUE);
        assertEquals("Continue ahead on " + nt, testPath.toString());

        testPath.setDirection(Direction.ROUNDABOUT_FIRST_EXIT);
        assertEquals("Take the 1st exit in the roundabout and follow " + nt, testPath.toString());

        testPath.setDirection(Direction.ROUNDABOUT_SECOND_EXIT);
        assertEquals("Take the 2nd exit in the roundabout and follow " + nt, testPath.toString());

        testPath.setExits(3);
        assertEquals("Take the 3rd exit in the roundabout and follow " + nt , testPath.toString());

        testPath.setExits(4);
        assertEquals("Take the 4th exit in the roundabout and follow " + nt, testPath.toString());

        testPath.setDirection(Direction.FOLLOW);
        assertEquals("Follow " + nt, testPath.toString());

        testPath.setDirection(Direction.ARRIVAL);
        assertEquals("Arrived at test", testPath.toString());

        testPath.setDirection(Direction.NO_PATH);
        assertEquals("No path was found", testPath.toString());
    }

    @Test
    public void testMetric() {
        Step testPath = new Step(Direction.RIGHT, "test", 1);
        //nt is the "not testing" part of the string
        String nt = "Turn right and follow test for ";

        assertEquals(nt + "1km", testPath.toString());
        
        testPath.setDistance(0.5);
        assertEquals(nt + "500m", testPath.toString());
    }
}
