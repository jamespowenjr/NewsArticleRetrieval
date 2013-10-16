package com.bah.finance.wordsearch;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import ru.algorithmist.jquant.math.GrangerTest;

import java.util.*;


public class WordSearchJob implements Runnable {

    @Override
    public void run() {
        for (int iteration = 0 ; iteration < ITERATIONS_ ; ++iteration) {
            //try {
                oneIteration_();
            //} catch (Exception e) {
              //  logger_.error(e.getMessage());
            //}
        }
    }


    public WordSearchJob(WordSearchContext context) {
        context_ = context;
        totalWords_ = context_.getAllWords().length;
        startDate_ = 0;
        endDate_ = context_.getDateMap().size() - 1;
    }


    private WordSearchContext context_;
    private final int totalWords_;
    private final Random random_ = new Random();
    private int startDate_;
    private int endDate_;

    private final static Logger logger_ = Logger.getLogger(WordSearchJob.class);


    // TODO: Make these configurable
    private final static double P_VALUE_THRESHOLD_ = 0.0001;
    private final static String EQUITY_NAME_ = "PL.C";

    // TODO: also figure out reasonable values for these
    private final static int MIN_RANGE_ = 120; // ~6 months
    private final static int MAX_RANGE_ = 2500; // ~10 years
    private final static int LAG_WINDOWS_ = 10;
    private final static int MIN_BAG_SIZE_ = 3;
    private final static int MAX_BAG_SIZE_ = 10;

    // TODO: Set this much higher for production runs
    private final static int ITERATIONS_ = 1000;


    private void oneIteration_() {
        Range<Integer> timeRange = getTimeRange_();
        String[] wordBag = getWordBag_();
        Collection<DateTimeSeries<Integer>> wordSeries = new ArrayList<DateTimeSeries<Integer>>(wordBag.length);
        for (String word : wordBag) {
            wordSeries.add(getTimeSeries_(word));
        }

        String seriesName = StringUtils.join(wordBag, "|");
        DateTimeSeries<Integer> fullSeries = combineTimeSeries_(seriesName, wordSeries, timeRange);

        String equityName = getEquityName_();
        DateTimeSeries<Double> priceSeries = context_.getPricesCache().get(equityName);

        System.out.println(String.format("%d", timeRange.end - timeRange.start));
        double pValue = GrangerTest.granger(
                Utils.asArray(priceSeries.toList(timeRange)),
                Utils.asDoubles(fullSeries.toList(timeRange)),
                LAG_WINDOWS_);

        if (pValue < P_VALUE_THRESHOLD_) {
            context_.getCollector().collect(new WordMatch(Arrays.asList(wordBag), equityName, timeRange, pValue));
        }
    }


    private int randomDate_() {
        return random_.nextInt(endDate_ - startDate_ + 1) + startDate_;
    }


    private Range<Integer> getTimeRange_() {
        Range<Integer> range = new Range<Integer>(0, 0);
        int rangeSize;
        do {
            range.start = randomDate_();
            range.end = randomDate_();
            rangeSize = Math.abs(range.end - range.start);
        } while (rangeSize < MIN_RANGE_ || rangeSize > MAX_RANGE_);

        if (range.start > range.end) {
            range.start ^= range.end;
            range.end ^= range.start;
            range.start ^= range.end;
        }

        return range;
    }


    private String[] getWordBag_() {
        Set<Integer> indices = new HashSet<Integer>();
        int bagSize = random_.nextInt(MAX_BAG_SIZE_ - MIN_BAG_SIZE_ + 1) + MIN_BAG_SIZE_;
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


    private DateTimeSeries<Integer> getTimeSeries_(String word) {
        DateTimeSeries<Integer> wordSeries = context_.getWordsCache().get(word);
        if (wordSeries == null) {
            logger_.error(String.format("Unable to retrieve time series for word %s", word));
        }

        return wordSeries;
    }


    private DateTimeSeries<Integer> combineTimeSeries_(String name, Iterable<DateTimeSeries<Integer>> wordSeries, Range<Integer> range) {
        CountTimeSeries timeSeries = new CountTimeSeries(name);
        for (DateTimeSeries<Integer> series : wordSeries) {
            for (Map.Entry<Integer, Integer> entry : series.getValues().subMap(range.start, range.end + 1).entrySet()) {
                timeSeries.addCount(entry.getKey(), entry.getValue());
            }
        }

        return timeSeries;
    }


    private String getEquityName_() {
        // TODO: add more equities here if desired
        return EQUITY_NAME_;
    }
}
