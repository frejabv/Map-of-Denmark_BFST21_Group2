package bfst21;

import bfst21.osm.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OSMParserTest {
    private Model samsoeModel;
    private Model sjaelsoeModel;

    public OSMParserTest() throws Exception {
        samsoeModel = new Model("/bfst21/data/TEST_MAP_SAMSOE.osm", false);
        sjaelsoeModel = new Model("/bfst21/data/TEST_MAP_SJAELSOE.osm.zip", false);
    }

    @Test
    public void testLatLonBoundsSamsoe() {
        assertEquals(10.8105f, samsoeModel.getMaxX());
        assertEquals(10.3828f, samsoeModel.getMinX());
        assertEquals(56.0099f / -Model.scalingConstant, samsoeModel.getMaxY());
        assertEquals(55.7518f / -Model.scalingConstant, samsoeModel.getMinY());
    }

    @Test
    public void testLatLonBoundsSjaelsoe() {
        assertEquals(12.5055f, sjaelsoeModel.getMaxX());
        assertEquals(12.4173f, sjaelsoeModel.getMinX());
        assertEquals(55.8840f / -Model.scalingConstant, sjaelsoeModel.getMaxY());
        assertEquals(55.8584f / -Model.scalingConstant, sjaelsoeModel.getMinY());
    }

    @Test
    public void testUnsupportedFileTypeException() {
        Assertions.assertThrows(IOException.class, () -> {
            new Model("TEST_MAP_SAMSOE.osm.png", false);
        });
    }

    @Test
    public void testInputFileSamsoe() {
        assertEquals(96751, samsoeModel.getNodeIndex().size());
    }

    @Test
    public void testInputFileSjaelsoe() {
        assertEquals(41017, sjaelsoeModel.getNodeIndex().size());
    }

    @Test
    public void testSamsoeCoastlines() {
        assertThrows(NullPointerException.class, () -> samsoeModel.getCoastlines().size());
    }

    @Test
    public void testSjaelsoeCoastlines() {
        assertThrows(NullPointerException.class, () -> sjaelsoeModel.getCoastlines().size());
    }

    @Test
    public void testSamsoeIslands() {
        assertEquals(20, samsoeModel.getIslands().size());
    }

    @Test
    public void testSjaelsoeIslands() {
        assertEquals(0, sjaelsoeModel.getIslands().size());
    }

    @Test
    public void testSamsoeBuildings() {
        assertEquals(5863, samsoeModel.getFillMap().get(Tag.BUILDING).size());
    }

    @Test
    public void testSjaelsoeBuildings() {
        assertEquals(2880, sjaelsoeModel.getFillMap().get(Tag.BUILDING).size());
    }

    @Test
    public void testSamsoeCycleways() {
        assertEquals(19, samsoeModel.getDrawableMap().get(Tag.CYCLEWAY).size());
    }

    @Test
    public void testSjaelsoeCycleways() {
        assertEquals(154, sjaelsoeModel.getDrawableMap().get(Tag.CYCLEWAY).size());
    }

    @Test
    public void testSamsoeFootways() {
        assertEquals(107, samsoeModel.getDrawableMap().get(Tag.FOOTWAY).size());
    }

    @Test
    public void testSjaelsoeFootways() {
        assertEquals(157, sjaelsoeModel.getDrawableMap().get(Tag.FOOTWAY).size());
    }

    @Test
    public void testSamsoeMotorways() {
        assertThrows(NullPointerException.class, () -> samsoeModel.getDrawableMap().get(Tag.MOTORWAY).size());
    }

    @Test
    public void testSjaelsoeMotorways() {
        assertEquals(11, sjaelsoeModel.getDrawableMap().get(Tag.MOTORWAY).size());
    }

    @Test
    public void testSamsoeParks() {
        assertEquals(5, samsoeModel.getFillMap().get(Tag.PARK).size());
    }

    @Test
    public void testSjaelsoeParks() {
        assertEquals(7, sjaelsoeModel.getFillMap().get(Tag.PARK).size());
    }

    @Test
    public void testSamsoePaths() {
        assertEquals(232, samsoeModel.getDrawableMap().get(Tag.PATH).size());
    }

    @Test
    public void testSjaelsoePaths() {
        assertEquals(126, sjaelsoeModel.getDrawableMap().get(Tag.PATH).size());
    }

    @Test
    public void testSamsoePedestrian() {
        assertThrows(NullPointerException.class, () -> samsoeModel.getDrawableMap().get(Tag.PEDESTRIAN).size());
    }

    @Test
    public void testSjaelsoePedestrian() {
        assertEquals(5, sjaelsoeModel.getDrawableMap().get(Tag.PEDESTRIAN).size());
    }

    @Test
    public void testSamsoePrimary() {
        assertThrows(NullPointerException.class, () -> samsoeModel.getDrawableMap().get(Tag.PRIMARY).size());
    }

    @Test
    public void testSjaelsoePrimary() {
        assertEquals(8, sjaelsoeModel.getDrawableMap().get(Tag.PRIMARY).size());
    }

    @Test
    public void testSamsoeResidential() {
        assertEquals(348, samsoeModel.getDrawableMap().get(Tag.RESIDENTIAL).size());
    }

    @Test
    public void testSjaelsoeResidential() {
        assertEquals(152, sjaelsoeModel.getDrawableMap().get(Tag.RESIDENTIAL).size());
    }

    @Test
    public void testSamsoeSecondary() {
        assertThrows(NullPointerException.class, () -> samsoeModel.getDrawableMap().get(Tag.SECONDARY).size());
    }

    @Test
    public void testSjaelsoeSecondary() {
        assertEquals(63, sjaelsoeModel.getDrawableMap().get(Tag.SECONDARY).size());
    }

    @Test
    public void testSamsoeService() {
        assertEquals(798, samsoeModel.getDrawableMap().get(Tag.SERVICE).size());
    }

    @Test
    public void testSjaelsoeService() {
        assertEquals(402, sjaelsoeModel.getDrawableMap().get(Tag.SERVICE).size());
    }

    @Test
    public void testSamsoeTertiary() {
        assertEquals(86, samsoeModel.getDrawableMap().get(Tag.TERTIARY).size());
    }

    @Test
    public void testSjaelsoeTertiary() {
        assertEquals(22, sjaelsoeModel.getDrawableMap().get(Tag.TERTIARY).size());
    }

    @Test
    public void testSamsoeTrack() {
        assertEquals(337, samsoeModel.getDrawableMap().get(Tag.TRACK).size());
    }

    @Test
    public void testSjaelsoTrack() {
        assertEquals(29, sjaelsoeModel.getDrawableMap().get(Tag.TRACK).size());
    }

    @Test
    public void testSamsoeTrunk() {
        assertThrows(NullPointerException.class, () -> samsoeModel.getDrawableMap().get(Tag.TRUNK).size());
    }

    @Test
    public void testSjaelsoeTrunk() {
        assertEquals(2, sjaelsoeModel.getDrawableMap().get(Tag.TRUNK).size());
    }

    @Test
    public void testSamsoeUnclassifiedWays() {
        assertEquals(222, samsoeModel.getDrawableMap().get(Tag.UNCLASSIFIED).size());
    }

    @Test
    public void testSjaelsoeUnclassifiedWays() {
        assertEquals(26, sjaelsoeModel.getDrawableMap().get(Tag.UNCLASSIFIED).size());
    }

    @Test
    public void testSamsoeInlandWater() {
        assertEquals(347, samsoeModel.getFillMap().get(Tag.WATER).size());
    }

    @Test
    public void testSjaelsoeInlandWater() {
        assertEquals(101, sjaelsoeModel.getFillMap().get(Tag.WATER).size());
    }

}
