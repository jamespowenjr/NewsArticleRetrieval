import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class TimeSeries<K, V> {

    public String getName() {
        return name_;
    }

    public SortedMap<K, V> getValues() {
        return map_;
    }

    public void addEntry(K time, V value) {
        map_.put(time, value);
    }

    public TimeSeries(String name) {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        name_ = name;
        map_ = new TreeMap<K, V>();
    }

    public <V2 extends V> TimeSeries(String name, Map<K, V2> map_) {
        this(name);
        for (Map.Entry<K, V2> entry : map_.entrySet()) {
            map_.put(entry.getKey(), entry.getValue());
        }
    }

    private String name_;
    private SortedMap<K, V> map_;
}
