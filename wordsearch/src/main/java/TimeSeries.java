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

    public TimeSeries(String name, Map<K, V> map_) {
        this(name);
        map_.putAll(map_);
    }

    private String name_;
    private SortedMap<K, V> map_;
}
