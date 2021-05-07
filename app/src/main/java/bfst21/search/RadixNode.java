package bfst21.search;

import java.io.Serializable;
import java.util.ArrayList;

public class RadixNode implements Serializable {
    private ArrayList<RadixNode> children;
    boolean isPlace;
    boolean secondary;
    String value;
    String fullName;
    long id;

    public RadixNode(String value) {
        children = new ArrayList<>();
        isPlace = false;
        this.value = value;
    }

    public RadixNode(String value, String fullName, long id, boolean secondary) {
        this(value);
        this.fullName = fullName;
        this.id = id;
        isPlace = true;
        this.secondary = secondary;
    }

    public void addChild(RadixNode child) {
        children.add(child);
    }

    public ArrayList<RadixNode> getChildren() {
        return children;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean isPlace() {
        return isPlace;
    }

    public long getId() {
        return id;
    }
}
