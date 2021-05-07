package bfst21.osm;

import java.io.Serializable;
import java.util.HashMap;

public class Member implements Serializable {
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
}
