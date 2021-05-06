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
        widthMap.put(Tag.LIVING_STREET, 0.8);
        widthMap.put(Tag.PEDESTRIAN, 0.8);
        widthMap.put(Tag.ROAD, 0.8);

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
        sea = Color.rgb(156, 192, 249);
        islandFar = Color.rgb(187, 226, 197);
        islandClose = Color.rgb(241, 243, 244);
        theme = theme.DEFAULT;
    }

    public void darkMode() {
        sea = Color.rgb(58, 64, 93);
        islandFar = Color.rgb(40, 68, 53);
        islandClose = Color.rgb(25, 26, 28);
        theme = theme.DARK;
    }

    private void genDefaultMode() {
        defaultColorMap.put(Tag.BUILDING, Color.rgb(206, 214, 224));
        defaultColorMap.put(Tag.PARK, Color.rgb(206,234,214));
        defaultColorMap.put(Tag.WATER, Color.rgb(156, 192, 249));

        defaultColorMap.put(Tag.FOOTWAY, Color.rgb(112, 112, 112));
        defaultColorMap.put(Tag.PATH, Color.rgb(112, 112, 112));
        defaultColorMap.put(Tag.CYCLEWAY, Color.rgb(91,185,116));
        defaultColorMap.put(Tag.TRACK, Color.rgb(112, 112, 112));

        defaultColorMap.put(Tag.MOTORWAY, Color.rgb(255, 107, 129));
        defaultColorMap.put(Tag.MOTORWAY_LINK, Color.rgb(255, 107, 129));

        defaultColorMap.put(Tag.PEDESTRIAN, Color.WHITE);
        defaultColorMap.put(Tag.UNCLASSIFIED, Color.WHITE);
        defaultColorMap.put(Tag.ROAD, Color.WHITE);
        defaultColorMap.put(Tag.LIVING_STREET, Color.WHITE);
        defaultColorMap.put(Tag.RESIDENTIAL, Color.WHITE);
        defaultColorMap.put(Tag.SERVICE, Color.WHITE);
        defaultColorMap.put(Tag.JUNCTION, Color.WHITE);
        defaultColorMap.put(Tag.TERTIARY, Color.WHITE);
        defaultColorMap.put(Tag.TERTIARY_LINK, Color.WHITE);
        defaultColorMap.put(Tag.SECONDARY, Color.rgb(246, 229, 141));
        defaultColorMap.put(Tag.SECONDARY_LINK, Color.rgb(246, 229, 141));
        defaultColorMap.put(Tag.PRIMARY, Color.rgb(255, 221, 89));
        defaultColorMap.put(Tag.PRIMARY_LINK, Color.rgb(255, 221, 89));
        defaultColorMap.put(Tag.TRUNK, Color.rgb(255, 221, 89));
        defaultColorMap.put(Tag.TRUNK_LINK, Color.rgb(255, 221, 89));

        //TODO: fix colors
        defaultColorMap.put(Tag.CITYBOARDER, Color.rgb(241,243,244));
        defaultColorMap.put(Tag.MEADOW, Color.rgb(198, 228, 207));
        defaultColorMap.put(Tag.HEATH, Color.rgb(190,217,170));
        defaultColorMap.put(Tag.CEMETERY, Color.rgb(168, 218, 181));
        defaultColorMap.put(Tag.FERRY, Color.rgb(112,161,255));
        defaultColorMap.put(Tag.LAKE, Color.rgb(156, 192, 249));
        defaultColorMap.put(Tag.GRASS, Color.rgb(198, 228, 207));
        defaultColorMap.put(Tag.SCRUB, Color.rgb(198, 228, 207));
        defaultColorMap.put(Tag.FARMYARD, Color.rgb(227,229,230));
        defaultColorMap.put(Tag.WATERWAY, Color.rgb(156, 192, 249));
        defaultColorMap.put(Tag.FOREST, Color.rgb(175,221,187));
        defaultColorMap.put(Tag.WOOD,Color.rgb(175,221,187));
    }

    private void genDarkMode() {
        darkModeColorMap.put(Tag.WATER, Color.rgb(58, 64, 93));
        darkModeColorMap.put(Tag.PARK, Color.rgb(71,95,82));
        darkModeColorMap.put(Tag.BUILDING, Color.rgb(73, 74, 76));

        darkModeColorMap.put(Tag.FOOTWAY, Color.rgb(184,184,184));
        darkModeColorMap.put(Tag.PATH, Color.rgb(184,184,184));
        darkModeColorMap.put(Tag.CYCLEWAY, Color.rgb(0,100,27));
        darkModeColorMap.put(Tag.TRACK, Color.rgb(184,184,184));

        darkModeColorMap.put(Tag.MOTORWAY, Color.rgb(167, 36, 47));
        darkModeColorMap.put(Tag.MOTORWAY_LINK, Color.rgb(167, 36, 47));

        darkModeColorMap.put(Tag.PEDESTRIAN, Color.rgb(63, 64, 66));
        darkModeColorMap.put(Tag.UNCLASSIFIED, Color.rgb(63, 64, 66));
        darkModeColorMap.put(Tag.ROAD, Color.rgb(63, 64, 66));
        darkModeColorMap.put(Tag.LIVING_STREET, Color.rgb(63, 64, 66));
        darkModeColorMap.put(Tag.RESIDENTIAL, Color.rgb(63, 64, 66));
        darkModeColorMap.put(Tag.SERVICE, Color.rgb(63, 64, 66));
        darkModeColorMap.put(Tag.JUNCTION, Color.rgb(63, 64, 66));
        darkModeColorMap.put(Tag.TERTIARY, Color.rgb(63, 64, 66));
        darkModeColorMap.put(Tag.TERTIARY_LINK, Color.rgb(63, 64, 66));
        darkModeColorMap.put(Tag.SECONDARY, Color.rgb(197, 183, 113));
        darkModeColorMap.put(Tag.SECONDARY_LINK, Color.rgb(197, 183, 113));
        darkModeColorMap.put(Tag.PRIMARY, Color.rgb(204, 177, 71));
        darkModeColorMap.put(Tag.PRIMARY_LINK, Color.rgb(204, 177, 71));
        darkModeColorMap.put(Tag.TRUNK, Color.rgb(204, 177, 71));
        darkModeColorMap.put(Tag.TRUNK_LINK, Color.rgb(204, 177, 71));

        //TODO: fix colors
        darkModeColorMap.put(Tag.CITYBOARDER, Color.rgb(25,26,28));
        darkModeColorMap.put(Tag.MEADOW, Color.rgb(51, 78, 63));
        darkModeColorMap.put(Tag.HEATH, Color.rgb(64,63,37));
        darkModeColorMap.put(Tag.CEMETERY, Color.rgb(34,58,45));
        darkModeColorMap.put(Tag.FERRY, Color.rgb(72,104,165));
        darkModeColorMap.put(Tag.LAKE, Color.rgb(58, 64, 93));
        darkModeColorMap.put(Tag.GRASS, Color.rgb(51, 78, 63));
        darkModeColorMap.put(Tag.SCRUB, Color.rgb(51, 78, 63));
        darkModeColorMap.put(Tag.FARMYARD, Color.rgb(37,38,40));
        darkModeColorMap.put(Tag.WATERWAY, Color.rgb(58, 64, 93));
        darkModeColorMap.put(Tag.FOREST, Color.rgb(35,60,47));
        darkModeColorMap.put(Tag.WOOD,Color.rgb(35,60,47));
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
