package bfst21.osm;

import javafx.scene.canvas.GraphicsContext;

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
        //System.out.println("Relation.draw() began running");
        System.out.println("Tag size when running: " + tags.size());
        //System.out.println("Tags in relation: " + tags);
        //ArrayList<Tag> tag2 = getTags();
        //System.out.println("Result of getTags() " + tag2);
        if(!tags.isEmpty()) {
            gc.setStroke(style.getColorByTag(tags.get(0)));
            gc.setFill(style.getColorByTag(tags.get(0)));

            //vil tjekke om den vi skal til at tegne har tag building,
            // i så fald kalder vi en anden tegne metode med even odd.

            if(tags.contains(Tag.BUILDING)) {
                drawBuilding(gc);
                System.out.println("relation building drawn");
            } else {
                for(Way way : ways) {
                    var drawStyle = style.getDrawStyleByTag(way.tags.get(0));

                    way.draw(gc);
                    //gc.fill();
                    System.out.println("relation drawn");
                }
            }
            /*boolean innerDrawn = false;
            for (Member member : members) {
                if(member instanceof Way) {
                    member.getRoleMap().forEach((key, value) -> {
                        if (value.equals("inner")) {
                            ((Way) member).draw(gc);
                            innerDrawn = true;
                        }
                    });
                    ((Way) member).draw(gc);

                    gc.fill();
                }
            }*/
        }
        //System.out.println("Relation.draw() finished");
        System.out.println();
    }

    public void drawBuilding(GraphicsContext gc) {
        for(Way way : ways) {
            // her vil jeg tjekke om dette member har rollen inner
            /*for(String value : member.getRoleMap().values()) {
                    if(value.equals("inner")) {
                        way.draw(gc);
                    }
            }*/
            way.draw(gc);
            gc.fill();
        }
    }

    public void showTags() {
        System.out.println("Result from show tags: " + tags);
    }

}
