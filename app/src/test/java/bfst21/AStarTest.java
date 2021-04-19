package bfst21;

import bfst21.osm.Node;

import static org.junit.jupiter.api.Assertions.assertEquals;

import bfst21.pathfinding.AStar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AStarTest {
    private Model model;
    private final Model samsoeModel = new Model("data/TEST_MAP_SAMSOE.osm",false);

    @BeforeEach
    public void setUp() {
        model = new Model("data/dk-simple.osm",false);
        //System.out.println(model.getDrawableMap());
        model.setUpAStar();
    }
    
    @Test
    public void testCreateAStar() {
        Node testNode = model.getNodeIndex().getMember(11);
        assertEquals(2,testNode.getAdjecencies().size());
        testNode = model.getNodeIndex().getMember(2);
        assertEquals(3,testNode.getAdjecencies().size()); //dunno yet
    }

    @Test
    public void testAdjecencies() {
        //not done, test the expected adjecencies
        Node testNode = model.getNodeIndex().getMember(11);
        assertEquals(model.getNodeIndex().getMember(11),testNode.getAdjecencies().get(0).target);
    }

    @Test
    public void testPrintPath() {
        AStar astar = model.getAStar();
        astar.AStarSearch(model.getNodeIndex().getMember(1),model.getNodeIndex().getMember(5), model.getCurrentTransportType());
        assertEquals("1 -> 2 -> 5 -> ", astar.printPath(model.getNodeIndex().getMember(5)));
    }

    @Test
    public void testDriveable() {
        Node testNode = model.getNodeIndex().getMember(11);
        assertEquals(true,testNode.getAdjecencies().get(0).isDriveable());
        assertEquals(false,testNode.getAdjecencies().get(0).isWalkable());
    }

    //test path that avoids the direct route because we are driving

    //test change transport type which changes the path

    //test costvalue for edges and total costvalue

    //maybe test speed
}
