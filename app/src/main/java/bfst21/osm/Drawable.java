package bfst21.osm;

import javafx.scene.canvas.GraphicsContext;

public interface Drawable {
    void draw(GraphicsContext gc);

    void setRectangle(float minX, float minY, float maxX, float maxY);
}
