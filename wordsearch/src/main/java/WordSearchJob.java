import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;

import java.util.*;


public class WordSearchJob implements Runnable {

    @Override
    public void run() {
        for (int iteration = 0 ; iteration < ITERATIONS_ ; ++iteration) {
            Range<Integer> timeRange = getTimeRange();
            String[] wordBag = getWordBag();
            List<DateTimeSeries<Integer>> wordSeries = new ArrayList<DateTimeSeries<Integer>>(wordBag.length);
            for (int i = 0 ; i < wordBag.length ; ++i) {
                wordSeries.add(getTimeSeries(wordBag[i]));
            }

            String seriesName = StringUtils.join(wordBag, "|");
            CountTimeSeries fullSeries = combineTimeSeries(seriesName, wordSeries, timeRange);

            String equityName = getEquityName();
            DateTimeSeries<Double> priceSeries = context_.getPricesCache().get(equityName);

            double pValue = GrangerCausality.granger(Utils.asDoubles(fullSeries.toList(timeRange)), priceSeries.toList(timeRange), LAG_WINDOWS_);

            if (pValue < P_VALUE_THRESHOLD_) {
                context_.getCollector().collect(new WordMatch(Arrays.asList(wordBag), equityName, timeRange, pValue));
            }
        }
    }


    public WordSearchJob(WordSearchContext context) {
        context_ = context;
        totalWords_ = context_.getAllWords().length;
    }


    private WordSearchContext context_;
    private final int totalWords_;
    private final Random random_ = new Random();

    private final static Logger logger_ = Logger.getLogger(WordSearchJob.class);


    // TODO: Make these configurable
    private final static int START_DATE_ = Utils.intFromDate(new LocalDate(1993, 1, 1).toDate());
    private final static int END_DATE_ = Utils.intFromDate(new LocalDate(2013, 1, 1).toDate());
    private final static double P_VALUE_THRESHOLD_ = 0.05;
    private final static String EQUITY_NAME_ = "oil";

    // TODO: also figure out reasonable values for these
    private final static int MIN_RANGE_ = 100;
    private final static int MAX_RANGE_ = 365 * 5;
    private final static List<Integer> LAG_WINDOWS_ = Arrays.asList(1, 2, 3, 5, 7, 10);
    private final static int BAG_SIZE_ = 5;

    // TODO: Set this much higher for production runs
    private final static int ITERATIONS_ = 10;


    private int randomDate() {
        return random_.nextInt(END_DATE_ - START_DATE_ + 1) + START_DATE_;
    }


    private Range<Integer> getTimeRange() {
        Range<Integer> range = new Range<Integer>();
        int rangeSize;
        do {
            range.start = randomDate();
            range.end = randomDate();
            rangeSize = Math.abs(range.end - range.start);
        } while (rangeSize >= MIN_RANGE_ && rangeSize <= MAX_RANGE_);

        if (range.start > range.end) {
            range.start ^= range.end;
            range.end ^= range.start;
            range.start ^= range.end;
        }

        return range;
    }


    private String[] getWordBag() {
        Set<Integer> indices = new HashSet<Integer>();
        while (indices.size() < BAG_SIZE_) {
            indices.add(random_.nextInt(totalWords_));
        }

        String[] bag = new String[BAG_SIZE_];
        int i = 0;
        Iterator<Integer> it = indices.iterator();
        while (it.hasNext()) {
            bag[i] = context_.getAllWords()[it.next()];
            ++i;
        }

        return bag;
    }


    private DateTimeSeries<Integer> getTimeSeries(String word) {
        DateTimeSeries<Integer> wordSeries = context_.getWordsCache().get(word);
        if (wordSeries == null) {
            logger_.error(String.format("Unable to retrieve time series for word %s", word));
        }

        return wordSeries;
    }


    private CountTimeSeries combineTimeSeries(String name, Iterable<DateTimeSeries<Integer>> wordSeries, Range<Integer> range) {
        CountTimeSeries timeSeries = new CountTimeSeries(name);
        for (DateTimeSeries<Integer> series : wordSeries) {
            for (Map.Entry<Integer, Integer> entry : series.getValues().subMap(range.start, range.end + 1).entrySet()) {
                timeSeries.addCount(entry.getKey(), entry.getValue());
            }
        }

        return timeSeries;
    }


    private String getEquityName() {
        // TODO: add more equities here if desired
        return EQUITY_NAME_;
    }
}
