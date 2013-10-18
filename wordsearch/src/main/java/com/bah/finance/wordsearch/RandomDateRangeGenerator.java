package com.bah.finance.wordsearch;

import java.util.Properties;
import java.util.Random;

public class RandomDateRangeGenerator implements DateRangeGenerator {

    @Override
    public void configure(Properties props) throws PropertyException {
        startDate_ = Utils.getConfigInt(props, START_DATE_KEY_);
        endDate_ = Utils.getConfigInt(props, END_DATE_KEY_);
        minLength_ = Utils.getConfigInt(props, MIN_LENGTH_KEY_, DEFAULT_MIN_LENGTH_);
        maxLength_ = Utils.getConfigInt(props, MAX_LENGTH_KEY_, DEFAULT_MAX_LENGTH_);
    }

    @Override
    public Range<Integer> getDateRange() {
        int start, end;
        int rangeSize;
        do {
            start = randomDate_();
            end = randomDate_();
            rangeSize = Math.abs(end - start);
        } while (rangeSize < minLength_ || rangeSize > maxLength_);

        if (start > end) {
            return new Range<Integer>(end, start);
        } else {
            return new Range<Integer>(start, end);
        }
    }

    public RandomDateRangeGenerator() {
        random_ = new Random();
    }

    private Random random_;

    // Configuration settings
    private int startDate_;
    private final static String START_DATE_KEY_ = "dates.start_date";

    private int endDate_;
    private final static String END_DATE_KEY_ = "dates.end_date";

    private int minLength_;
    private final static String MIN_LENGTH_KEY_ = "dates.min_length";
    private final static int DEFAULT_MIN_LENGTH_ = 250;

    private int maxLength_;
    private final static String MAX_LENGTH_KEY_ = "dates.max_length";
    private final static int DEFAULT_MAX_LENGTH_ = 2500;


    private Integer randomDate_() {
        return random_.nextInt(endDate_ - startDate_ + 1) + startDate_;
    }
}
