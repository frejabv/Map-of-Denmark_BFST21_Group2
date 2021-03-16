package bfst21.osm;
import javafx.scene.paint.Color;
import java.util.ArrayList;
public class ColorScheme{
    
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
    public Color trackWay;

    public ColorScheme(){
        defaultMode();
    }

    public void defaultMode(){
        sea = Color.rgb(170, 218, 255);
        island = Color.rgb(255, 241, 178);
        park = Color.rgb(195, 236, 178);
        inlandWater = Color.LIGHTBLUE;
        buildings = Color.rgb(232, 232, 232);
        footways = Color.GREEN;
        paths = Color.GREEN;
        pedestrianWay = Color.WHITE;
        cycleway = Color.TURQUOISE;
        unclassifiedWay = Color.WHITE;
        roads = Color.WHITE;
        livingStreets = Color.WHITE;
        residentialWay = Color.WHITE;
        serviceWay = Color.WHITE;
        junction = Color.WHITE;
        tertiaryWay = Color.WHITE;
        secondaryWay = Color.YELLOW;
        primaryWay = Color.ORANGE;
        trunkWay = Color.RED;
        trackWay = Color.BROWN;
    }

    public void darkMode(){
        sea = Color.rgb(47, 53, 66); 
        island = Color.rgb(87, 96, 111);
        park = Color.rgb(116, 125, 140);
        buildings = Color.rgb(72, 84, 96);

        unclassifiedWay = Color.rgb(128, 142, 155);
        roads = Color.rgb(128, 142, 155);
        livingStreets = Color.rgb(128, 142, 155);
        residentialWay = Color.rgb(128, 142, 155);
        serviceWay = Color.rgb(128, 142, 155);
        junction = Color.rgb(128, 142, 155);
        tertiaryWay = Color.rgb(128, 142, 155);
        secondaryWay = Color.rgb(128, 142, 155);
        primaryWay = Color.rgb(128, 142, 155);
        trunkWay = Color.rgb(128, 142, 155);
        trackWay = Color.rgb(128, 142, 155);
    }

    public void deuteranopeColorMode(){
        
    }

    public void protanopeColorMode(){
        
    }

    public void tritanopeColorMode(){
    }

    public void redGreenColorBlindMode(){
        
    }
}