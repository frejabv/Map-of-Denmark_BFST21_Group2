package bfst21.osm;

import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

enum Theme {
    DEFAULT, DARK,
}

public class ColorScheme {
    private Map<Tag, Color> defaultColorMap;
    private Map<Tag, DrawStyle> styleMap;
    private Theme theme;

    public Color sea;
    public Color island;
    public Color park;
    public Color inlandWater;
    public Color buildings;
    public Color footways;
    public Color paths;
    public Color pedestrianWay;
    public Color cycleway;
    public Color unclassifiedWay;
    public Color roads;
    public Color livingStreets;
    public Color residentialWay;
    public Color serviceWay;
    public Color junction;
    public Color tertiaryWay;
    public Color secondaryWay;
    public Color primaryWay;
    public Color trunkWay;

    public ColorScheme() {
        defaultColorMap = new HashMap<>();
        styleMap = new HashMap<>();
        theme = theme.DEFAULT;

        styleMap.put(Tag.BUILDING, DrawStyle.FILL);
        styleMap.put(Tag.WATER, DrawStyle.FILL);

        defaultMode();
    }

    public void defaultMode() {
        // These are special cases that are not related to osm tags
        sea = Color.rgb(170, 218, 255);
        island = Color.rgb(255, 241, 178);

        defaultColorMap.put(Tag.BUILDING, Color.rgb(232, 232, 232));
        defaultColorMap.put(Tag.PARK,Color.rgb(195, 236, 178) );
        defaultColorMap.put(Tag.WATER,  Color.LIGHTBLUE);
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
    }

    public void darkMode() {

    }

    public void redGreenColorBlindMode() {

    }

    public Color getColorByTag(Tag tag) {
        switch (theme) {
        case DEFAULT:
        default:
            var color = defaultColorMap.get(tag);
            return color == null ? Color.GRAY : color;
        }
    }

    public DrawStyle getDrawStyleByTag(Tag tag) {
        var style = styleMap.get(tag);

        return style == null ? DrawStyle.STROKE : style;
    }
}
