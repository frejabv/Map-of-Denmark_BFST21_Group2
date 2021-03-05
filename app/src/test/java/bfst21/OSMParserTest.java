package bfst21;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class OSMParserTest {
    @Test
    public void testOSMInputFile() {
        var model = new Model("data/test.osm");
        assertEquals(115, model.getNodeIndex().size());
    }
   @Test
   public void testZIPInputFile(){
        var model = new Model("data/test.osm.zip");
        assertEquals(115,model.getNodeIndex().size());
   }

}
