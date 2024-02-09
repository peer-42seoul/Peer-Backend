package peer.backend.comparator;

import java.util.Comparator;
import java.util.Objects;

public class LongComparator implements Comparator<Long> {

    @Override
    public int compare(Long o1, Long o2) {
        return o1.compareTo(o2);
    }
}
