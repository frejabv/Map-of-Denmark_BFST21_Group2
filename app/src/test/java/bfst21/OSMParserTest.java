package bfst21;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import bfst21.exceptions.UnsupportedFileTypeException;
import bfst21.osm.OSMParser;
import bfst21.osm.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OSMParserTest {
    private final Model samsoeModel = new Model("data/TEST_MAP_SAMSOE.osm",false);
    private final Model sjaelsoeModel = new Model("data/TEST_MAP_SJAELSOE.osm.zip",false);

    @Test
    public void testLatLonBoundsSamsoe(){
        assertEquals(10.8105f, samsoeModel.getMaxX());
        assertEquals(10.3828f, samsoeModel.getMinX());
        assertEquals(56.0099f/-0.56f, samsoeModel.getMaxY());
        assertEquals(55.7518f/-0.56f, samsoeModel.getMinY());
    }

    @Test
    public void testLatLonBoundsSjaelsoe(){
        assertEquals(12.5055f, sjaelsoeModel.getMaxX());
        assertEquals(12.4173f, sjaelsoeModel.getMinX());
        assertEquals(55.8840f/-0.56f, sjaelsoeModel.getMaxY());
        assertEquals(55.8584f/-0.56f, sjaelsoeModel.getMinY());
    }

    @Test
    public void testUnsupportedFileTypeException(){
        Assertions.assertThrows(UnsupportedFileTypeException.class,()-> {
            OSMParser.readMapElements("data/TEST_MAP_SAMSOE.osm.png",new Model(null,false));
        });
    }

    @Test
    public void testInputFileSamsoe() { assertEquals(96751, samsoeModel.getNodeIndex().size()); }

    @Test
    public void testInputFileSjaelsoe(){ assertEquals(41017, sjaelsoeModel.getNodeIndex().size()); }

    @Test
    public void testSamsoeCoastlines(){ assertEquals(27,samsoeModel.getCoastlines().size()); }

    @Test
    public void testSjaelsoeCoastlines(){ assertEquals(0,sjaelsoeModel.getCoastlines().size()); }

    @Test
    public void testSamsoeIslands(){ assertEquals(20, samsoeModel.getIslands().size()); }

    @Test
    public void testSjaelsoeIslands(){ assertEquals(0, sjaelsoeModel.getIslands().size()); }

    @Test
    public void testSamsoeBuildings(){
        assertEquals(5863,samsoeModel.getFillMap().get(Tag.BUILDING).size());
    }

    @Test
    public void testSjaelsoeBuildings(){
        assertEquals(2880,sjaelsoeModel.getFillMap().get(Tag.BUILDING).size());
    }

    @Test
    public void testSamsoeCycleways(){ assertEquals(19,samsoeModel.getDrawableMap().get(Tag.CYCLEWAY).size()); }

    @Test
    public void testSjaelsoeCycleways(){ assertEquals(188,sjaelsoeModel.getDrawableMap().get(Tag.CYCLEWAY).size()); }

    @Test
    public void testSamsoeFootways(){ assertEquals(108,samsoeModel.getDrawableMap().get(Tag.FOOTWAY).size()); }

    @Test
    public void testSjaelsoeFootways(){ assertEquals(157,sjaelsoeModel.getDrawableMap().get(Tag.FOOTWAY).size()); }

    @Test
    public void testSamsoeMotorways(){ assertThrows(NullPointerException.class, () -> samsoeModel.getDrawableMap().get(Tag.MOTORWAY).size()); }

    @Test
    public void testSjaelsoeMotorways(){ assertEquals(11,sjaelsoeModel.getDrawableMap().get(Tag.MOTORWAY).size()); }

    @Test
    public void testSamsoeParks(){ assertEquals(5,samsoeModel.getFillMap().get(Tag.PARK).size()); }

    @Test
    public void testSjaelsoeParks(){ assertEquals(7,sjaelsoeModel.getFillMap().get(Tag.PARK).size()); }

    @Test
    public void testSamsoePaths(){ assertEquals(232,samsoeModel.getDrawableMap().get(Tag.PATH).size()); }

    @Test
    public void testSjaelsoePaths(){ assertEquals(126,sjaelsoeModel.getDrawableMap().get(Tag.PATH).size()); }

    @Test
    public void testSamsoePedestrian(){ assertThrows(NullPointerException.class, () -> samsoeModel.getDrawableMap().get(Tag.PEDESTRIAN).size()); }

    @Test
    public void testSjaelsoePedestrian(){ assertEquals(5,sjaelsoeModel.getDrawableMap().get(Tag.PEDESTRIAN).size()); }

    @Test
    public void testSamsoePrimary(){ assertThrows(NullPointerException.class, () -> samsoeModel.getDrawableMap().get(Tag.PRIMARY).size()); }

    @Test
    public void testSjaelsoePrimary(){ assertEquals(8,sjaelsoeModel.getDrawableMap().get(Tag.PRIMARY).size()); }

    @Test
    public void testSamsoeResidential(){ assertEquals(490,samsoeModel.getDrawableMap().get(Tag.RESIDENTIAL).size()); }

    @Test
    public void testSjaelsoeResidential(){ assertEquals(164,sjaelsoeModel.getDrawableMap().get(Tag.RESIDENTIAL).size()); }

    @Test
    public void testSamsoeSecondary(){ assertThrows(NullPointerException.class, () -> samsoeModel.getDrawableMap().get(Tag.SECONDARY).size()); }

    @Test
    public void testSjaelsoeSecondary(){ assertEquals(63,sjaelsoeModel.getDrawableMap().get(Tag.SECONDARY).size()); }

    @Test
    public void testSamsoeService(){ assertEquals(799,samsoeModel.getDrawableMap().get(Tag.SERVICE).size()); }

    @Test
    public void testSjaelsoeService(){ assertEquals(402,sjaelsoeModel.getDrawableMap().get(Tag.SERVICE).size()); }

    @Test
    public void testSamsoeTertiary(){ assertEquals(86,samsoeModel.getDrawableMap().get(Tag.TERTIARY).size()); }

    @Test
    public void testSjaelsoeTertiary(){ assertEquals(22,sjaelsoeModel.getDrawableMap().get(Tag.TERTIARY).size()); }

    @Test
    public void testSamsoeTrack(){ assertEquals(337,samsoeModel.getDrawableMap().get(Tag.TRACK).size()); }

    @Test
    public void testSjaelsoTrack(){ assertEquals(54,sjaelsoeModel.getDrawableMap().get(Tag.TRACK).size()); }

    @Test
    public void testSamsoeTrunk(){ assertThrows(NullPointerException.class, () -> samsoeModel.getDrawableMap().get(Tag.TRUNK).size()); }

    @Test
    public void testSjaelsoeTrunk(){ assertEquals(2,sjaelsoeModel.getDrawableMap().get(Tag.TRUNK).size()); }

    @Test
    public void testSamsoeUnclassifiedWays(){ assertEquals(223,samsoeModel.getDrawableMap().get(Tag.UNCLASSIFIED).size()); }

    @Test
    public void testSjaelsoeUnclassifiedWays(){ assertEquals(26,sjaelsoeModel.getDrawableMap().get(Tag.UNCLASSIFIED).size()); }

    @Test
    public void testSamsoeInlandWater(){ assertEquals(347,samsoeModel.getFillMap().get(Tag.WATER).size()); }

    @Test
    public void testSjaelsoeInlandWater(){ assertEquals(101,sjaelsoeModel.getFillMap().get(Tag.WATER).size()); }

}
