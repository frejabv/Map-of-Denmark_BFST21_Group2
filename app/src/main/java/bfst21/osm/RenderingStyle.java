package bfst21.osm;

import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

enum Theme {
    DEFAULT, DARK,
}

public class RenderingStyle {
    private Map<Tag, Color> defaultColorMap;
    private Map<Tag, Color> darkModeColorMap;
    private Map<Tag, DrawStyle> styleMap;
    private Map<Tag, Integer> widthMap;
    private Theme theme;

    public Color sea;
    public Color island;

    public RenderingStyle() {
        defaultColorMap = new HashMap<>();
        darkModeColorMap = new HashMap<>();
        styleMap = new HashMap<>();
        theme = theme.DEFAULT;

        styleMap.put(Tag.BUILDING, DrawStyle.FILL);
        styleMap.put(Tag.WATER, DrawStyle.FILL);

        styleMap.put(Tag.CEMETERY, DrawStyle.FILL);
        //styleMap.put(Tag.FARMLAND, DrawStyle.FILL);
        styleMap.put(Tag.FOREST, DrawStyle.FILL);
        styleMap.put(Tag.GRASS, DrawStyle.FILL);
        styleMap.put(Tag.GRASSLAND, DrawStyle.FILL);
        styleMap.put(Tag.GRAVE_YARD, DrawStyle.FILL);
        styleMap.put(Tag.HEATH, DrawStyle.FILL);
        styleMap.put(Tag.MEADOW, DrawStyle.FILL);
        styleMap.put(Tag.ORCHARD, DrawStyle.FILL);
        styleMap.put(Tag.PARK, DrawStyle.FILL);
        styleMap.put(Tag.SCRUB, DrawStyle.FILL);

        genDefaultMode();
        genDarkMode();
        defaultMode();
    }

    public void defaultMode() {
        sea = Color.rgb(170, 218, 255);
        island = Color.rgb(255, 241, 178);
        theme = theme.DEFAULT;
    }

    public void darkMode() {
        sea = Color.rgb(47, 53, 66);
        island = Color.rgb(87, 96, 111);
        theme = theme.DARK;
    }

    private void genDefaultMode() {
        defaultColorMap.put(Tag.BUILDING, Color.rgb(232, 232, 232));
        defaultColorMap.put(Tag.WATER, Color.LIGHTBLUE);

        defaultColorMap.put(Tag.STREAM, Color.LIGHTBLUE);
        defaultColorMap.put(Tag.RIVER, Color.LIGHTBLUE);

        defaultColorMap.put(Tag.CEMETERY, Color.CRIMSON);
        //defaultColorMap.put(Tag.FARMLAND, Color.rgb(247, 255, 178));
        defaultColorMap.put(Tag.FOREST, Color.YELLOW);
        defaultColorMap.put(Tag.GRASS, Color.AQUAMARINE);
        defaultColorMap.put(Tag.GRASSLAND, Color.AQUA);
        defaultColorMap.put(Tag.GRAVE_YARD, Color.CRIMSON);
        defaultColorMap.put(Tag.HEATH, Color.FUCHSIA);
        defaultColorMap.put(Tag.MEADOW, Color.CORNFLOWERBLUE);
        defaultColorMap.put(Tag.ORCHARD, Color.DARKORCHID);
        defaultColorMap.put(Tag.PARK, Color.rgb(195, 236, 178));
        defaultColorMap.put(Tag.SCRUB, Color.RED);


        defaultColorMap.put(Tag.FOOTWAY, Color.GREEN);
        defaultColorMap.put(Tag.PATH, Color.GREEN);
        defaultColorMap.put(Tag.CYCLEWAY, Color.TURQUOISE);

        defaultColorMap.put(Tag.PEDESTRIAN, Color.WHITE);
        defaultColorMap.put(Tag.UNCLASSIFIED, Color.WHITE);
        defaultColorMap.put(Tag.ROAD, Color.WHITE);
        defaultColorMap.put(Tag.LIVING_STREET, Color.WHITE);
        defaultColorMap.put(Tag.RESIDENTIAL, Color.WHITE);
        defaultColorMap.put(Tag.SERVICE, Color.WHITE);
        defaultColorMap.put(Tag.JUNCTION, Color.WHITE);
        defaultColorMap.put(Tag.TERTIARY, Color.WHITE);
        defaultColorMap.put(Tag.SECONDARY, Color.WHITE);
        defaultColorMap.put(Tag.PRIMARY, Color.WHITE);
        defaultColorMap.put(Tag.TRUNK, Color.WHITE);
        defaultColorMap.put(Tag.TRACK, Color.BROWN);
    }

    private void genDarkMode() {

        darkModeColorMap.put(Tag.WATER, Color.LIGHTBLUE);
        darkModeColorMap.put(Tag.PARK, Color.rgb(116, 125, 140));
        darkModeColorMap.put(Tag.FOREST, Color.rgb(116, 125, 140));
        darkModeColorMap.put(Tag.GRASS, Color.rgb(116, 125, 140));
        darkModeColorMap.put(Tag.GRASSLAND, Color.rgb(116, 125, 140));
        darkModeColorMap.put(Tag.FOOTWAY, Color.GREEN);
        darkModeColorMap.put(Tag.PATH, Color.GREEN);
        darkModeColorMap.put(Tag.BUILDING, Color.rgb(72, 84, 96));
        darkModeColorMap.put(Tag.CYCLEWAY, Color.TURQUOISE);

        darkModeColorMap.put(Tag.PEDESTRIAN, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.UNCLASSIFIED, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.ROAD, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.LIVING_STREET, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.RESIDENTIAL, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.SERVICE, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.JUNCTION, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.TERTIARY, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.SECONDARY, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.PRIMARY, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.TRUNK, Color.rgb(128, 142, 155));
        darkModeColorMap.put(Tag.TRACK, Color.BROWN);
    }

    public void deuteranopeColorMode() {

    }

    public void protanopeColorMode() {

    }

    public void tritanopeColorMode() {
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

    public DrawStyle getDrawStyleByTag(Tag tag) {
        var style = styleMap.get(tag);

        return style == null ? DrawStyle.STROKE : style;
    }
}
