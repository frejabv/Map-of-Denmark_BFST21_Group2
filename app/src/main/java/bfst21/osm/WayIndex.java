package bfst21.osm;

import java.util.ArrayList;
import java.util.List;

public class WayIndex {
        private List<Way> ways;
        private boolean isSorted;

        public WayIndex() {
            this.ways = new ArrayList<>();
            this.isSorted = true;
        }

        public void addWay(Way way) {
            ways.add(way);
            isSorted = false;
        }

        public Way getWay(long id) {
            if (!isSorted) {
                ways.sort((a, b) -> Long.compare(a.getId(), b.getId()));
                isSorted = true;
            }

            long lo = 0;
            long hi = ways.size();
            while (lo + 1 < hi) {
                long mid = (lo + hi) / 2;
                if (ways.get((int) mid).getId() <= id) {
                    lo = mid;
                } else {
                    hi = mid;
                }
            }
            Way way = ways.get((int) lo);

            if (way.getId() == id) {
                return way;
            } else {
                return null;
            }
        }

        public int size() {
            return ways.size();
        }
}
