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
        System.out.println("Tag size when running: " + tags.size());
        //System.out.println("Tags in relation: " + tags);
        if(!tags.isEmpty()) {
            gc.setStroke(style.getColorByTag(tags.get(0)));
            gc.setFill(style.getColorByTag(tags.get(0)));

            //i want to check if what we are about to draw needs to be drawn with
            //empty space (building) or else which style it needs to be drawn as

            //outer of vestballegård
            gc.beginPath();
            var firstNode = new Node(10.5212460f, 55.9666861f, 1755108083);
            gc.moveTo(firstNode.getX(), firstNode.getY());
            ArrayList<Node> nodes = new ArrayList<>();
            nodes.add(firstNode);
            nodes.add(new Node(10.5218011f,55.9666945f, 2));
            nodes.add(new Node(10.5218114f,55.9664824f,3));
            nodes.add(new Node(10.5214534f,55.9664770f, 4));
            nodes.add(new Node(10.5214464f,55.9666215f, 5));
            nodes.add(new Node(10.5212493f,55.9666185f,6));
            nodes.add(new Node(10.5212460f,55.9666861f,7));
            //Node(lon,lat,id)

            for (var node : nodes) {
                gc.lineTo(node.getX(), node.getY());
            }
            gc.stroke();

            //inner of vestballegård
            gc.beginPath();
            var firstNode1 = new Node(10.5215410f, 55.9666264f, 1755108083);
            gc.moveTo(firstNode1.getX(), firstNode1.getY());
            ArrayList<Node> nodes1 = new ArrayList<>();
            nodes1.add(firstNode1);
            nodes1.add(new Node(10.5216942f,55.9666283f, 2));
            nodes1.add(new Node(10.5216978f,55.9665398f,3));
            nodes1.add(new Node(10.5215446f,55.9665378f, 4));
            nodes1.add(firstNode1);
            //Node(lon,lat,id)

            for (var node : nodes1) {
                gc.lineTo(node.getX(), node.getY());
            }
            gc.stroke();

            /*if(tags.contains(Tag.BUILDING)) {
                drawBuilding(gc);
                System.out.println("relation building drawn");
            } else {
                for(Way way : ways) {
                    var drawStyle = style.getDrawStyleByTag(tags.get(0));
                    way.draw(gc);
                    if(drawStyle.equals(DrawStyle.FILL)) {
                        gc.fill();
                    }
                }
            }*/
        }
        System.out.println();
    }

    public void drawBuilding(GraphicsContext gc) {
        boolean innerDrawn = false;
        gc.setFillRule(FillRule.EVEN_ODD);
        for(Way way : ways) {
            // I want to check if the way has role inner
            for(String value : way.getRoleMap().values()) {
                if(value.equals("inner")) {
                    way.draw(gc);
                    innerDrawn = true;
                    System.out.println("inner drawn");
                } else if(innerDrawn) {
                    way.draw(gc);
                    System.out.println(value + " drawn");
                }
            }
            //gc.fill();
        }
        gc.fill();
    }

    public void showTags() {
        System.out.println("Result from show tags: " + tags);
    }

}
