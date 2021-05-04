package bfst21.osm;

import java.util.Comparator;

public enum Tag{
    BUILDING(7,999),
    COASTLINE(100000000,999),
    CYCLEWAY(7,2),
    FOOTWAY(3,1),
    JUNCTION(7,999),
    LIVING_STREET(7,7),
    MOTORWAY(700,19),
    MOTORWAY_LINK(700,18),
    PARK(50,999),
    PATH(3,0),
    PEDESTRIAN(7,3),
    PRIMARY(400,15),
    PRIMARY_LINK(400,14),
    RESIDENTIAL(7,8),
    ROAD(7,6),
    SECONDARY(150,13),
    SECONDARY_LINK(150,12),
    SERVICE(7,5),
    TERRITORIALBORDER(100000000,999),
    TERTIARY(150,11),
    TERTIARY_LINK(150,10),
    TRACK(7,4),
    TRUNK(400,17),
    TRUNK_LINK(400,16),
    UNCLASSIFIED(7,9),
    WATER(100000000,999);

    public float zoomLimit;
    public int layer;

    Tag(float zoomLimit, int layer){
        this.zoomLimit = zoomLimit;
        this.layer = layer;
    }
}

