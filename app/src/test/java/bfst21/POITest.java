package bfst21;

import bfst21.POI.POI;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class POITest {

    @Test
    public void testType() {
        POI testPOI = new POI("test", "type", "imageType", 1, 1);
        assertEquals("type", testPOI.getType());
        assertEquals("imageType", testPOI.getImageType());

        testPOI.changeType("new type");
        assertEquals("new type", testPOI.getType());
    }

    @Test
    public void testCompareTo() {
        POI p1 = new POI("closest", "test", 1, 1);
        POI p2 = new POI("furthest", "test", 2, 1);

        Point2D testPoint = new Point2D(0,1);

        p1.setDistTo(testPoint);
        p2.setDistTo(testPoint);


        assertEquals(1, p1.getDistTo());
        assertEquals(4, p2.getDistTo());

        assertEquals(-1, p1.compareTo(p2));
        assertEquals(0, p1.compareTo(p1));
        assertEquals(0, p2.compareTo(p2));
        assertEquals(1, p2.compareTo(p1));
    }
}
