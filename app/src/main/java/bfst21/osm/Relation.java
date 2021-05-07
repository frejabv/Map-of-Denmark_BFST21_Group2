package bfst21.osm;

import bfst21.Rtree.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.FillRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Relation extends Member implements Drawable {
    ArrayList<Way> ways = new ArrayList<>();
    Rectangle rect;

    public Relation(long id) {
        super(id);
    }

    public void addWay(Way way) {
        ways.add(way);
    }

    public void draw(GraphicsContext gc, RenderingStyle style) {
        if (tag != null) {
            gc.setStroke(style.getColorByTag(tag));
            gc.setFill(style.getColorByTag(tag));

            boolean innerDrawn = false;
            ArrayList<Way> outerLines = new ArrayList<>();

            gc.setFillRule(FillRule.EVEN_ODD);
            gc.beginPath();

            for (Way way : ways) {
                String value = way.getRoleMap().get(id);
                if (value.equals("inner")) {
                    way.drawRelationPart(gc);
                    innerDrawn = true;
                }
                if (value.equals("outer")) {
                    outerLines.add(way);
                }
            }

            ArrayList<Way> mergedList;
            if (outerLines.size() > 1) {
                mergedList = mergeOuter(outerLines);
            } else {
                mergedList = outerLines;
            }

            if (innerDrawn || outerLines.size() != 0) {
                for (Way way : mergedList) {
                    way.drawRelationPart(gc);
                }
                gc.fill();
            } else {
                //draw relation normally
                gc.setFillRule(FillRule.NON_ZERO);
                for (Way way : ways) {
                    var drawStyle = style.getDrawStyleByTag(tag);
                    way.draw(gc, style);
                    if (drawStyle.equals(DrawStyle.FILL)) {
                        gc.fill();
                    }
                }
            }
        }
    }

    public ArrayList<Way> mergeOuter(ArrayList<Way> outerLines) {
        HashMap<Node, Way> pieces = new HashMap<>();
        for (Way line : outerLines) {
            Way before = pieces.remove(line.first());
            Way after = pieces.remove(line.last());
            if (after != null && line.last() != after.first()) {
                Collections.reverse(after.getNodes());
            }
            if (before == after)
                after = null;
            var merged = Way.merge(before, line, after);
            pieces.put(merged.first(), merged);
            pieces.put(merged.last(), merged);
        }
        ArrayList<Way> mergedList = new ArrayList<>();
        pieces.forEach((node, way) -> {
            if (way.last() == node) {
                mergedList.add(way);
            }
        });
        return mergedList;
    }

    public void createRectangle() {
        float minX = 1800, maxX = -1800, minY = 1800, maxY = -1800;

        for (Way w : ways) {
            //check min values
            if (w.getRect() == null) {
                continue;
            }
            if (w.getRect().getMinX() < minX) {
                minX = w.getRect().getMinX();
            }
            if (w.getRect().getMinX() > maxX) {
                maxX = w.getRect().getMinX();
            }
            if (w.getRect().getMinY() < minY) {
                minY = w.getRect().getMinY();
            }
            if (w.getRect().getMinY() > maxY) {
                maxY = w.getRect().getMinY();
            }

            //check max values
            if (w.getRect().getMaxX() < minX) {
                minX = w.getRect().getMaxX();
            }
            if (w.getRect().getMaxX() > maxX) {
                maxX = w.getRect().getMaxX();
            }
            if (w.getRect().getMaxY() < minY) {
                minY = w.getRect().getMaxY();
            }
            if (w.getRect().getMaxY() > maxY) {
                maxY = w.getRect().getMaxY();
            }
        }

        rect = new Rectangle(minX, minY, maxX, maxY);
    }

    public Rectangle getRect() {
        return rect;
    }
}
