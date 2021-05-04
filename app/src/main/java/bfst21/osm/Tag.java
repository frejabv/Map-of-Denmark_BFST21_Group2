package bfst21.osm;

public enum Tag{
    JUNCTION(7,999),

    PATH(3,0),
    FOOTWAY(3,1),
    CYCLEWAY(7,2),
    PEDESTRIAN(7,3),
    TRACK(3,4),
    SERVICE(7,5),
    ROAD(7,6),
    LIVING_STREET(7,7),
    RESIDENTIAL(7,8),
    UNCLASSIFIED(7,9),
    TERTIARY_LINK(150,10),
    TERTIARY(150,11),
    SECONDARY_LINK(150,12),
    SECONDARY(150,13),
    PRIMARY_LINK(400,14),
    PRIMARY(400,15),
    TRUNK_LINK(400,16),
    TRUNK(400,17),
    MOTORWAY_LINK(700,18),
    MOTORWAY(700,19),

    WATER(100000000,0),
    COASTLINE(100000000,1),
    CITYBOARDER(200,2),
    PARK(100,3),
    MEADOW(200,4),
    HEATH(200,5),
    BUILDING(7,6),

    TERRITORIALBORDER(100000000,5);



    public float zoomLimit;
    public int layer;

    Tag(float zoomLimit, int layer){
        this.zoomLimit = zoomLimit;
        this.layer = layer;
    }
}

