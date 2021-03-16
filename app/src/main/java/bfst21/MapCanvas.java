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
    ColorScheme colorScheme;

    public void init(Model model) {
        this.model = model;
        colorScheme = new ColorScheme();
        moveToInitialPosition();
        widthProperty().addListener((obs, oldVal, newVal) -> {
            pan(((Double)newVal -(Double)oldVal)/2,0);
        });
        heightProperty().addListener((obs, oldVal, newVal) -> {
            pan(0,((Double)newVal-(Double)oldVal)/2);
        });
    }

    void repaint() {
        var gc = getGraphicsContext2D();
        gc.save();
        gc.setTransform(new Affine());
        gc.setFill(colorScheme.sea);
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(trans);
        gc.fill();
        gc.setFill(colorScheme.island);
        gc.setLineWidth(1 / Math.sqrt(trans.determinant()));
        gc.setStroke(colorScheme.island);
        for (var island : model.getIslands()) {
            island.draw(gc);
            gc.fill();
        }

        gc.setStroke(Color.BLACK);
        for (var line : model.getUndefinedDrawables()) {
            line.draw(gc);
        }

        gc.setStroke(colorScheme.park); //Park
        gc.setFill(colorScheme.park); //Park
        for (var park : model.getParks()) {
            park.draw(gc);
            gc.fill();
        }

        gc.setStroke(colorScheme.inlandWater); //Inland water
        gc.setFill(colorScheme.inlandWater); //Inland water
        for (var line : model.getWater()) {
            line.draw(gc);
            gc.fill();
        }

        gc.setStroke(colorScheme.buildings);
        gc.setFill(colorScheme.buildings);
        for (var building : model.getBuildings()){
            building.draw(gc);
            gc.fill();
        }

        //roads added from smallest to largest
        gc.setStroke(colorScheme.footways);
        gc.setFill(colorScheme.footways);
        for (var line : model.getFootways()) {
            line.draw(gc);
        }

        gc.setStroke(colorScheme.paths);
        gc.setFill(colorScheme.paths);
        for (var line : model.getPaths()) {
            line.draw(gc);
        }

        gc.setStroke(colorScheme.pedestrianWay);
        gc.setFill(colorScheme.pedestrianWay);
        for (var line : model.getPedestrianWays()) {
            line.draw(gc);
        }

        gc.setStroke(colorScheme.cycleway);
        gc.setFill(colorScheme.cycleway);
        for (var line : model.getCycleways()) {
            line.draw(gc);
        }

        gc.setStroke(colorScheme.unclassifiedWay);
        gc.setFill(colorScheme.unclassifiedWay);
        for (var line : model.getUnclassifiedWays()) {
            line.draw(gc);
        }

        gc.setStroke(colorScheme.roads);
        gc.setFill(colorScheme.roads);
        for (var line : model.getRoads()) {
            line.draw(gc);
        }

        gc.setStroke(colorScheme.livingStreets);
        gc.setFill(colorScheme.livingStreets);
        for (var line : model.getLiving_streets()) {
            line.draw(gc);
        }

        gc.setStroke(colorScheme.residentialWay);
        gc.setFill(colorScheme.residentialWay);
        for (var line : model.getResidentialWays()) {
            line.draw(gc);
        }

        gc.setStroke(colorScheme.serviceWay);
        gc.setFill(colorScheme.serviceWay);
        for (var line : model.getServiceWays()) {
            line.draw(gc);
        }

        gc.setStroke(colorScheme.junction);
        gc.setFill(colorScheme.junction);
        for (var line : model.getJunctions()) {
            line.draw(gc);
        }

        gc.setStroke(colorScheme.tertiaryWay);
        gc.setFill(colorScheme.tertiaryWay);
        for (var line : model.getTertiaryWays()) {
            var oldLineWidth = gc.getLineWidth();
            gc.setLineWidth(1/Math.sqrt(trans.determinant())*2);
            line.draw(gc);
            gc.setLineWidth(oldLineWidth);
        }

        gc.setStroke(colorScheme.secondaryWay);
        gc.setFill(colorScheme.secondaryWay);
        for (var line : model.getSecondaryWays()) {
            line.draw(gc);
        }

        gc.setStroke(colorScheme.primaryWay);
        gc.setFill(colorScheme.primaryWay);
        for (var line : model.getPrimaryWays()) {
            line.draw(gc);
        }

        gc.setStroke(colorScheme.trunkWay);
        gc.setFill(colorScheme.trunkWay);
        for (var line : model.getTrunkWays()) {
            line.draw(gc);
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

    public Point2D mouseToModelCoords(Point2D point) {
        try {
            return trans.inverseTransform(point);
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void moveToInitialPosition(){
        double deltaY = (model.getMaxY())-(model.getMinY());
        double deltaX = model.getMaxX()-model.getMinX();
        trans.setToIdentity();
        if(deltaX<deltaY){
            pan(-model.getMinX(),-model.getMaxY());
            zoom((getHeight()-getWidth()/(model.getMaxX()-model.getMinX()))*-1, new Point2D(0,0));
            pan(-(model.getMinY()-(model.getMaxX())), 0);
        }
        else {
            pan(-model.getMinX(), -model.getMaxY());
            zoom(((getWidth() / (model.getMinX() - model.getMaxX()))*-1), new Point2D(0, 0));
            pan(0,-(model.getMaxX()-(-model.getMinY()/2)));
        }
    }

}
