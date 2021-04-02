package bfst21.osm;

import java.util.ArrayList;
import java.util.HashMap;

public class Member {
    long id;
    HashMap<Long, String> roleMap;
    ArrayList<Tag> tags;

    public Member(long id){
        this.id = id;
        tags = new ArrayList<>();
    }

    public long getId() { return id; }

    public HashMap<Long, String> getRoleMap() { return roleMap; }

    public void addRole(long id, String role) {
        if (roleMap ==  null)
            roleMap = new HashMap<>();
        roleMap.put(id,role);
    }

    public void setTags(ArrayList<Tag> tags){
        this.tags = tags;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }
}
