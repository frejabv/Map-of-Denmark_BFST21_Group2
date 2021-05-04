package bfst21.osm;

import bfst21.Rtree.Rectangle;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;
import java.util.HashMap;

//TODO remove Member class at some point
public class Member implements Serializable, Drawable {
    long id;
    HashMap<Long, String> roleMap;
    Tag tag;

    public Member(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public HashMap<Long, String> getRoleMap() {
        return roleMap;
    }

    public void addRole(long id, String role) {
        if (roleMap == null)
            roleMap = new HashMap<>();
        roleMap.put(id, role);
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }

    @Override
    public void draw(GraphicsContext gc) {

    }

    @Override
    public Rectangle getRect() {
        return null;
    }
}
