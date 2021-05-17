package bfst21.Rtree;

import bfst21.osm.Drawable;

import java.util.List;

public class RtreeLeaf extends RtreeNode {
    private List<Drawable> drawables;

    public RtreeLeaf(List<Drawable> drawables) {
        super(drawables);

        this.drawables = drawables;
    }

    public List<Drawable> getDrawables() {
        return drawables;
    }
}
