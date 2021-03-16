package bfst21;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import bfst21.osm.*;

public class MapCanvas extends Canvas {
    private Model model;
    private Affine trans = new Affine();
    RenderingStyle renderingStyle;

    public void init(Model model) {
        this.model = model;
        renderingStyle = new RenderingStyle();
        trans.setToIdentity();

        pan(-model.getMinX(), -model.getMaxY());
        zoom(getHeight() / (Math.min((model.getMaxX() - model.getMinX()), (model.getMinY() - model.getMaxY()))),
                new Point2D(0, 0));
    }

    void repaint() {
        var gc = getGraphicsContext2D();
        gc.save();
        gc.setTransform(new Affine());
        gc.setFill(renderingStyle.sea);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);
        gc.fill();
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));

        gc.setFill(Color.LIGHTYELLOW);
        for (var island : model.getIslands()) {
            island.draw(gc);
            gc.fill();
        }

        model.getDrawableMap().forEach((tag, drawables) -> {
            gc.setStroke(renderingStyle.getColorByTag(tag));
            var style = renderingStyle.getDrawStyleByTag(tag);
            drawables.forEach(drawable -> {
                drawable.draw(gc);
                if (style == DrawStyle.FILL) {
                    gc.setFill(renderingStyle.getColorByTag(tag));
                    gc.fill();
                }
            });
        });

        gc.restore();
    }

    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    public void zoom(double factor, Point2D center) {
        trans.prependScale(factor, factor, center);
        repaint();
    }

    public Point2D mouseToModelCoords(Point2D point) {
        try {
            return trans.inverseTransform(point);
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
            return null;
        }
    }

}
