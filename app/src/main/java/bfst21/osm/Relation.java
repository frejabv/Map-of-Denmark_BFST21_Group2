package bfst21.osm;

import java.util.ArrayList;
import java.util.List;

public class Relation extends Member {
    ArrayList<Member> members = new ArrayList<>();

    public Relation(long id) {
        super(id);
    }

    public void  addMember(Member member) {
        members.add(member);
    }

    public List<Member> getMembers() {
        return members;
    }
}
