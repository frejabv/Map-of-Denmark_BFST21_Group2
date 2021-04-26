package bfst21.osm;

import bfst21.Rtree.Rectangle;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public interface Drawable {
    void draw(GraphicsContext gc);

    Rectangle getRect();
    List<Tag> getTags();
}
