package bfst21.osm;

import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

enum Theme {
    DEFAULT, DARK,
}

public class RenderingStyle {
    private Map<Tag, Boolean> doubleDrawn;
    private Map<Tag, Color> defaultColorMap;
    private Map<Tag, Color> darkModeColorMap;
    private Map<Tag, DrawStyle> styleMap;
    private Map<Tag, Double> widthMap;
    private Theme theme;

    public Color sea;
    public Color islandFar;
    public Color islandClose;

    public RenderingStyle() {
        doubleDrawn = new HashMap<Tag, Boolean>();
        defaultColorMap = new HashMap<>();
        darkModeColorMap = new HashMap<>();
        styleMap = new HashMap<>();
        widthMap = new HashMap<>();
        theme = theme.DEFAULT;

        styleMap.put(Tag.BUILDING, DrawStyle.FILL);
        styleMap.put(Tag.WATER, DrawStyle.FILL);
        styleMap.put(Tag.PARK, DrawStyle.FILL);
        styleMap.put(Tag.CITYBOARDER, DrawStyle.FILL);
        styleMap.put(Tag.MEADOW,DrawStyle.FILL);
        styleMap.put(Tag.HEATH,DrawStyle.FILL);
        styleMap.put(Tag.LAKE, DrawStyle.FILL);
        styleMap.put(Tag.GRASS, DrawStyle.FILL);
        styleMap.put(Tag.CEMETERY, DrawStyle.FILL);
        styleMap.put(Tag.SCRUB, DrawStyle.FILL);
        styleMap.put(Tag.FARMYARD, DrawStyle.FILL);
        styleMap.put(Tag.FOREST, DrawStyle.FILL);
        styleMap.put(Tag.WOOD, DrawStyle.FILL);
        styleMap.put(Tag.CYCLEWAY, DrawStyle.DASH);
        styleMap.put(Tag.FOOTWAY, DrawStyle.DASH);
        styleMap.put(Tag.PATH, DrawStyle.DASH);
        styleMap.put(Tag.TRACK, DrawStyle.DASH);
        styleMap.put(Tag.FERRY,DrawStyle.DASH);

        doubleDrawn.put(Tag.JUNCTION, true);
        doubleDrawn.put(Tag.LIVING_STREET, true);
        doubleDrawn.put(Tag.MOTORWAY, true);
        doubleDrawn.put(Tag.MOTORWAY_LINK, true);
        doubleDrawn.put(Tag.PRIMARY, true);
        doubleDrawn.put(Tag.PRIMARY_LINK, true);
        doubleDrawn.put(Tag.RESIDENTIAL, true);
        doubleDrawn.put(Tag.ROAD, true);
        doubleDrawn.put(Tag.SECONDARY, true);
        doubleDrawn.put(Tag.SECONDARY_LINK, true);
        doubleDrawn.put(Tag.SERVICE, true);
        doubleDrawn.put(Tag.TERTIARY, true);
        doubleDrawn.put(Tag.TERTIARY_LINK, true);
        doubleDrawn.put(Tag.TRUNK, true);
        doubleDrawn.put(Tag.TRUNK_LINK, true);
        doubleDrawn.put(Tag.UNCLASSIFIED, true);
        doubleDrawn.put(Tag.PEDESTRIAN, true);

        widthMap.put(Tag.WATERWAY, 0.3);
        widthMap.put(Tag.MOTORWAY, 3.0);
        widthMap.put(Tag.MOTORWAY_LINK, 3.0);
        widthMap.put(Tag.PRIMARY, 2.0);
        widthMap.put(Tag.PRIMARY_LINK, 2.0);
        widthMap.put(Tag.TRUNK, 2.0);
        widthMap.put(Tag.TRUNK_LINK, 2.0);
        widthMap.put(Tag.SECONDARY, 1.5);
        widthMap.put(Tag.SECONDARY_LINK, 1.5);
        widthMap.put(Tag.TERTIARY, 2.0);
        widthMap.put(Tag.TERTIARY_LINK, 2.0);
        widthMap.put(Tag.RESIDENTIAL, 0.8);
        widthMap.put(Tag.SERVICE, 0.6);
        widthMap.put(Tag.CYCLEWAY, 0.5);
        widthMap.put(Tag.FOOTWAY, 0.5);
        widthMap.put(Tag.PATH, 0.5);
        widthMap.put(Tag.TRACK, 0.5);
        //livinstreet
        //pedestrian
        //road

        genDefaultMode();
        genDarkMode();
        defaultMode();
    }

    public Color getIslandColor(float distanceWidth) {
        if (distanceWidth >= 7.0) {
            return islandFar;
        } else {
            return islandClose;
        }
    }

    public void defaultMode() {
        sea = Color.rgb(170, 218, 255);
        islandFar = Color.rgb(187, 226, 198);
        islandClose = Color.rgb(255, 245, 190);
        theme = theme.DEFAULT;
    }

    public void darkMode() {
        sea = Color.rgb(47, 53, 66);
        islandFar = Color.rgb(87, 96, 111);
        theme = theme.DARK;
    }

    private void genDefaultMode() {
        defaultColorMap.put(Tag.BUILDING, Color.rgb(149, 165, 166));
        defaultColorMap.put(Tag.PARK, Color.rgb(168, 218, 181));
        defaultColorMap.put(Tag.WATER, Color.LIGHTBLUE);

        defaultColorMap.put(Tag.FOOTWAY, Color.GREEN);
        defaultColorMap.put(Tag.PATH, Color.GREEN);
        defaultColorMap.put(Tag.CYCLEWAY, Color.TURQUOISE);
        defaultColorMap.put(Tag.TRACK, Color.BROWN);

        defaultColorMap.put(Tag.MOTORWAY, Color.rgb(255, 181, 20));
        defaultColorMap.put(Tag.MOTORWAY_LINK, Color.rgb(255, 181, 20));

        defaultColorMap.put(Tag.PEDESTRIAN, Color.WHITE);
        defaultColorMap.put(Tag.UNCLASSIFIED, Color.WHITE);
        defaultColorMap.put(Tag.ROAD, Color.WHITE);
        defaultColorMap.put(Tag.LIVING_STREET, Color.WHITE);
        defaultColorMap.put(Tag.RESIDENTIAL, Color.WHITE);
        defaultColorMap.put(Tag.SERVICE, Color.WHITE);
        defaultColorMap.put(Tag.JUNCTION, Color.WHITE);
        defaultColorMap.put(Tag.TERTIARY, Color.WHITE);
        defaultColorMap.put(Tag.TERTIARY_LINK, Color.WHITE);
        defaultColorMap.put(Tag.SECONDARY, Color.rgb(189, 195, 199));
        defaultColorMap.put(Tag.SECONDARY_LINK, Color.rgb(189, 195, 199));
        defaultColorMap.put(Tag.PRIMARY, Color.rgb(253, 218, 118));
        defaultColorMap.put(Tag.PRIMARY_LINK, Color.rgb(253, 218, 118));
        defaultColorMap.put(Tag.TRUNK, Color.WHITE);
        defaultColorMap.put(Tag.TRUNK_LINK, Color.WHITE);

        //TODO: fix colors
        defaultColorMap.put(Tag.CITYBOARDER, Color.LIGHTGREY);
        defaultColorMap.put(Tag.MEADOW, Color.rgb(168, 218, 181));
        defaultColorMap.put(Tag.HEATH, Color.GREEN);
        defaultColorMap.put(Tag.CEMETERY, Color.LIGHTGREEN);
        defaultColorMap.put(Tag.FERRY, Color.PURPLE);
        defaultColorMap.put(Tag.LAKE, Color.LIGHTBLUE);
        defaultColorMap.put(Tag.GRASS, Color.rgb(168, 240, 181));
        defaultColorMap.put(Tag.SCRUB, Color.rgb(168, 240, 181));
        defaultColorMap.put(Tag.PEDESTRIAN, Color.DARKGRAY);
        defaultColorMap.put(Tag.FARMYARD, Color.rgb(255, 217, 140));
        defaultColorMap.put(Tag.WATERWAY, Color.LIGHTBLUE);
        defaultColorMap.put(Tag.FOREST, Color.rgb(168, 218, 181));
        defaultColorMap.put(Tag.WOOD,Color.rgb(168, 218, 181));
    }

    private void genDarkMode() {
        darkModeColorMap.put(Tag.WATER, Color.LIGHTBLUE);
        darkModeColorMap.put(Tag.PARK, Color.rgb(116, 125, 140));
        darkModeColorMap.put(Tag.BUILDING, Color.rgb(72, 84, 96));

        darkModeColorMap.put(Tag.FOOTWAY, Color.GREEN);
        darkModeColorMap.put(Tag.PATH, Color.GREEN);
        darkModeColorMap.put(Tag.CYCLEWAY, Color.TURQUOISE);
        darkModeColorMap.put(Tag.TRACK, Color.BROWN);

        darkModeColorMap.put(Tag.PEDESTRIAN, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.UNCLASSIFIED, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.ROAD, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.LIVING_STREET, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.RESIDENTIAL, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.SERVICE, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.JUNCTION, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.TERTIARY, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.TERTIARY_LINK, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.SECONDARY, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.SECONDARY_LINK, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.PRIMARY, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.PRIMARY_LINK, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.TRUNK, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.TRUNK_LINK, Color.rgb(128, 142, 155));

        //TODO: fix colors
        darkModeColorMap.put(Tag.CITYBOARDER, Color.LIGHTGREY);
        darkModeColorMap.put(Tag.MEADOW, Color.rgb(168, 218, 181));
        darkModeColorMap.put(Tag.HEATH, Color.GREEN);
        darkModeColorMap.put(Tag.CEMETERY, Color.LIGHTGREEN);
        darkModeColorMap.put(Tag.FERRY, Color.PURPLE);
        darkModeColorMap.put(Tag.LAKE, Color.LIGHTBLUE);
        darkModeColorMap.put(Tag.GRASS, Color.rgb(168, 240, 181));
        darkModeColorMap.put(Tag.SCRUB, Color.rgb(168, 240, 181));
        darkModeColorMap.put(Tag.PEDESTRIAN, Color.DARKGRAY);
        darkModeColorMap.put(Tag.FARMYARD, Color.rgb(255, 217, 140));
        darkModeColorMap.put(Tag.WATERWAY, Color.LIGHTBLUE);
        darkModeColorMap.put(Tag.FOREST, Color.RED);
        darkModeColorMap.put(Tag.WOOD,Color.rgb(168, 218, 181));
    }

    public Color getColorByTag(Tag tag) {
        switch (theme) {
            case DARK:
                var darkColor = darkModeColorMap.get(tag);
                return darkColor == null ? Color.RED : darkColor;
            case DEFAULT:
            default:
                var color = defaultColorMap.get(tag);
                return color == null ? Color.RED : color;
        }
    }

    public double getWidthByTag(Tag tag) {
        var width = widthMap.get(tag);
        return width == null ? 1.0 : width;
    }

    public DrawStyle getDrawStyleByTag(Tag tag) {
        var style = styleMap.get(tag);

        return style == null ? DrawStyle.STROKE : style;
    }

    public boolean getDoubleDrawn(Tag tag){
        Boolean drawnDouble = doubleDrawn.get(tag);
        return drawnDouble == null ? false : drawnDouble;
    }
}
