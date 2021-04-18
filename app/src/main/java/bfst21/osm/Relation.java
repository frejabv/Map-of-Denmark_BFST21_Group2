package bfst21.osm;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.FillRule;

import java.util.ArrayList;
import java.util.List;

public class Relation extends Member {
    ArrayList<Member> members = new ArrayList<>();
    ArrayList<Way> ways = new ArrayList<>();

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

            if(tags.contains(Tag.BUILDING) || tags.contains(Tag.MEADOW) || tags.contains(Tag.WATER)) {
                drawMultiPolygon(gc);
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

    public void drawMultiPolygon(GraphicsContext gc) {
        boolean innerDrawn = false;
        gc.setFillRule(FillRule.EVEN_ODD);
        gc.beginPath();
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

}
