package bfst21;

import bfst21.Rtree.Rtree;
import bfst21.Rtree.Rectangle;
import bfst21.osm.Drawable;
import bfst21.osm.Node;
import bfst21.osm.Way;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class RTreeClass {
    private final Model model = new Model("data/TEST_RTREE_STRUCTURE.osm", false);

    @Test
    public void testNearestWay() {
        Point2D testPoint = new Point2D(1.5, 1.5 / -Model.scalingConstant);

        assertEquals(54, model.getRoadRTree().nearestWay(testPoint).getId());
    }

    @Test
    public void testGetAllDrawables() {
        ArrayList<Drawable> testList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Way testway = new Way(i);
            testway.addNode(new Node(i, i, i));
            testway.addNode(new Node(-i, -i, -i));
            testway.createRectangle();
            testList.add(testway);
        }

        Rtree testTree = new Rtree(testList);
        List<Drawable> resultList = testTree.getAllDrawables(testTree.getRoot());

        for (Drawable d: testList) {
            assertTrue(resultList.contains(d));
        }
    }

    @Test
    public void testQuery() {
        Rtree testTree = model.getRoadRTree();

        Rectangle bounds = model.getRoadRTree().getRoot().getRect();
        List<Drawable> queryResult = testTree.query(bounds);

        List<Drawable> expectedResult = testTree.getAllDrawables(testTree.getRoot());

        for (Drawable d: queryResult) {
            assertTrue(expectedResult.contains(d));
        }

        Rectangle smallerQueryWindow = new Rectangle(
                0.1f, 1.75f / -Model.scalingConstant,
                3.5f, 1.05f / -Model.scalingConstant);

        queryResult.clear();
        queryResult = testTree.query(smallerQueryWindow);

        //sorting method taken from RtreeNode class. puts them in order og y-coordinate
        queryResult.sort((a, b) -> {
            float aVal =  a.getRect().getMinY();
            float bVal =  b.getRect().getMinY();
            return Float.compare(bVal, aVal);
        });

        assertEquals(9, queryResult.size());

        for (int i = 0; i < 9; i++) {
            assertEquals(i+50, ((Way) queryResult.get(i)).getId());
        }
    }
}
