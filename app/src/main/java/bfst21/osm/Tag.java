package bfst21.osm;

public enum Tag{
    JUNCTION(7,999),

    TERTIARY_LINK(150,1),
    SECONDARY_LINK(150,2),
    PRIMARY_LINK(400,3),
    TRUNK_LINK(400,4),
    MOTORWAY_LINK(700,5),
    WATERWAY(7,6),
    PATH(3,7),
    FOOTWAY(3,8),
    CYCLEWAY(7,9),
    PEDESTRIAN(7,10),
    TRACK(3,11),
    SERVICE(7,12),
    ROAD(7,13),
    LIVING_STREET(7,14),
    RESIDENTIAL(7,15),
    UNCLASSIFIED(7,16),
    TERTIARY(150,17),
    SECONDARY(150,18),
    PRIMARY(400,19),
    TRUNK(400,20),
    MOTORWAY(700,21),
    FERRY(700,22),

    COASTLINE(100000000,1),
    CITYBOARDER(200,2),
    FARMYARD(7,3),
    PARK(100,4),
    MEADOW(200,5),
    HEATH(200,6),
    CEMETERY(100,7),
    FOREST(400,8),
    GRASS(100,9),
    SCRUB(100,10),
    LAKE(100,11),
    WATER(400,12),
    BUILDING(7,13),

    TERRITORIALBORDER(100000000,5);



    public float zoomLimit;
    public int layer;

    Tag(float zoomLimit, int layer){
        this.zoomLimit = zoomLimit;
        this.layer = layer;
    }
}

