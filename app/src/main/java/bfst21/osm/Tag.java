package bfst21.osm;

public enum Tag {
    //fillables
    COASTLINE((int) Double.POSITIVE_INFINITY, 1),
    BEACH(150, 2),
    CITYBORDER(150, 3),
    GRASSLAND(150, 4),
    MEADOW(150, 5),
    FOREST(400, 6),
    FARMYARD(7, 7),
    PARK(100, 8),
    HEATH(150, 9),
    CEMETERY(100, 10),
    WOOD(400, 11),
    GRASS(100, 12),
    SCRUB(100, 13),
    LAKE(100, 14),
    WATER(400, 15),
    BUILDING(7, 16),

    //drawables
    TERTIARY_LINK(150, 17),
    SECONDARY_LINK(150, 18),
    PRIMARY_LINK(400, 19),
    TRUNK_LINK(400, 20),
    MOTORWAY_LINK(700, 21),
    WATERWAY(7, 22),
    PATH(3, 23),
    FOOTWAY(3, 24),
    CYCLEWAY(7, 25),
    PEDESTRIAN(7, 26),
    TRACK(3, 27),
    SERVICE(7, 28),
    ROAD(7, 29),
    LIVING_STREET(7, 30),
    RESIDENTIAL(7, 31),
    UNCLASSIFIED(7, 32),
    TERTIARY(150, 33),
    SECONDARY(150, 34),
    PRIMARY(400, 35),
    TRUNK(400, 36),
    MOTORWAY(700, 37),
    FERRY(700, 38);

    public int zoomLimit;
    public int layer;

    Tag(int zoomLimit, int layer) {
        this.zoomLimit = zoomLimit;
        this.layer = layer;
    }
}

