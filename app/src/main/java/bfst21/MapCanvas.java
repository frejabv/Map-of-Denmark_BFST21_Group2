package bfst21;

import bfst21.osm.Node;
import bfst21.osm.RenderingStyle;
import bfst21.pathfinding.Edge;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.List;

public class MapCanvas extends Canvas {
    private Model model;
    private Affine trans = new Affine();
    GraphicsContext gc;
    boolean setPin;
    Point2D canvasPoint;
    double size;
    RenderingStyle renderingStyle;
    int redrawIndex = 0;
    public long[] redrawAverage = new long[20];
    private float currentMaxX, currentMaxY, currentMinX, currentMinY;

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
        long start = System.nanoTime();
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
                if (tag.zoomLimit > getDistanceWidth()) {
                    fillable.draw(gc);
                    gc.fill();
                }
            });
        });

        model.getDrawableMap().forEach((tag, drawables) -> {
            gc.setStroke(renderingStyle.getColorByTag(tag));
            var style = renderingStyle.getDrawStyleByTag(tag);
            drawables.forEach(drawable -> {
                if (tag.zoomLimit > getDistanceWidth()) {
                    drawable.draw(gc);
                }
            });
        });

        model.getRelationIndex().forEach(relation -> {
            if (relation.getTags().size() != 0) {
                relation.draw(gc, renderingStyle);
            }
        });

        if(model.existsAStarPath()){
            debugAStarPath();
            paintPath(model.getAStarPath());
        }

        if (setPin) {
            gc.setFill(Color.rgb(231, 76, 60));
            gc.fillArc(canvasPoint.getX(), canvasPoint.getY(), 0.05 * size, 0.05 * size, -30, 240, ArcType.OPEN);
            double[] xPoints = {canvasPoint.getX() + 0.00307 * size, canvasPoint.getX() + 0.025 * size, canvasPoint.getX() + 0.04693 * size}; //+0.05
            double[] yPoints = {canvasPoint.getY() + 0.037 * size, canvasPoint.getY() + 0.076 * size, canvasPoint.getY() + 0.037 * size};
            gc.fillPolygon(xPoints, yPoints, 3);
            gc.setFill(Color.rgb(192, 57, 43));
            gc.fillOval(canvasPoint.getX() + 0.015 * size, canvasPoint.getY() + 0.015 * size, 0.020 * size, 0.020 * size);
        }
        gc.restore();
        long elapsedTime = System.nanoTime() - start;
        if (redrawIndex < 20) {
            redrawAverage[redrawIndex] = elapsedTime;
            redrawIndex++;
        } else {
            redrawIndex = 0;
        }
    }

    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    public void zoom(double factor, Point2D center) {
        setCurrentCanvasEdges();
        if (factor > 1) {
            if (getDistanceWidth() > 0.1) {
                trans.prependScale(factor, factor, center);
                repaint();
            }
        } else {
            //TODO: make the boundry go to inital zoom position
            if (getDistanceWidth() < 1000) {
                trans.prependScale(factor, factor, center);
                repaint();
            }
        }
    }

    public void debugAStarPath() {
        List<Node> nodes = model.getAStarDebugPath();
        gc.setStroke(Color.CORNFLOWERBLUE);
        gc.setLineWidth(1 / Math.sqrt(trans.determinant())*2);
        gc.beginPath();
        for(Node n : nodes) {
            for(Edge e : n.getAdjecencies()) {
                Node child = e.target;
                gc.moveTo(n.getX(), n.getY());
                gc.lineTo(child.getX(), child.getY());
            }
        }
        gc.stroke();
    }

    public void paintPath(List<Node> path){
        gc.setStroke(Color.ORANGERED);
        gc.setLineWidth(1 / Math.sqrt(trans.determinant())*3);
        gc.beginPath();
        for (int i = 0;i < path.size()-1; i++){
            Node current = path.get(i);
            Node next = path.get(i+1);
            gc.moveTo(current.getX(),current.getY());
            gc.lineTo(next.getX(),next.getY());
        }
        gc.stroke();
    }

    public String setPin(Point2D point){
        size = .3;
        canvasPoint = mouseToModelCoords(point);
        canvasPoint = new Point2D(canvasPoint.getX() - (0.025 * size), canvasPoint.getY() - (0.076 * size));
        setPin = true;
        repaint();
        return canvasPoint.getY() * -0.56f + ", " + canvasPoint.getX();
    }

    public String setPin(double x, double y) {
        size = .3;
        canvasPoint = new Point2D(x - (0.025 * size), y - (0.076 * size));
        setPin = true;
        repaint();
        return canvasPoint.getY() * -0.56f + ", " + canvasPoint.getX();
    }

    public Point2D mouseToModelCoords(Point2D point) {
        try {
            return trans.inverseTransform(point);
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void goToPosition(double minX, double maxX, double maxY) {
        trans.setToIdentity();
        pan(-minX, -maxY);
        zoom(((getHeight() - getWidth() / (maxX - minX)) * -1), new Point2D(0, 0));
        if (maxX - minX < 0.1) {
            zoom(0.01, new Point2D(getWidth() / 2, getHeight() / 2));
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

    public void setCurrentCanvasEdges() {
        currentMaxX = (float) mouseToModelCoords(new Point2D(getWidth(), 0)).getX();
        currentMinX = (float) mouseToModelCoords(new Point2D(0, 0)).getX();
        currentMaxY = (float) (mouseToModelCoords(new Point2D(0, getHeight())).getY());
        currentMinY = (float) (mouseToModelCoords(new Point2D(0, 0)).getY());
    }

    public float getDistanceWidth() {
        return (currentMaxX - currentMinX) * 111.320f * 0.56f;
    }

}
