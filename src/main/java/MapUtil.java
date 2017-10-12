import java.util.*;
import java.util.Map.Entry;

public class MapUtil {
    public static <K, V extends Comparable<? super V>> List<Entry<K,V>>
    sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>(
                        map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        return list;

    }
}