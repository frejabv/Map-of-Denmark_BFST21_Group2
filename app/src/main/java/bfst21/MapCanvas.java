package bfst21;

import bfst21.POI.POI;
import bfst21.Rtree.Rectangle;
import bfst21.osm.*;
import bfst21.pathfinding.Edge;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.ArrayList;
import java.util.List;

public class MapCanvas extends Canvas {
    private Model model;
    private Affine trans = new Affine();
    GraphicsContext gc;
    boolean setPin;
    public boolean kdLines;
    boolean doubleDraw;
    boolean smallerViewPort, RTreeLines, roadRectangles;
    boolean nearestNodeLine;
    public boolean debugAStar;
    private boolean showRoute;
    boolean showNames = true;
    Point2D canvasPoint;
    Point2D pinPoint;
    Point2D mousePoint = new Point2D(0, 0);
    Rectangle viewport;
    ArrayList<Drawable> activeDrawList, activeFillList, activeAreaList;
    ArrayList<POI> activePOIList;
    ArrayList<Tag> requiresMinimumAreaTagList;
    double size;
    RenderingStyle renderingStyle;
    int redrawIndex = 0;
    public long[] redrawAverage = new long[20];
    private float currentMaxX, currentMaxY, currentMinX, currentMinY;
    private float mapZoomLimit;

    public void init(Model model) {
        this.model = model;
        renderingStyle = new RenderingStyle();
        setCurrentCanvasEdges();
        initRequiresMinimumAreaTagList();
        moveToInitialPosition();
        mapZoomLimit = getDistanceWidth() * 5;
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

        float distanceWidth = getDistanceWidth();

        updateViewPort();

        activeDrawList = new ArrayList<>();
        activeFillList = new ArrayList<>();
        if (distanceWidth <= 700) {
            activeDrawList.addAll(model.getDrawableRTree700().query(viewport));
        }
        if (distanceWidth <= 400) {
            activeDrawList.addAll(model.getDrawableRTree400().query(viewport));
            activeFillList.addAll(model.getFillableRTree400().query(viewport));
        }
        if (distanceWidth <= 150) {
            activeDrawList.addAll(model.getDrawableRTree150().query(viewport));
            activeFillList.addAll(model.getFillableRTree150().query(viewport));
        }
        if (distanceWidth <= 7) {
            activeDrawList.addAll(model.getDrawableRTree7().query(viewport));
            activeFillList.addAll(model.getFillableRTree7().query(viewport));
        }
        if (distanceWidth <= 3) {
            activeDrawList.addAll(model.getDrawableRTree3().query(viewport));
        }

        activeDrawList.sort((a, b) -> Integer.compare(a.getTag().layer, b.getTag().layer));
        activeFillList.sort((a, b) -> Integer.compare(a.getTag().layer, b.getTag().layer));

        activeAreaList = new ArrayList<>();
        activeAreaList.addAll(model.getAreaTree().query(viewport));

        gc.setFill(renderingStyle.sea);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);
        gc.fill();

        gc.setStroke(Color.TRANSPARENT);
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));
        gc.setFill(renderingStyle.getIslandColor(distanceWidth));
        for (var island : model.getIslands()) {
            island.draw(gc, renderingStyle);
            gc.fill();
        }

        double minimumArea = viewport.getArea() / 50000;
        for (Drawable fillable : activeFillList) {
            if (!requiresMinimumAreaTagList.contains(fillable.getTag()) || fillable.getRect().getArea() > minimumArea) {
                Tag tag = fillable.getTag();
                gc.setStroke(renderingStyle.getColorByTag(tag));
                gc.setFill(renderingStyle.getColorByTag(tag));

                if (tag.zoomLimit > distanceWidth) {
                    fillable.draw(gc, renderingStyle);
                    gc.fill();
                }
            }
        }

        // Draw dark
        if (doubleDraw) {
            for (Drawable drawable : activeDrawList) {
                Tag tag = drawable.getTag();

                if (tag.zoomLimit > distanceWidth && renderingStyle.getDoubleDrawn(tag)) {
                    Color c1 = renderingStyle.getColorByTag(tag);
                    int darkRed = (int) (c1.getRed() * 255 * 0.75);
                    int darkGreen = (int) (c1.getGreen() * 255 * 0.75);
                    int darkBlue = (int) (c1.getBlue() * 255 * 0.75);
                    gc.setStroke(Color.rgb(darkRed, darkGreen, darkBlue));
                    gc.setLineWidth(renderingStyle.getWidthByTag(tag) / Math.sqrt(trans.determinant()));
                    if (distanceWidth < 7.0) {
                        gc.setLineWidth((renderingStyle.getWidthByTag(tag) / 13333));
                    }
                    var style = renderingStyle.getDrawStyleByTag(tag);
                    if (style != DrawStyle.DASH) {
                        drawable.draw(gc, renderingStyle);
                    }
                }
            }
        }

        // Draw normal
        for (Drawable drawable : activeDrawList) {
            Tag tag = drawable.getTag();
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

            if (distanceWidth < 7.0 && renderingStyle.getDrawStyleByTag(tag) != DrawStyle.DASH) {
                gc.setLineWidth((renderingStyle.getWidthByTag(tag) / 13333) * innerRoadWidth);
            }
            if (tag.zoomLimit > distanceWidth) {
                if (renderingStyle.getDoubleDrawn(tag) && doubleDraw) {
                    drawable.draw(gc, renderingStyle);
                }
                drawable.draw(gc, renderingStyle);
            }
        }

        if (model.existsAStarPath() && showRoute) {
            gc.setLineDashes(0);
            if (debugAStar) {
                drawDebugAStarPath();
            }
            paintPath(model.getAStarPath());
        }

        activePOIList = new ArrayList<>();
        if (distanceWidth <= 20) {
            activePOIList.addAll(model.getPOITree().query(viewport));
            activePOIList.forEach(poi -> {
                //TODO we will reduce this to poi.getType() == null in the future
                if (poi.getType().equals("place")) {
                    gc.setFill(Color.WHITE);
                    double size = (30 / Math.sqrt(trans.determinant()));
                    gc.fillOval(poi.getX() - (size / 2), poi.getY() - (size / 2), size, size);
                    gc.drawImage(new Image("bfst21/icons/heart.png"), poi.getX() - (size / 4), poi.getY() - (size / 4),
                            size / 2, size / 2);
                } else {
                    gc.setFill(Color.rgb(52, 152, 219));
                    double size = (30 / Math.sqrt(trans.determinant()));
                    gc.fillOval(poi.getX() - (size / 2), poi.getY() - (size / 2), size, size);
                    String image = poi.getImageType();
                    gc.drawImage(model.imageSet.get(image), poi.getX() - (size / 4), poi.getY() - (size / 4), size / 2, size / 2);

                    if (showNames) {
                        gc.setFill(Color.BLACK);
                        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10 / Math.sqrt(trans.determinant())));
                        gc.fillText(poi.getName(), poi.getX() + size, poi.getY());
                    }
                }
            });

            if (distanceWidth <= 150 && distanceWidth > 20) {
                model.getPointsOfInterest().forEach(POI -> {
                    gc.setFill(Color.WHITE);
                    double size = (30 / Math.sqrt(trans.determinant()));
                    gc.fillOval(POI.getX() - (size / 2), POI.getY() - (size / 2), size, size);
                    gc.drawImage(new Image("bfst21/icons/heart.png"), POI.getX() - (size / 4), POI.getY() - (size / 4),
                            size / 2, size / 2);
                });
            }

            minimumArea = viewport.getArea() / 1000;
            if (showNames) {
                gc.setLineDashes(0);
                gc.setFont(Font.font("Arial", 10 / Math.sqrt(trans.determinant())));
                for (Drawable area : activeAreaList) {
                    if (((AreaName) area).getType() != AreaType.ISLAND || area.getRect().getArea() > minimumArea) {
                        ((AreaName) area).drawType(gc, distanceWidth, renderingStyle);
                    }
                }
            }

            if (setPin) {
                double size = (30 / Math.sqrt(trans.determinant()));
                gc.drawImage(new Image("bfst21/icons/pin.png"), pinPoint.getX() - (size / 2), pinPoint.getY() - size, size,
                        size);
            }


            gc.setLineWidth((1 / Math.sqrt(trans.determinant())));
            if (kdLines) {
                model.getPOITree().drawLines(gc);
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
            if (getDistanceWidth() < mapZoomLimit) {
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
        gc.setStroke(Color.rgb(112, 161, 255));
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));
        if (getDistanceWidth() < 7.0) {
            //TODO: make it not magic
            gc.setLineWidth(0.000045);
        }
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

    private void setStyle(DrawStyle style) {
        if (style == DrawStyle.DASH) {
            gc.setLineDashes(5 / Math.sqrt(trans.determinant()));
        } else {
            gc.setLineDashes(0);
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

    private void initRequiresMinimumAreaTagList() {
        requiresMinimumAreaTagList = new ArrayList<>();
        requiresMinimumAreaTagList.add(Tag.MEADOW);
        requiresMinimumAreaTagList.add(Tag.FOREST);
        requiresMinimumAreaTagList.add(Tag.WOOD);
        requiresMinimumAreaTagList.add(Tag.GRASS);
        requiresMinimumAreaTagList.add(Tag.PARK);
        requiresMinimumAreaTagList.add(Tag.SCRUB);
        requiresMinimumAreaTagList.add(Tag.GRASSLAND);
        requiresMinimumAreaTagList.add(Tag.LAKE);
        requiresMinimumAreaTagList.add(Tag.WATER);
        requiresMinimumAreaTagList.add(Tag.HEATH);
        requiresMinimumAreaTagList.add(Tag.CEMETERY);
    }

}
