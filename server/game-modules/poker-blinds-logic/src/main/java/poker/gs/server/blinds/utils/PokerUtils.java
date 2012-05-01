package poker.gs.server.blinds.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;

public class PokerUtils {
    /**
     * Gets the next element after the given element in a sorted map.
     * <p/>
     * If the given element is the last element, the first element in the map
     * will be returned.
     *
     * @param <K>       The type of the key.
     * @param <V>       The type of the value.
     * @param fromKey   the key to start looking from
     * @param sortedMap a sorted map to find an element in
     * @return the next element after the given element in a sorted map.
     */
    public static <K, V> V getElementAfter(K fromKey, SortedMap<K, V> sortedMap) {
        SortedMap<K, V> tailMap = sortedMap.tailMap(fromKey);
        V result;
        V fromValue = sortedMap.get(fromKey);

        if (tailMap.isEmpty()) {
            result = sortedMap.get(sortedMap.firstKey());
        } else if (tailMap.size() == 1 && tailMap.firstKey().equals(fromKey)) {
            result = sortedMap.get(sortedMap.firstKey());
        } else {
            Iterator<V> iterator = tailMap.values().iterator();

            result = iterator.next();
            if (result.equals(fromValue)) {
                if (iterator.hasNext()) {
                    result = iterator.next();
                }
            }
        }
        return result;
    }

    public static <K, V> List<V> unwrapList(SortedMap<K, V> sortedMap, K fromKey) {
        List<V> list = new ArrayList<V>();

        list.addAll(sortedMap.tailMap(fromKey).values());
        list.addAll(sortedMap.headMap(fromKey).values());

        return list;
    }

    /**
     * Checks if the given number is between from and to.
     * <p/>
     * If to is smaller than from, all values greater than from are considered
     * being between the two.
     *
     * @param number
     * @param from
     * @param to
     * @return
     */
    public static boolean isBetween(int number, int from, int to) {
        if (from > to) {
            return number > from || number < to;
        } else {
            return number > from && number < to;
        }
    }

}
