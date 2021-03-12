package bfst21.osm;

import java.util.ArrayList;
import java.util.List;

public class RelationIndex {
    private List<Relation> relations;
    private boolean isSorted;

    public RelationIndex() {
        this.relations = new ArrayList<>();
        this.isSorted = true;
    }

    public void addRelation(Relation relation) {
        relations.add(relation);
        isSorted = false;
    }

    public Relation getRelation(long id) {
        if (!isSorted) {
            relations.sort((a, b) -> Long.compare(a.getId(), b.getId()));
            isSorted = true;
        }

        long lo = 0;
        long hi = relations.size();
        while (lo + 1 < hi) {
            long mid = (lo + hi) / 2;
            if (relations.get((int) mid).getId() <= id) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        Relation relation = relations.get((int) lo);

        if (relation.getId() == id) {
            return relation;
        } else {
            return null;
        }
    }

    public int size() {
        return relations.size();
    }
}
