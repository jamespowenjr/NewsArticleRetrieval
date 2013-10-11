public class TimeSeriesCache<K, V> extends MemoryCache<TimeSeries<K, V>> {

    public TimeSeriesCache(Loader<TimeSeries<K, V>, String> loader) {
        super(loader);
    }

}
