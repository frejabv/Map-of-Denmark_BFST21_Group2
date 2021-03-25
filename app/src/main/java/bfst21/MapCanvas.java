package bfst21;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import bfst21.osm.*;

public class MapCanvas extends Canvas {
    private Model model;
    private Affine trans = new Affine();
    GraphicsContext gc;
    boolean setPin;
    Point2D canvasPoint;
    double size;
    RenderingStyle renderingStyle;

    public void init(Model model) {
        this.model = model;
        renderingStyle = new RenderingStyle();
        moveToInitialPosition();
        widthProperty().addListener((obs, oldVal, newVal) -> {
            pan(((Double) newVal - (Double) oldVal) / 2, 0);
        });
        heightProperty().addListener((obs, oldVal, newVal) -> {
            pan(0, ((Double) newVal - (Double) oldVal) / 2);
        });
    }

    void repaint() {
        gc = getGraphicsContext2D();
        gc.save();
        gc.setTransform(new Affine());
        gc.setFill(renderingStyle.sea);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);
        gc.fill();
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));

        gc.setFill(renderingStyle.island);
        for (var island : model.getIslands()) {
            island.draw(gc);
            gc.fill();
        }

        model.getFillMap().forEach((tag, fillables) -> {
            gc.setStroke(renderingStyle.getColorByTag(tag));
            gc.setFill(renderingStyle.getColorByTag(tag));

            fillables.forEach(fillable -> {
                fillable.draw(gc);
                gc.fill();
            });

        });

        model.getDrawableMap().forEach((tag, drawables) -> {
            gc.setStroke(renderingStyle.getColorByTag(tag));
            var style = renderingStyle.getDrawStyleByTag(tag);
            drawables.forEach(drawable -> {
                drawable.draw(gc);
            });
        });

        model.getRelationIndex().forEach(relation -> {
            System.out.println("Relation draw called");
            relation.draw(gc, renderingStyle);
            //System.out.println("Relation draw finished");
        });

        if(setPin){
            gc.setFill(Color.rgb(231, 76, 60));
            gc.fillArc(canvasPoint.getX(), canvasPoint.getY(), 0.05*size, 0.05*size, -30, 240, ArcType.OPEN);
            double[] xPoints = {canvasPoint.getX()+0.00307*size,canvasPoint.getX()+0.025*size,canvasPoint.getX() + 0.04693*size}; //+0.05
            double[] yPoints = {canvasPoint.getY()+0.037*size,canvasPoint.getY()+0.076*size,canvasPoint.getY()+0.037*size};
            gc.fillPolygon(xPoints, yPoints, 3);
            gc.setFill(Color.rgb(192, 57, 43));
            gc.fillOval(canvasPoint.getX()+0.015*size,canvasPoint.getY()+0.015*size,0.020*size,0.020*size);
        }
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

    public String setPin(Point2D point){
        size = .3;
        canvasPoint = mouseToModelCoords(point);
        canvasPoint = new Point2D(canvasPoint.getX()-(0.025*size),canvasPoint.getY()-(0.076*size));
        setPin = true;
        repaint();
        return canvasPoint.getY()*-0.56f + ", " + canvasPoint.getX();
    }

    public Point2D mouseToModelCoords(Point2D point) {
        try {
            return trans.inverseTransform(point);
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void goToPosition(double minX, double maxX, double maxY){
            trans.setToIdentity();
            pan(-minX, -maxY);
            zoom(((getHeight() - getWidth() / (maxX - minX)) * -1), new Point2D(0, 0));
            if(maxX-minX < 0.1){
                zoom(0.01, new Point2D(getWidth()/2, getHeight()/2));
            }
    }

    private void moveToInitialPosition() {
        double deltaY = model.getMaxY() - model.getMinY();
        double deltaX = model.getMaxX() - model.getMinX();
        trans.setToIdentity();
        if (deltaX < deltaY) {
            pan(-model.getMinX(), -model.getMaxY());
            zoom((getHeight() - getWidth() / (model.getMaxX() - model.getMinX())) * -1, new Point2D(0, 0));
            pan(-(model.getMinY() - (model.getMaxX())), 0);
        } else {
            pan(-model.getMinX(), -model.getMaxY());
            zoom(((getWidth() / (model.getMinX() - model.getMaxX())) * -1), new Point2D(0, 0));
            pan(0, -(model.getMaxX() - (-model.getMinY() / 2)));
        }
    }

}
