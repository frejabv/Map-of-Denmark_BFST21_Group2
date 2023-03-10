package bfst21.osm;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MemberIndex<T extends Member> implements Iterable<T>, Serializable {
    private final List<T> members;
    private boolean isSorted;

    public MemberIndex() {
        this.members = new ArrayList<>();
        this.isSorted = true;
    }

    public void addMember(T member) {
        members.add(member);
        isSorted = false;
    }

    public T getMember(long id) {
        if (!isSorted) {
            members.sort((a, b) -> Long.compare(a.getId(), b.getId()));
            isSorted = true;
        }

        long lo = 0;
        long hi = members.size();
        while (lo + 1 < hi) {
            long mid = (lo + hi) / 2;
            if (members.get((int) mid).getId() <= id) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        T member = members.get((int) lo);

        if (member.getId() == id) {
            return member;
        } else {
            return null;
        }
    }

    public int size() {
        return members.size();
    }

    public List<Drawable> getDrawableMembers() {
        return new ArrayList<>(members);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return members.iterator();
    }
}

