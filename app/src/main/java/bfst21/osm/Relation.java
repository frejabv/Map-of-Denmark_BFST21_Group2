package bfst21.osm;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.shape.FillRule.EVEN_ODD;

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

            /*gc.setFill(Color.RED);
            gc.setFillRule(EVEN_ODD);
            gc.beginPath();
            gc.moveTo(120,130);
            gc.lineTo(140,180);
            gc.lineTo(170,180);
            gc.lineTo(170,130);
            gc.lineTo(120,130);

            gc.moveTo(100,100);
            gc.lineTo(120,150);
            gc.lineTo(90,200);
            gc.lineTo(200,200);
            gc.lineTo(180,175);
            gc.lineTo(200,100);
            gc.lineTo(100,100);

            gc.moveTo(10,10);
            gc.lineTo(10,250);
            gc.lineTo(250,250);
            gc.lineTo(250,10);
            gc.lineTo(10,10);
            gc.fill();*/

            //Test for VESTBALLEGÅRD
            /*gc.setFill(Color.PURPLE);
            gc.setFillRule(FillRule.EVEN_ODD);

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

            //outer of vestballegård
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

            gc.fill();*/

            if(tags.contains(Tag.BUILDING)) {
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
            }
        }
        System.out.println();
    }

    public void drawBuilding(GraphicsContext gc) {
        boolean innerDrawn = false;
        gc.setFillRule(FillRule.EVEN_ODD);
        gc.beginPath();
        if(id == 2186043) System.out.println("VESTBALLEGÅRD !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        for(Way way : ways) {
            // I want to check if the way has role inner
            String value = way.getRoleMap().get(id);
            //System.out.println(value + " role value read");
            if(value.equals("inner")) {
                way.specialDraw(gc);
                innerDrawn = true;
                System.out.println("inner drawn");
            }
        }
        if(innerDrawn) {
            for(Way way :ways) {
                String value = way.getRoleMap().get(id);
                //System.out.println(value + " role value read in second loop");
                if(value.equals("outer")) {
                    way.specialDraw(gc);
                    System.out.println(value + " drawn");
                }
            }
        }
        gc.fill();
    }

    /*public void showTags() {
        System.out.println("Result from show tags: " + tags);
    }*/

}
