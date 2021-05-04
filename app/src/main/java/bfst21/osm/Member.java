package bfst21.osm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Member implements Serializable {
    long id;
    HashMap<Long, String> roleMap;
    Tag tag;

    public Member(long id){
        this.id = id;
    }

    public long getId() { return id; }

    public HashMap<Long, String> getRoleMap() { return roleMap; }

    public void addRole(long id, String role) {
        if (roleMap ==  null)
            roleMap = new HashMap<>();
        roleMap.put(id,role);
    }

    public void setTag(Tag tag){
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }
}
