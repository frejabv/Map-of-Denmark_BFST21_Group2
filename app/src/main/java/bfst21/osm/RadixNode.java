package bfst21.osm;

import java.util.ArrayList;

public class RadixNode {
    private ArrayList<RadixNode> children;
    boolean isPlace;
    String content;
    long id;

    public RadixNode(String content) {
        children = new ArrayList<>();
        isPlace = false;
        this.content = content;
    }

    public RadixNode(String content, long id) {
        this(content);
        this.id = id;
    }

    public void addChild(RadixNode child) {
        children.add(child);
    }

    public ArrayList<RadixNode> getChildren() {
        return children;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPlace() {
        return isPlace;
    }

    public void setIsPlace(boolean isPlace) {
        this.isPlace = isPlace;
    }

    public long getId() {
        return id;
    }
}
