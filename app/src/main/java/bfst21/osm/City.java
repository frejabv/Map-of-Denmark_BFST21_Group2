package bfst21.osm;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

enum CityTypes{
    BOROUGH(30,0),
    CITY(250,0),
    HAMLET(20,0),
    ISLAND(250,20),
    ISLET(10,0),
    NEIGHBOURHOOD(15,0),
    QUARTER(30,0),
    SUBURB(30,0),
    TOWN(50,0),
    VILLAGE(50,0);

    public float zoomMax;
    public float zoomMin;

    CityTypes(float zoomMax, float zoomMin){
        this.zoomMax = zoomMax;
        this.zoomMin = zoomMin;
    }
}

public class City implements Drawable{
    String name;
    CityTypes type;
    float lat;
    float lon;
    public City(String name,CityTypes type, float lat, float lon){
        this.name = name;
        this.type = type;
        this.lat = lat;
        this.lon = lon;
    }

    public void drawType(GraphicsContext gc, float distanceWidth){
        if(distanceWidth < type.zoomMax && distanceWidth > type.zoomMin){
            draw(gc);
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setStroke(Color.rgb(236, 240, 241));
        gc.setFill(Color.rgb(45, 52, 54));
        if (type.equals(CityTypes.ISLAND)){
            gc.setFont(Font.font("Arial", FontWeight.BOLD,gc.getFont().getSize()*2));
            gc.setLineWidth(gc.getFont().getSize()/6);
            gc.strokeText(name,lat,lon);
            gc.fillText(name,lat,lon);
            gc.setFont(Font.font("Arial", FontWeight.BOLD,gc.getFont().getSize()/2));
        }
        else {
            gc.setFont(Font.font("Arial", FontWeight.BOLD,gc.getFont().getSize()));
            gc.setLineWidth(gc.getFont().getSize()/6);
            gc.strokeText(name,lat,lon);
            gc.fillText(name,lat,lon);
        }
    }
}
