package bfst21.osm;

import bfst21.Rtree.Rectangle;
import javafx.scene.canvas.GraphicsContext;

public interface Drawable {
    void draw(GraphicsContext gc, RenderingStyle renderingStyle);

    Rectangle getRect();

    Tag getTag();
}
