import java.io.File;
import java.util.Set;

public class WordSearchApp {

    public static final int THREAD_COUNT = 4;


    public static void main(String[] args) {
        TimeSeriesFileLoader wordFileLoader = new TimeSeriesFileLoader(
                new File(args[1], WORD_TIME_SERIES_DIRECTORY_).toString());
        TimeSeriesFileLoader priceFileLoader = new TimeSeriesFileLoader(
                new File(args[1], PRICES_TIME_SERIES_DIRECTORY_).toString());

        TimeSeriesCache wordsCache = new TimeSeriesCache(wordFileLoader);
        TimeSeriesCache pricesCache = new TimeSeriesCache(priceFileLoader);

        Set<String> allWords = wordFileLoader.getAllSeriesNames();

        WordSearchContext context = new WordSearchContext();
        context.setAllWords(allWords.toArray(new String[allWords.size()]));
        context.setWordsCache(wordsCache);
        context.setPricesCache(pricesCache);

    }


    private static final String WORD_TIME_SERIES_DIRECTORY_ = "words";
    private static final String PRICES_TIME_SERIES_DIRECTORY_ = "prices";
}
