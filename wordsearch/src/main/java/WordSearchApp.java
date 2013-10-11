import java.util.Set;

public class WordSearchApp {

    public static final int THREAD_COUNT = 4;


    public static void main(String[] args) {
        TimeSeriesFileLoader fileLoader = new TimeSeriesFileLoader(args[1]);
        Set<String> allTimeSeries = fileLoader.getAllSeriesNames();
        MemoryCache<TimeSeries<Integer, Double>> cache = new MemoryCache<TimeSeries<Integer, Double>>(fileLoader);

    }


}
