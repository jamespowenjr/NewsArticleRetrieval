package com.bah.finance.wordsearch;

public class CountTimeSeriesFileLoader extends TimeSeriesFileLoader<Integer> {

    public CountTimeSeriesFileLoader(String searchDirectory, TradingDateMap dateMap) {
        super(searchDirectory, dateMap);
    }

    @Override
    protected Integer parseValue_(String value) {
        return Integer.parseInt(value);
    }

    @Override
    protected boolean forceNextDate_() {
        return true;
    }

    @Override
    protected DateTimeSeries<Integer> createTimeSeries_(String name) {
        return new CountTimeSeries(name);
    }
}
