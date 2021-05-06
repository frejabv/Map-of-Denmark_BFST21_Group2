package bfst21.osm;

import bfst21.Rtree.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.FillRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Relation extends Member implements Drawable{
    ArrayList<Member> members = new ArrayList<>();
    ArrayList<Way> ways = new ArrayList<>();
    Rectangle rect;
    RenderingStyle renderingStyle = new RenderingStyle();

    public Relation(long id) {
        super(id);
    }

    public void  addMember(Member member) {
        members.add(member);
    }

    public void addWay(Way way) {
        ways.add(way);
    }

    public List<Member> getMembers() {
        return members;
    }

    public void draw(GraphicsContext gc) {
        if(!tags.isEmpty()) {
            gc.setStroke(renderingStyle.getColorByTag(tags.get(0)));
            gc.setFill(renderingStyle.getColorByTag(tags.get(0)));

            boolean innerDrawn = false;
            ArrayList<Way> outerLines = new ArrayList<>();

            gc.setFillRule(FillRule.EVEN_ODD);
            gc.beginPath();

            for(Way way : ways) {
                String value = way.getRoleMap().get(id);
                if(value.equals("inner")) {
                    way.drawRelationPart(gc);
                    innerDrawn = true;
                }
                if(value.equals("outer")) {
                    outerLines.add(way);
                }
            }

            ArrayList<Way> mergedList;
            if(outerLines.size() > 1) {
                mergedList = mergeOuter(outerLines);
            } else {
                mergedList = outerLines;
            }

            if(innerDrawn) {
                for(Way way : mergedList) {
                    way.drawRelationPart(gc);
                }
                gc.fill();
            } else {
                //draw relation normally
                gc.setFillRule(FillRule.NON_ZERO);
                for(Way way : ways) {
                    var drawStyle = renderingStyle.getDrawStyleByTag(tags.get(0));
                    way.draw(gc);
                    if(drawStyle.equals(DrawStyle.FILL)) {
                        gc.fill();
                    }
                }
            }
        }
    }

    public ArrayList<Way> mergeOuter(ArrayList<Way> outerLines) {
        HashMap<Node, Way> pieces = new HashMap<>();
        for (var line : outerLines) {
            var before = pieces.remove(line.first());
            var after = pieces.remove(line.last());
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

    public void createRectangle(){
        float minX = 1800, maxX = -1800, minY = 1800, maxY = -1800;

        for (Way w: ways) {
            //check min values
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

    public Rectangle getRect(){
        return rect;
    }
}
