package bfst21;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class OSMParserTest {
    @Test
    public void test() {
        var model = new Model("data/test.osm");
        assertEquals(115, model.getNodeIndex().size());
    }

}
