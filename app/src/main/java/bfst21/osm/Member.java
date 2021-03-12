package bfst21.osm;

import java.util.ArrayList;

public class Member {
    long id;
    ArrayList<Tag> tags;

    public Member(long id){
        this.id = id;
        tags = new ArrayList<>();
    }

    public long getId(){ return id; }

    public void addTag(Tag tag){
        tags.add(tag);
    }
}
