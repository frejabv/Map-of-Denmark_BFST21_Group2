package bfst21.osm;

import bfst21.Rtree.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.FillRule;

import java.util.ArrayList;
import java.util.List;

public class Relation extends Member {
    ArrayList<Member> members = new ArrayList<>();
    ArrayList<Way> ways = new ArrayList<>();
    Rectangle rect;

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

    public void draw(GraphicsContext gc, RenderingStyle style) {
        if(!tags.isEmpty()) {
            gc.setStroke(style.getColorByTag(tags.get(0)));
            gc.setFill(style.getColorByTag(tags.get(0)));

            if(tags.contains(Tag.BUILDING)) {
                drawBuilding(gc);
            } else {
                for(Way way : ways) {
                    var drawStyle = style.getDrawStyleByTag(tags.get(0));
                    way.draw(gc);
                    if(drawStyle.equals(DrawStyle.FILL)) {
                        gc.fill();
                    }
                }
            }
        }
    }

    public void drawBuilding(GraphicsContext gc) {
        boolean innerDrawn = false;
        gc.setFillRule(FillRule.EVEN_ODD);
        gc.beginPath();
        //System.out.println("ID of relation: " + id);
        for(Way way : ways) {
            String value = way.getRoleMap().get(id);
            if(value.equals("inner")) {
                way.drawRelationPart(gc);
                innerDrawn = true;
            }
        }
        if(innerDrawn) {
            for(Way way : ways) {
                String value = way.getRoleMap().get(id);
                if(value.equals("outer")) {
                    way.drawRelationPart(gc);
                }
            }
        }
        gc.fill();
    }

    public void createRectangle(){
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        for (Way w: ways) {
            //check min values
            if (w.getRect().getMinX() < minX)
                minX = w.getRect().getMinX();
            else if (w.getRect().getMinX() < maxX)
                maxX = w.getRect().getMinX();
            if (w.getRect().getMinY() < minY)
                minY = w.getRect().getMinY();
            else if (w.getRect().getMinY() < maxY)
                maxY = w.getRect().getMinY();
            //check max values
            if (w.getRect().getMaxX() < minX)
                minX = w.getRect().getMaxX();
            if (w.getRect().getMaxX() < maxX)
                maxX = w.getRect().getMaxX();
            if (w.getRect().getMaxY() < minY)
                minY = w.getRect().getMaxY();
            if (w.getRect().getMaxY() < maxY)
                maxY = w.getRect().getMaxY();
        }

        rect = new Rectangle(minX, minY, maxX, maxY);
    }

    public Rectangle getRect(){
        return rect;
    }
}
