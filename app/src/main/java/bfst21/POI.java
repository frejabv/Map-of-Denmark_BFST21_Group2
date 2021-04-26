package bfst21;

import bfst21.osm.Drawable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class POI {

    private String name;
    private String type;
    private String imageType;
    protected float x, y;


    public POI(String name, String type, float x, float y) {
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public POI(String name, String type, String imageType, float x, float y){
        this.name = name;
        this.type = type;
        this.imageType = imageType;
        this.x = x;
        this.y = y;
    }

    public void changeName(String newName) {
        name = newName + " " + type;
    }

    public String getName() {
        return name;
    }

    public void changeType (String newType) {
        type = newType;
    }

    public String getType() {
        return type;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public String getImageType() {
        return imageType;
    }
}
