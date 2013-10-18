package com.bah.finance.wordsearch;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import ru.algorithmist.jquant.math.GrangerTest;

import java.util.*;


public class WordSearchJob implements Runnable, Configurable {

    @Override
    public void configure(Properties props) throws PropertyException {
        iterations_ = Utils.getConfigInt(props, ITERATIONS_KEY_, DEFAULT_ITERATIONS_);
        threshold_ = Utils.getConfigDouble(props, THRESHOLD_KEY_, DEFAULT_THRESHOLD_);
        lagWindows_ = Utils.getConfigInt(props, LAG_WINDOWS_KEY_, DEFAULT_LAG_WINDOWS_);
        minBagSize_ = Utils.getConfigInt(props, MIN_BAG_SIZE_KEY_, DEFAULT_MIN_BAG_SIZE_);
        maxBagSize_ = Utils.getConfigInt(props, MAX_BAG_SIZE_KEY_, DEFAULT_MAX_BAG_SIZE_);
        minWordTotal_ = Utils.getConfigInt(props, MIN_WORD_TOTAL_KEY_, DEFAULT_MIN_WORD_TOTAL_);

        dateRangeGenerator_.configure(props);
    }

    @Override
    public void run() {
        for (int iteration = 0 ; iteration < iterations_ ; ++iteration) {
        //while (true) {
            try {
                oneIteration_();
                ++completedIterations_;
            } catch (Exception e) {
                logger_.error(e.getMessage());
            }
        }
    }


    public WordSearchJob(WordSearchContext context) {
        context_ = context;
        totalWords_ = context_.getAllWords().length;
        try {
            dateRangeGenerator_ = context.getDateRangeGenerator().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date range generator class");
        }
    }
    
    
    public int getCompletedIterations() {
        return completedIterations_;
    }


    private WordSearchContext context_;
    private final int totalWords_;
    private DateRangeGenerator dateRangeGenerator_;
    private final Random random_ = new Random();

    private final static Logger logger_ = Logger.getLogger(WordSearchJob.class);


    // TODO: in general we probably want to choose from all available price time series,
    // but for now just hardcode these
    private final static String[] EQUITY_NAMES_ = new String[] { "CL.C", "CL", };

    private int iterations_ = DEFAULT_ITERATIONS_;
    private final static String ITERATIONS_KEY_ = "test.iterations";
    private final static int DEFAULT_ITERATIONS_ = 10000;
    private int completedIterations_ = 0;

    private double threshold_ = DEFAULT_THRESHOLD_;
    private final static String THRESHOLD_KEY_ = "test.threshold";
    private final static double DEFAULT_THRESHOLD_ = 1e-5;

    private int lagWindows_ = DEFAULT_LAG_WINDOWS_;
    private final static String LAG_WINDOWS_KEY_ = "test.lag_windows";
    private final static int DEFAULT_LAG_WINDOWS_ = 10;

    private int minBagSize_ = DEFAULT_MIN_BAG_SIZE_;
    private final static String MIN_BAG_SIZE_KEY_ = "words.min_bag_size";
    private final static int DEFAULT_MIN_BAG_SIZE_ = 3;

    private int maxBagSize_ = DEFAULT_MAX_BAG_SIZE_;
    private final static String MAX_BAG_SIZE_KEY_ = "words.max_bag_size";
    private final static int DEFAULT_MAX_BAG_SIZE_ = 20;

    private int minWordTotal_ = DEFAULT_MIN_WORD_TOTAL_;
    private final static String MIN_WORD_TOTAL_KEY_ = "words.min_word_total";
    private final static int DEFAULT_MIN_WORD_TOTAL_ = 100;


    private void oneIteration_() throws Exception {
        Range<Integer> timeRange = dateRangeGenerator_.getDateRange();
        String[] wordBag = getWordBag_();
        List<DateTimeSeries<Integer>> wordSeries = new ArrayList<DateTimeSeries<Integer>>(wordBag.length);
        for (String word : wordBag) {
            wordSeries.add(getTimeSeries_(word));
        }

        String seriesName = StringUtils.join(wordBag, "|");
        DateTimeSeries<Integer> fullSeries = combineTimeSeries_(seriesName, wordSeries, timeRange);
        double[] wordArray = Utils.asDoubles(fullSeries.toList(timeRange));

        if (Utils.sum(wordArray) < minWordTotal_) {
            return;
        }

        String equityName = getEquityName_();
        DateTimeSeries<Double> priceSeries = context_.getPricesCache().get(equityName);
        double[] priceArray = Utils.asArray(priceSeries.toList(timeRange));

        double pValue = GrangerTest.granger(priceArray, wordArray, lagWindows_);

        if (pValue < threshold_) {
            context_.getCollector().collect(new WordMatch(wordSeries, equityName, timeRange, pValue));
        }
    }


    private String[] getWordBag_() {
        Set<Integer> indices = new HashSet<Integer>();
        int bagSize = random_.nextInt(maxBagSize_ - minBagSize_ + 1) + minBagSize_;
        while (indices.size() < bagSize) {
            indices.add(random_.nextInt(totalWords_));
        }

        String[] bag = new String[bagSize];
        int i = 0;
        for (Integer index : indices) {
            bag[i] = context_.getAllWords()[index];
            ++i;
        }

        return bag;
    }


    private DateTimeSeries<Integer> getTimeSeries_(String word) throws Exception {
        DateTimeSeries<Integer> wordSeries = context_.getWordsCache().get(word);
        if (wordSeries == null) {
            String error = String.format("Unable to retrieve time series for word %s", word);
            logger_.error(error);
            throw new Exception(error);
        }

        return wordSeries;
    }


    private DateTimeSeries<Integer> combineTimeSeries_(String name, Iterable<DateTimeSeries<Integer>> wordSeries, Range<Integer> range) {
        CountTimeSeries timeSeries = new CountTimeSeries(name);
        for (DateTimeSeries<Integer> series : wordSeries) {
            for (Map.Entry<Integer, Integer> entry : series.getValues().subMap(range.getStart(), range.getEnd() + 1).entrySet()) {
                timeSeries.addCount(entry.getKey(), entry.getValue());
            }
        }

        return timeSeries;
    }


    private String getEquityName_() {
        return EQUITY_NAMES_[random_.nextInt(EQUITY_NAMES_.length)];
    }

}
