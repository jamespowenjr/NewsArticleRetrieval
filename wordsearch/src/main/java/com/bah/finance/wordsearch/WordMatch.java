package com.bah.finance.wordsearch;

import com.bah.finance.wordsearch.timeseries.DateTimeSeries;
import com.bah.finance.wordsearch.util.Range;

import java.util.List;

public class WordMatch {

    public List<DateTimeSeries<Integer>> getSeries() {
        return words_;
    }

    public String getEquity() {
        return equity_;
    }

    public Range<Integer> getTimeframe() {
        return timeframe_;
    }

    public double getPValue() {
        return pValue_;
    }

    public WordMatch(List<DateTimeSeries<Integer>> words, String equity, Range<Integer> timeframe, double pValue) {
        words_ = words;
        equity_ = equity;
        timeframe_ = timeframe;
        pValue_ = pValue;
    }

    private final List<DateTimeSeries<Integer>> words_;
    private final String equity_;
    private final Range<Integer> timeframe_;
    private final double pValue_;
}
