import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MapUtil {
    public static <K, V extends Comparable<? super V>> List<Entry<K,V>>
    sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>(
                        map.entrySet());
        list.sort((o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

        return list;

    }
}