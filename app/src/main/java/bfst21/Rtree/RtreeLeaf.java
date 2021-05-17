package bfst21.Rtree;

import bfst21.osm.Drawable;

import java.util.List;

/**
 * R-tree leaves are the lowest nodes in the R-tree. they only contain a small list of Drawables.
 * These drawables are effectively the actual leaves of the tree, where the RtreeLeaf is the lowest
 * none-leaf node.
 */
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
