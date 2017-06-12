package strategy.util;

import java.util.Comparator;
import java.util.List;

public class BiggestRiseComparator implements Comparator<List<Integer>> {

    @Override
    public int compare(List<Integer> o1, List<Integer> o2) {
        if(o1.size() == 0) {
            if(o2.size() == 0) {
                return 0;
            }
            return 1;
        }
        if(o2.size() == 0) {
            return -1;
        }
        int difference1 = o1.get(0) - o1.get(o1.size() - 1);
        int difference2 = o2.get(0) - o2.get(o2.size() - 1);
        return Integer.compare(difference1, difference2);
    }

    public int reverseCompare(List<Integer> o1, List<Integer> o2) {
        return -compare(o1, o2);
    }
}
