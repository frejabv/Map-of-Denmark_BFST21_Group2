package bfst21.osm;

public enum Tag {
    BUILDING(1),
    CEMETERY(50),
    COASTLINE(100000000),
    CYCLEWAY(2),
    /*FARMLAND(50),*/
    FOOTWAY(2),
    FOREST(500),
    GRASS(50),
    GRASSLAND(50),
    GRAVE_YARD(50),
    HEATH(500),
    JUNCTION(10),
    LIVING_STREET(10),
    MEADOW(50),
    MOTORWAY(100000000),
    ORCHARD(50),
    PARK(50),
    PATH(2),
    PEDESTRIAN(2),
    PRIMARY(400),
    RESIDENTIAL(10),
    RIVER(100000000),
    ROAD(10),
    SCRUB(50),
    SECONDARY(150),
    SERVICE(10),
    STREAM(100000000),
    TERRITORIALBORDER(100000000),
    TERTIARY(150),
    TRACK(10),
    TRUNK(400),
    UNCLASSIFIED(10),
    WATER(100000000);

    public float zoomLimit;

    Tag(float zoomLimit){
        this.zoomLimit = zoomLimit;
    }
}
