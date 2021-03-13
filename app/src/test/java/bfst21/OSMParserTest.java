package bfst21;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class OSMParserTest {
    private final Model samsoeModel = new Model("data/TEST_MAP_SAMSOE.osm");
    private final Model sjaelsoeModel = new Model("data/TEST_MAP_SJAELSOE.osm");

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
    public void testInputFileSamsoe() { assertEquals(96751, samsoeModel.getNodeIndex().size()); }

    @Test
    public void testInputFileSjaelsoe(){ assertEquals(41017, sjaelsoeModel.getNodeIndex().size()); }

    @Test
    public void testSamsoeCoastlines(){
        assertEquals(27,samsoeModel.getCoastlines().size());
    }

    @Test
    public void testSjaelsoeCoastlines(){
        assertEquals(0,sjaelsoeModel.getCoastlines().size());
    }

    /*@Test
    public void testSamsoeBuildings(){
        assertEquals(5894,samsoeModel.getBuildings().size());
    }

    @Test
    public void testSjaelsoeBuildings(){
        assertEquals(2894,sjaelsoeModel.getBuildings().size());
    }*/

    @Test
    public void testSamsoeCycleways(){ assertEquals(19,samsoeModel.getCycleways().size()); }

    @Test
    public void testSjaelsoeCycleways(){ assertEquals(154,sjaelsoeModel.getCycleways().size()); }

    @Test
    public void testSamsoeFootways(){ assertEquals(107,samsoeModel.getFootways().size()); }

    @Test
    public void testSjaelsoeFootways(){ assertEquals(157,sjaelsoeModel.getFootways().size()); }

    @Test
    public void testSamsoeMotorways(){ assertEquals(0,samsoeModel.getMotorways().size()); }

    @Test
    public void testSjaelsoeMotorways(){ assertEquals(11,sjaelsoeModel.getMotorways().size()); }

    @Test
    public void testSamsoeParks(){ assertEquals(5,samsoeModel.getParks().size()); }

    @Test
    public void testSjaelsoeParks(){ assertEquals(7,sjaelsoeModel.getParks().size()); }

    @Test
    public void testSamsoePaths(){ assertEquals(232,samsoeModel.getPaths().size()); }

    @Test
    public void testSjaelsoePaths(){ assertEquals(126,sjaelsoeModel.getPaths().size()); }

    @Test
    public void testSamsoePedestrian(){ assertEquals(0,samsoeModel.getPedestrianWays().size()); }

    @Test
    public void testSjaelsoePedestrian(){ assertEquals(5,sjaelsoeModel.getPedestrianWays().size()); }

    @Test
    public void testSamsoePrimary(){ assertEquals(0,samsoeModel.getPrimaryWays().size()); }

    @Test
    public void testSjaelsoePrimary(){ assertEquals(8,sjaelsoeModel.getPrimaryWays().size()); }

    @Test
    public void testSamsoeResidential(){ assertEquals(348,samsoeModel.getResidentialWays().size()); }

    @Test
    public void testSjaelsoeResidential(){ assertEquals(152,sjaelsoeModel.getResidentialWays().size()); }

    @Test
    public void testSamsoeSecondary(){ assertEquals(0,samsoeModel.getSecondaryWays().size()); }

    @Test
    public void testSjaelsoeSecondary(){ assertEquals(63,sjaelsoeModel.getSecondaryWays().size()); }

    @Test
    public void testSamsoeService(){ assertEquals(798,samsoeModel.getServiceWays().size()); }

    @Test
    public void testSjaelsoeService(){ assertEquals(402,sjaelsoeModel.getServiceWays().size()); }

    @Test
    public void testSamsoeTertiary(){ assertEquals(86,samsoeModel.getTertiaryWays().size()); }

    @Test
    public void testSjaelsoeTertiary(){ assertEquals(22,sjaelsoeModel.getTertiaryWays().size()); }

    @Test
    public void testSamsoeTrack(){ assertEquals(337,samsoeModel.getTrackWays().size()); }

    @Test
    public void testSjaelsoTrack(){ assertEquals(28,sjaelsoeModel.getTrackWays().size()); }

    @Test
    public void testSamsoeTrunk(){ assertEquals(0,samsoeModel.getTrunkWays().size()); }

    @Test
    public void testSjaelsoeTrunk(){ assertEquals(2,sjaelsoeModel.getTrunkWays().size()); }

    @Test
    public void testSamsoeUnclassifiedWays(){ assertEquals(222,samsoeModel.getUnclassifiedWays().size()); }

    @Test
    public void testSjaelsoeUnclassifiedWays(){ assertEquals(26,sjaelsoeModel.getUnclassifiedWays().size()); }

    /*@Test
    public void testSamsoeInlandWater(){ assertEquals(351,samsoeModel.getWater().size()); }

    @Test
    public void testSjaelsoeInlandWater(){ assertEquals(103,sjaelsoeModel.getWater().size()); }*/

    //TODO: test junctions when read as junctions and not secoundry ways
}
