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
    private Map<Tag, Double> widthMap;
    private Theme theme;

    public Color sea;
    public Color islandFar;
    public Color islandClose;

    public RenderingStyle() {
        defaultColorMap = new HashMap<>();
        darkModeColorMap = new HashMap<>();
        styleMap = new HashMap<>();
        widthMap = new HashMap<>();
        theme = theme.DEFAULT;

        styleMap.put(Tag.BUILDING, DrawStyle.FILL);
        styleMap.put(Tag.WATER, DrawStyle.FILL);
        styleMap.put(Tag.PARK, DrawStyle.FILL);

        widthMap.put(Tag.MOTORWAY,3.0);
        widthMap.put(Tag.PRIMARY,2.0);
        widthMap.put(Tag.SECONDARY,1.5);

        genDefaultMode();
        genDarkMode();
        defaultMode();
    }

    public Color getIslandColor(float distanceWidth){
        if(distanceWidth >= 7.0){
            return islandFar;
        }
        else{
            return islandClose;
        }
    }

    public void defaultMode() {
        sea = Color.rgb(170, 218, 255);
        islandFar = Color.rgb(187, 226, 198);
        islandClose = Color.rgb(250,250,250);
        theme = theme.DEFAULT;
    }

    public void darkMode() {
        sea = Color.rgb(47, 53, 66);
        islandFar = Color.rgb(87, 96, 111);
        theme = theme.DARK;
    }

    private void genDefaultMode() {
        defaultColorMap.put(Tag.BUILDING, Color.rgb(241, 241, 241));
        defaultColorMap.put(Tag.PARK, Color.rgb(168, 218, 181));
        defaultColorMap.put(Tag.WATER, Color.LIGHTBLUE);
        defaultColorMap.put(Tag.FOOTWAY, Color.GREEN);
        defaultColorMap.put(Tag.PATH, Color.GREEN);
        defaultColorMap.put(Tag.MOTORWAY, Color.rgb(	255, 181, 20));
        defaultColorMap.put(Tag.CYCLEWAY, Color.TURQUOISE);

        defaultColorMap.put(Tag.PEDESTRIAN, Color.WHITE);
        defaultColorMap.put(Tag.UNCLASSIFIED, Color.WHITE);
        defaultColorMap.put(Tag.ROAD, Color.WHITE);
        defaultColorMap.put(Tag.LIVING_STREET, Color.WHITE);
        defaultColorMap.put(Tag.RESIDENTIAL, Color.WHITE);
        defaultColorMap.put(Tag.SERVICE, Color.WHITE);
        defaultColorMap.put(Tag.JUNCTION, Color.WHITE);
        defaultColorMap.put(Tag.TERTIARY, Color.WHITE);
        defaultColorMap.put(Tag.SECONDARY, Color.rgb(189, 195, 199));
        defaultColorMap.put(Tag.PRIMARY, Color.rgb(253, 218, 118));
        defaultColorMap.put(Tag.TRUNK, Color.WHITE);
        defaultColorMap.put(Tag.TRACK, Color.BROWN);
    }

    private void genDarkMode() {

        darkModeColorMap.put(Tag.WATER, Color.LIGHTBLUE);
        darkModeColorMap.put(Tag.PARK, Color.rgb(116, 125, 140));
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
}
