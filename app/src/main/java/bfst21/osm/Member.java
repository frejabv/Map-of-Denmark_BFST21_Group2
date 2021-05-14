package bfst21.osm;

import bfst21.Rtree.Rectangle;
import javafx.scene.canvas.GraphicsContext;

import java.io.Serializable;

public abstract class Member implements Serializable, Drawable {
    long id;
    Tag tag;

    public Member(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }

    @Override
    public void draw(GraphicsContext gc, RenderingStyle renderingStyle) {}

    @Override
    public Rectangle getRect() {
        return null;
    }
}
