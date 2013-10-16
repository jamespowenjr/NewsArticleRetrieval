package com.bah.finance.wordsearch;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import ru.algorithmist.jquant.math.GrangerTest;

import java.util.*;


public class WordSearchJob implements Runnable {

    @Override
    public void run() {
        for (int iteration = 0 ; iteration < ITERATIONS_ ; ++iteration) {
            Range<Integer> timeRange = getTimeRange();
            String[] wordBag = getWordBag();
            Collection<DateTimeSeries<Integer>> wordSeries = new ArrayList<DateTimeSeries<Integer>>(wordBag.length);
            for (String word : wordBag) {
                wordSeries.add(getTimeSeries(word));
            }

            String seriesName = StringUtils.join(wordBag, "|");
            CountTimeSeries fullSeries = combineTimeSeries(seriesName, wordSeries, timeRange);

            String equityName = getEquityName();
            DateTimeSeries<Double> priceSeries = context_.getPricesCache().get(equityName);

            double pValue = GrangerTest.granger(Utils.asDoubles(fullSeries.toList(timeRange)),
                    Utils.asArray(priceSeries.toList(timeRange)), LAG_WINDOWS_[LAG_WINDOWS_.length - 1]);

            if (pValue < P_VALUE_THRESHOLD_) {
                context_.getCollector().collect(new WordMatch(Arrays.asList(wordBag), equityName, timeRange, pValue));
            }
        }
    }


    public WordSearchJob(WordSearchContext context) {
        context_ = context;
        totalWords_ = context_.getAllWords().length;
        startDate_ = context_.getDateMap().getStartDate();
        endDate_ = context_.getDateMap().getEndDate();
    }


    private WordSearchContext context_;
    private final int totalWords_;
    private final Random random_ = new Random();
    private int startDate_;
    private int endDate_;

    private final static Logger logger_ = Logger.getLogger(WordSearchJob.class);


    // TODO: Make these configurable
    private final static double P_VALUE_THRESHOLD_ = 0.05;
    private final static String EQUITY_NAME_ = "oil";

    // TODO: also figure out reasonable values for these
    private final static int MIN_RANGE_ = 100;
    private final static int MAX_RANGE_ = 365 * 5;
    private final static int[] LAG_WINDOWS_ = new int[] {1, 2, 3, 5, 7, 10 };
    private final static int BAG_SIZE_ = 5;

    // TODO: Set this much higher for production runs
    private final static int ITERATIONS_ = 10;


    private int randomDate() {
        return random_.nextInt(endDate_ - startDate_ + 1) + startDate_;
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
        for (Integer index : indices) {
            bag[i] = context_.getAllWords()[index];
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
