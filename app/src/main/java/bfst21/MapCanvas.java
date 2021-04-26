package bfst21;

import bfst21.osm.Node;
import bfst21.Rtree.Rectangle;
import bfst21.osm.RenderingStyle;
import bfst21.osm.Way;
import bfst21.osm.Tag;
import bfst21.pathfinding.Edge;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

import java.util.List;

public class MapCanvas extends Canvas {
    private Model model;
    private Affine trans = new Affine();
    GraphicsContext gc;
    boolean setPin;
    boolean RTreeLines;
    Point2D canvasPoint;
    Point2D pinPoint;
    double size;
    RenderingStyle renderingStyle;
    int redrawIndex = 0;
    public long[] redrawAverage = new long[20];
    public boolean debugAStar;
    private float currentMaxX, currentMaxY, currentMinX, currentMinY;
    boolean showNames = true;
    private boolean showRoute;

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
        gc.save();
        gc.setTransform(new Affine());
        gc.setFill(renderingStyle.sea);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);
        gc.fill();
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));

        gc.setFill(renderingStyle.getIslandColor(getDistanceWidth()));
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

        model.getRelationIndex().forEach(relation -> {
            if (relation.getTags().size() != 0) {
                if (relation.getTags().get(0).zoomLimit > getDistanceWidth()) {
                    relation.draw(gc, renderingStyle);
                }
            }
        });

        model.getDrawableMap().forEach((tag, drawables) -> {
            gc.setStroke(renderingStyle.getColorByTag(tag));
            gc.setLineWidth(renderingStyle.getWidthByTag(tag) / Math.sqrt(trans.determinant()));
            var style = renderingStyle.getDrawStyleByTag(tag);
            if (drawables != null) {
                drawables.forEach(drawable -> {
                    if (tag.zoomLimit > getDistanceWidth()) {
                        drawable.draw(gc);
                    }
                    if (tag.zoomLimit / 100 > getDistanceWidth() && tag.equals(Tag.MOTORWAY)) {
                        gc.setLineWidth(.00015);
                    }
                });
            }
        });

        model.getRelationIndex().forEach(relation -> {
            if (relation.getTags().size() != 0) {
                if (relation.getTags().get(0).zoomLimit > getDistanceWidth()) {
                    relation.draw(gc, renderingStyle);
                }
            }
        });

        if(model.existsAStarPath() && showRoute){
            if(debugAStar) {
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

        if (RTreeLines) {
            //display window
            Point2D maxPoint = new Point2D(getWidth() * 3/4, getHeight() * 3/4);
            maxPoint = mouseToModelCoords(maxPoint);

            Point2D minPoint = new Point2D(getWidth() * 1/4, getHeight() * 1/4);
            minPoint = mouseToModelCoords(minPoint);

            Rectangle window = new Rectangle((float) minPoint.getX(),(float) minPoint.getY(), (float) maxPoint.getX(), (float) maxPoint.getY());
            gc.setLineWidth(1 / Math.sqrt(trans.determinant()));
            model.getRtree().drawRTree(window, gc);
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
        if (factor > 1) {
            if (getDistanceWidth() > 0.1) {
                trans.prependScale(factor, factor, center);
            }
        } else {
            // TODO: make the boundry go to inital zoom position
            if (getDistanceWidth() < 1000) {
                trans.prependScale(factor, factor, center);
            }
        }
        setCurrentCanvasEdges();
        repaint();
    }

    public void drawDebugAStarPath() {
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
    public void drawNearest() {
        gc.setStroke(Color.GREENYELLOW);
        gc.setLineWidth(3 / Math.sqrt(trans.determinant()));
        Way nearest = model.getRtree().NearestWay(pinPoint);
        System.out.println("way ID: " + nearest.getId());
        System.out.println("Node ID: " + nearest.nearestNode(pinPoint).getId() + " coordinate: "+ nearest.nearestNode(pinPoint).getY() + " " + nearest.nearestNode(canvasPoint).getX() * -0.56f);
        nearest.getRect().draw(gc);
        nearest.draw(gc);
    }

    public void showRoute(){
        showRoute = true;
        repaint();
    }
    public void hideRoute(){
        showRoute = false;
        repaint();
    }
}
