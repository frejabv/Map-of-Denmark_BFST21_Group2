package bfst21.osm;

import bfst21.Rtree.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.Serializable;

public class AreaName implements Drawable, Serializable {
    private String name;
    private AreaType type;
    private float lat;
    private float lon;
    private Relation relation;
    private Way way;


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
            draw(gc, renderingStyle);
        }
    }

    @Override
    public void draw(GraphicsContext gc, RenderingStyle renderingStyle) {
        if (lon == 0.0 && lat == 0.0) {
            if (relation != null) {
                lat = (relation.getRect().getMaxX() + relation.getRect().getMinX()) / 2;
                lon = ((relation.getRect().getMaxY() + relation.getRect().getMinY()) / 2);
            } else {
                lat = (way.getRect().getMaxX() + way.getRect().getMinX()) / 2;
                lon = ((way.getRect().getMaxY() + way.getRect().getMinY()) / 2);
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

    public AreaType getType() {
        return type;
    }

    @Override
    public Tag getTag() {
        return null;
    }
}
