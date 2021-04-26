package bfst21.osm;

public enum Tag {
    BUILDING(1),
    COASTLINE(100000000),
    CYCLEWAY(2),
    FOOTWAY(2),
    JUNCTION(10),
    LIVING_STREET(10),
    MOTORWAY(100000000),
    PARK(50),
    PATH(2),
    PEDESTRIAN(2),
    PRIMARY(400),
    RESIDENTIAL(10),
    ROAD(10),
    SECONDARY(150),
    SERVICE(10),
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
