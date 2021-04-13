package bfst21.Rtree;

import bfst21.osm.Drawable;

public class RtreeLeaf {
    Drawable[] drawables;
    Rectangle rect;

    public RtreeLeaf(Rectangle rect, Drawable[] drawables){
        this.rect = rect;
        this.drawables = drawables;
    }

    public Drawable[] getDrawables() {
        return drawables;
    }

    public Rectangle getRect() {
        return rect;
    }
}
