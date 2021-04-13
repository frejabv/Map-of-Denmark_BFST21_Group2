package bfst21.Rtree;

public class RtreeNode {
    Rectangle rect;
    RtreeNode[] children;
    int height;

    public RtreeNode(Rectangle rect, int height){
        this.rect = rect;
        children = new RtreeNode[Rtree.maxChildren];
        this.height = height;
    }

    public Rectangle getRect() {
        return rect;
    }

    public RtreeNode[] getChildren(){
        return children;
    }

    public void split(){
        //TODO split rect into smaller children, set children, children = leaves if maxHeight == true
    }

    public void setChildren(){

    }
}
