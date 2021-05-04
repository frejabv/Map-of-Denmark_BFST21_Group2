package bfst21.osm;

import bfst21.Rtree.Rectangle;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

//TODO remove Member class at some point
public class Member implements Serializable, Drawable {
    long id;
    HashMap<Long, String> roleMap;
    ArrayList<Tag> tags;

    public Member(long id){
        this.id = id;
        tags = new ArrayList<>();
    }

    public long getId() { return id; }

    public HashMap<Long, String> getRoleMap() { return roleMap; }

    public void addRole(long id, String role) {
        if (roleMap ==  null)
            roleMap = new HashMap<>();
        roleMap.put(id,role);
    }

    public void setTags(ArrayList<Tag> tags){
        this.tags = tags;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    @Override
    public void draw(GraphicsContext gc) {

    }

    @Override
    public Rectangle getRect() {
        return null;
    }

    public Tag getTag() {
        return tags.get(0);
    }
}
