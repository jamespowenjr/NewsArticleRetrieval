import java.io.File;
import java.util.Set;

public class WordSearchApp {

    public static final int THREAD_COUNT = 4;


    public static void main(String[] args) {
        TimeSeriesFileLoader<Integer> wordFileLoader = new CountTimeSeriesFileLoader(
                new File(args[1], WORD_TIME_SERIES_DIRECTORY_).toString());
        TimeSeriesFileLoader<Double> priceFileLoader = new PriceTimeSeriesFileLoader(
                new File(args[1], PRICES_TIME_SERIES_DIRECTORY_).toString());

        TimeSeriesCache<Integer, Integer> wordsCache = new TimeSeriesCache<Integer, Integer>(wordFileLoader);
        TimeSeriesCache<Integer, Double> pricesCache = new TimeSeriesCache<Integer, Double>(priceFileLoader);

        Set<String> allWords = wordFileLoader.getAllSeriesNames();

        WordSearchContext context = new WordSearchContext();
        context.setAllWords(allWords.toArray(new String[allWords.size()]));
        context.setWordsCache(wordsCache);
        context.setPricesCache(pricesCache);

    }


    private static final String WORD_TIME_SERIES_DIRECTORY_ = "words";
    private static final String PRICES_TIME_SERIES_DIRECTORY_ = "prices";
}
