package bfst21.osm;

public enum Tag{
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
    BEACH(200,2),
    CITYBORDER(200,3),
    GRASSLAND(200,4),
    MEADOW(200,5),
    FOREST(400,6),
    FARMYARD(7,7),
    PARK(100,8),
    HEATH(200,9),
    CEMETERY(100,10),
    WOOD(400,11),
    GRASS(100,12),
    SCRUB(100,13),
    LAKE(100,14),
    WATER(400,15),
    BUILDING(7,16);



    public float zoomLimit;
    public int layer;

    Tag(float zoomLimit, int layer){
        this.zoomLimit = zoomLimit;
        this.layer = layer;
    }
}

