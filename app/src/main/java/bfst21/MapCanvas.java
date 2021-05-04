package bfst21;

import bfst21.osm.*;
import bfst21.Rtree.Rectangle;
import bfst21.pathfinding.Edge;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.ArrayList;
import java.util.List;

public class MapCanvas extends Canvas {
    public boolean debugAStar;
    public long[] redrawAverage = new long[20];
    GraphicsContext gc;
    boolean setPin;
    boolean smallerViewPort, RTreeLines, roadRectangles;
    boolean nearestNodeLine;
    boolean doubleDraw;
    boolean showNames = true;
    Point2D canvasPoint;
    Point2D pinPoint;
    Point2D mousePoint = new Point2D(0, 0);
    Rectangle viewport;
    ArrayList<Drawable> activeFillList, activeDrawList;
    double size;
    RenderingStyle renderingStyle;
    int redrawIndex = 0;
    private Model model;
    private Affine trans = new Affine();
    private boolean showRoute;
    private float currentMaxX, currentMaxY, currentMinX, currentMinY;

    public void init(Model model) {
        this.model = model;
        renderingStyle = new RenderingStyle();
        setCurrentCanvasEdges();
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
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.save();
        gc.setTransform(new Affine());

        updateViewPort();
        // Rtree queries
        activeFillList = new ArrayList<>();
        activeFillList.addAll(model.getFillRTree().query(viewport));
        activeFillList.sort((a, b) -> Integer.compare(a.getTag().layer, b.getTag().layer));

        activeDrawList = new ArrayList<>();
        activeDrawList.addAll(model.getRoadRTree().query(viewport));
        activeDrawList.sort((a, b) -> Integer.compare(a.getTag().layer, b.getTag().layer));

        gc.setFill(renderingStyle.sea);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);
        gc.fill();

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));

        gc.setFill(renderingStyle.getIslandColor(getDistanceWidth()));
        for (var island : model.getIslands()) {
            island.draw(gc);
            gc.fill();
        }

        for (Drawable fillable : activeFillList) {
            Tag tag = fillable.getTag();
            gc.setStroke(renderingStyle.getColorByTag(tag));
            gc.setFill(renderingStyle.getColorByTag(tag));

            if (tag.zoomLimit > getDistanceWidth()) {
                fillable.draw(gc);
                gc.fill();
            }

        }

        //TODO - make part of R-tree
        model.getRelationIndex().forEach(relation -> {
            if (relation.getTag() != null) {
                if (relation.getTag().zoomLimit > getDistanceWidth()) {
                    relation.draw(gc, renderingStyle);
                }
            }
        });

        // Draw dark
        if (doubleDraw) {
            for (Drawable road : activeDrawList) {
                Tag tag = road.getTag();

                if (tag.zoomLimit > getDistanceWidth() && renderingStyle.getDoubleDrawn(tag)) {
                    Color c1 = renderingStyle.getColorByTag(tag);
                    int darkRed = (int) (c1.getRed() * 255 * 0.75);
                    int darkGreen = (int) (c1.getGreen() * 255 * 0.75);
                    int darkBlue = (int) (c1.getBlue() * 255 * 0.75);
                    gc.setStroke(Color.rgb(darkRed, darkGreen, darkBlue));
                    gc.setLineWidth(renderingStyle.getWidthByTag(tag) / Math.sqrt(trans.determinant()));
                    if (getDistanceWidth() < 7.0) {
                        gc.setLineWidth((renderingStyle.getWidthByTag(tag) / 13333));
                    }
                    var style = renderingStyle.getDrawStyleByTag(tag);
                    if (style != DrawStyle.DASH) {
                        road.draw(gc);
                    }
                }
            }
        }

        // Draw normal
        for (Drawable road : activeDrawList) {
            Tag tag = road.getTag();
            double innerRoadWidth = 1;
            if (doubleDraw) {
                innerRoadWidth = 0.65;
            }
            gc.setStroke(renderingStyle.getColorByTag(tag));
            if (renderingStyle.getDoubleDrawn(tag)) {
                gc.setLineWidth(renderingStyle.getWidthByTag(tag) / Math.sqrt(trans.determinant()) * 0.5);
            } else {
                gc.setLineWidth(renderingStyle.getWidthByTag(tag) / Math.sqrt(trans.determinant()));
            }
            setStyle(renderingStyle.getDrawStyleByTag(tag));
            //if (drawables != null) {
            double finalInnerRoadWidth = innerRoadWidth;

            if (getDistanceWidth() < 7.0 && renderingStyle.getDrawStyleByTag(tag) != DrawStyle.DASH) {
                gc.setLineWidth((renderingStyle.getWidthByTag(tag) / 13333) * finalInnerRoadWidth);
            }
            if (tag.zoomLimit > getDistanceWidth()) {
                if (renderingStyle.getDoubleDrawn(tag) && doubleDraw) {
                    road.draw(gc);
                }
                road.draw(gc);
            }
        }

        if (model.existsAStarPath() && showRoute) {
            if (debugAStar) {
                drawDebugAStarPath();
            }
            paintPath(model.getAStarPath());
        }

        if (showNames) {
            gc.setFont(Font.font("Arial", 10 / Math.sqrt(trans.determinant())));
            model.getCities().forEach((city) -> {
                city.drawType(gc, getDistanceWidth());
            });
        }

        model.getPointsOfInterest().forEach(POI -> {
            gc.setFill(Color.WHITE);
            double size = (30 / Math.sqrt(trans.determinant()));
            gc.fillOval(POI.getX() - (size / 2), POI.getY() - (size / 2), size, size);
            gc.drawImage(new Image("bfst21/icons/heart.png"), POI.getX() - (size / 4), POI.getY() - (size / 4),
                    size / 2, size / 2);
            switch (POI.getType().toLowerCase()) {
                case "home":
                    // draw home icon
                    break;
                case "work":
                    // draw briefcase icon
                    break;
                default:
                    // draw generic icon
            }
        });

        if (setPin) {
            double size = (30 / Math.sqrt(trans.determinant()));
            gc.drawImage(new Image("bfst21/icons/pin.png"), pinPoint.getX() - (size / 2), pinPoint.getY() - size, size,
                    size);
        }


        gc.setLineWidth((1 / Math.sqrt(trans.determinant())));
        gc.setLineDashes(0);
        if (smallerViewPort) {
            gc.setStroke(Color.BLACK);
            viewport.draw(gc);
        }

        if (RTreeLines) {
            gc.setStroke(Color.RED);
            model.getRoadRTree().drawRTree(viewport, gc);
        }

        if (roadRectangles) {
            gc.setStroke(Color.PURPLE);
            model.getRoadRTree().drawRoadRectangles(viewport, gc);
        }

        if (smallerViewPort || RTreeLines || roadRectangles) {
            gc.setStroke(Color.BLACK);
            viewport.draw(gc);
        }

        if (nearestNodeLine) {
            gc.setStroke(Color.RED);
            gc.setLineWidth((2 / Math.sqrt(trans.determinant())));

            gc.beginPath();
            gc.moveTo(mousePoint.getX(), mousePoint.getY());
            gc.lineTo(model.getNearestNode().getX(), model.getNearestNode().getY());
            gc.stroke();
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

    private void updateViewPort() {
        Point2D origo;
        Point2D limit;
        if (smallerViewPort || RTreeLines || roadRectangles) {
            origo = mouseToModelCoords(new Point2D(getWidth() * 1/4, getHeight()* 1/4));
            limit = mouseToModelCoords(new Point2D(getWidth() * 3/4, getHeight() * 3/4));
        } else {
            origo = mouseToModelCoords(new Point2D(0, 0));
            limit = mouseToModelCoords(new Point2D(getWidth(), getHeight()));
        }

        Rectangle vp = new Rectangle((float) origo.getX(), (float) origo.getY(), (float) limit.getX(), (float) limit.getY());
        viewport = vp;
    }

    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    public void zoom(double factor, Point2D center) {
        if (factor > 1) {
            if (getDistanceWidth() > 0.1) {
                trans.prependScale(factor, factor, center);
            }
        } else {
            // TODO: make the boundry go to inital zoom position
            if (getDistanceWidth() < 1000000000) {
                trans.prependScale(factor, factor, center);
            }
        }
        setCurrentCanvasEdges();
        repaint();
    }

    public void drawDebugAStarPath() {
        List<Node> nodes = model.getAStarDebugPath();
        gc.setStroke(Color.CORNFLOWERBLUE);
        gc.setLineWidth(2 / Math.sqrt(trans.determinant()));
        gc.beginPath();
        for (Node n : nodes) {
            for (Edge e : n.getAdjacencies()) {
                Node child = e.target;
                gc.moveTo(n.getX(), n.getY());
                gc.lineTo(child.getX(), child.getY());
            }
        }
        gc.stroke();
    }

    public void paintPath(List<Node> path) {
        gc.setStroke(Color.ORANGERED);
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()) * 3);
        gc.beginPath();
        for (int i = 0; i < path.size() - 1; i++) {
            Node current = path.get(i);
            Node next = path.get(i + 1);
            gc.moveTo(current.getX(), current.getY());
            gc.lineTo(next.getX(), next.getY());
        }
        gc.stroke();
    }

    public String setPin(Point2D point) {
        size = .3;
        canvasPoint = mouseToModelCoords(point);
        pinPoint = canvasPoint;
        canvasPoint = new Point2D(canvasPoint.getX() - (0.025 * size), canvasPoint.getY() - (0.076 * size));
        setPin = true;
        repaint();
        return canvasPoint.getY() * -Model.scalingConstant + ", " + canvasPoint.getX();
    }

    public String setPin(double x, double y) {
        size = .3;
        pinPoint = new Point2D(x, y);
        canvasPoint = new Point2D(x - (0.025 * size), y - (0.076 * size));
        setPin = true;
        repaint();
        return canvasPoint.getY() * -Model.scalingConstant + ", " + canvasPoint.getX();
    }

    private void setStyle(DrawStyle style) {
        if (style == DrawStyle.DASH) {
            gc.setLineDashes(5 / Math.sqrt(trans.determinant()));
        } else {
            gc.setLineDashes(0);
        }
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
        return (currentMaxX - currentMinX) * 111.320f * Model.scalingConstant;
    }

    public Point2D getPinPoint() {
        return pinPoint;
    }

    public void showRoute() {
        showRoute = true;
        repaint();
    }

    public void hideRoute() {
        showRoute = false;
        repaint();
    }
}
