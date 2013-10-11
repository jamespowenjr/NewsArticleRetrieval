public class TimeSeriesCache extends MemoryCache<TimeSeries<Integer, Double>> {

    public TimeSeriesCache(Loader<TimeSeries<Integer, Double>, String> loader) {
        super(loader);
    }

}
