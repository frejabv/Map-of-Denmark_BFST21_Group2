package bfst21.osm;

public enum Tag {
    BUILDING(7),
    COASTLINE(700),
    CYCLEWAY(7),
    FOOTWAY(3),
    JUNCTION(7),
    LIVING_STREET(7),
    MOTORWAY(700),
    PARK(50),
    PATH(3),
    PEDESTRIAN(7),
    PRIMARY(400),
    RESIDENTIAL(7),
    ROAD(7),
    SECONDARY(150),
    SERVICE(7),
    TERRITORIALBORDER(100000000),
    TERTIARY(150),
    TRACK(7),
    TRUNK(400),
    UNCLASSIFIED(7),
    WATER(100000000);

    public float zoomLimit;

    Tag(float zoomLimit){
        this.zoomLimit = zoomLimit;
    }
}
