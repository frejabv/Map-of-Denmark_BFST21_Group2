package bfst21.osm;

import java.util.ArrayList;
import java.util.List;

public class MemberIndex {
        private List<Member> members;
        private boolean isSorted;

        public MemberIndex() {
            this.members = new ArrayList<>();
            this.isSorted = true;
        }

        public void addMember(Member member) {
            members.add(member);
            isSorted = false;
        }

        public Member getMember(long id) {
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
            Member member = members.get((int) lo);

            if (member.getId() == id) {
                return member;
            } else {
                return null;
            }
        }

        public int size() {
            return members.size();
        }
    }

