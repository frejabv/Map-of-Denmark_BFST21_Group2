package bfst21.search;

import java.util.ArrayList;

public class RadixNode {
    private ArrayList<RadixNode> children;
    boolean isPlace;
    String content;
    String fullName;
    long id;

    public RadixNode(String content) {
        children = new ArrayList<>();
        isPlace = false;
        this.content = content;
    }

    public RadixNode(String content, String fullName, long id) {
        this(content);
        this.fullName = fullName;
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

    public String getFullName() { return fullName; }

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
