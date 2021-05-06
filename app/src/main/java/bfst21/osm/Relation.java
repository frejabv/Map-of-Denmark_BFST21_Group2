package bfst21.osm;

import bfst21.Rtree.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.FillRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Relation extends Member implements Drawable {
    ArrayList<Member> members = new ArrayList<>();
    ArrayList<Way> ways = new ArrayList<>();
    Rectangle rect;
    RenderingStyle renderingStyle = new RenderingStyle();

    public Relation(long id) {
        super(id);
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public void addWay(Way way) {
        ways.add(way);
    }

    public List<Member> getMembers() {
        return members;
    }

    public void draw(GraphicsContext gc) {
        if (!tags.isEmpty()) {
            gc.setStroke(renderingStyle.getColorByTag(tags.get(0)));
            gc.setFill(renderingStyle.getColorByTag(tags.get(0)));

            boolean innerDrawn = false;
            ArrayList<Way> outerLines = new ArrayList<>();

            gc.setFillRule(FillRule.EVEN_ODD);
            gc.beginPath();

            if(id == 2325011 || id == 2191417) System.out.println("IT'S THE CHOSEN ONE AAAAAAAAAAAAAH !!!!!!!!!!!!!!!!!!!!!!!");

            for (Way way : ways) {
                String value = way.getRoleMap().get(id);
                if (value.equals("inner")) {
                    way.drawRelationPart(gc);
                    System.out.println("inner drawn");
                    innerDrawn = true;
                }
                if (value.equals("outer")) {
                    outerLines.add(way);
                }
            }

            System.out.println("Size of outers: " + outerLines.size());
            ArrayList<Way> mergedList;
            if (outerLines.size() > 1) {
                mergedList = mergeOuter(outerLines);
                System.out.println("Size of merged: " + mergedList.size());

            } else {
                mergedList = outerLines;
            }

            if (innerDrawn) {
                for (Way way : mergedList) {
                    System.out.println("outer drawn");
                    way.drawRelationPart(gc);
                }
                gc.fill();
            } else {
                //draw relation normally
                System.out.println("something normal");
                gc.setFillRule(FillRule.NON_ZERO);
                for (Way way : ways) {
                    var drawStyle = renderingStyle.getDrawStyleByTag(tags.get(0));
                    way.draw(gc);
                    if (drawStyle.equals(DrawStyle.FILL)) {
                        gc.fill();
                    }
                }
            }
            System.out.println();
        }
    }

    public ArrayList<Way> mergeOuter(ArrayList<Way> outerLines) {
        HashMap<Node, Way> pieces = new HashMap<>();
        for (Way line : outerLines) {
            System.out.println(line.getId());
            System.out.println("First and last nodes: " + line.first().getId() + " " + line.last().getId());
            Way before = pieces.remove(line.first());
            Way after = pieces.remove(line.last());
            if (before == after)
                after = null;
            var merged = Way.merge(before, line, after);
            var merged1 = Way.merge(before, merged);
            var merged2 = Way.merge(merged1, after);
            if(before != null) {
                System.out.println("First of before: " + before.first().getId());
            }
            if(after != null) {
                System.out.println( "Last of after: " + after.last().getId());
            }
            System.out.println("New first and last nodes: " + merged2.first().getId() + " " + merged2.last().getId());
            pieces.put(merged2.first(), merged2);
            pieces.put(merged2.last(), merged2);
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
