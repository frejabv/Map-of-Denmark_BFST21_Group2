package bfst21.osm;

import bfst21.Rtree.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

enum AreaType {
    BOROUGH(30, 0),
    CITY(250, 0),
    HAMLET(20, 0),
    ISLAND(250, 20),
    ISLET(10, 0),
    NEIGHBOURHOOD(15, 0),
    QUARTER(30, 0),
    SUBURB(30, 0),
    TOWN(50, 0),
    VILLAGE(50, 0);

    public float zoomMax;
    public float zoomMin;

    AreaType(float zoomMax, float zoomMin) {
        this.zoomMax = zoomMax;
        this.zoomMin = zoomMin;
    }
}

public class AreaName implements Drawable {
    String name;
    AreaType type;
    float lat;
    float lon;
    Relation relation;
    Way way;


    public AreaName(String name, AreaType type, Node node) {
        setNameAndType(name, type);
        this.lat = node.getX();
        this.lon = node.getY();
    }

    public AreaName(String name, AreaType type, Way way) {
        setNameAndType(name, type);
        this.way = way;
    }

    public AreaName(String name, AreaType type, Relation relation) {
        setNameAndType(name, type);
        this.relation = relation;
    }

    public void setNameAndType(String name, AreaType type) {
        this.name = name;
        this.type = type;
    }

    public void drawType(GraphicsContext gc, float distanceWidth, RenderingStyle renderingStyle) {
        if (distanceWidth < type.zoomMax && distanceWidth > type.zoomMin) {
            draw(gc,renderingStyle);
        }
    }

    @Override
    public void draw(GraphicsContext gc, RenderingStyle renderingStyle) {
        if (lon == 0.0 && lat == 0.0) {
            if (relation != null) {
                lat = (relation.getRect().getMaxX() + relation.getRect().getMinX()) / 2;
                lon = ((relation.getRect().getMaxY() + relation.getRect().getMinY()) / 2);
                relation = null;
            } else {
                lat = (way.getRect().getMaxX() + way.getRect().getMinX()) / 2;
                lon = ((way.getRect().getMaxY() + way.getRect().getMinY()) / 2);
                way = null;
            }
        }

        gc.setStroke(Color.rgb(236, 240, 241));
        gc.setFill(Color.rgb(45, 52, 54));
        if (type.equals(AreaType.ISLAND)) {
            gc.setFont(Font.font("Arial", FontWeight.BOLD, gc.getFont().getSize() * 2));
            gc.setLineWidth(gc.getFont().getSize() / 6);
            gc.strokeText(name, lat, lon);
            gc.fillText(name, lat, lon);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, gc.getFont().getSize() / 2));
        } else {
            gc.setFont(Font.font("Arial", FontWeight.BOLD, gc.getFont().getSize()));
            gc.setLineWidth(gc.getFont().getSize() / 6);
            gc.strokeText(name, lat, lon);
            gc.fillText(name, lat, lon);
        }
    }

    @Override
    public Rectangle getRect() {
        if (relation != null) {
            return relation.getRect();
        } else if (way != null) {
            return way.getRect();
        } else {
            return new Rectangle(lon, lat, lon, lat);
        }
    }

    //TODO remove when member class is removed
    @Override
    public Tag getTag() {
        return null;
    }
}
