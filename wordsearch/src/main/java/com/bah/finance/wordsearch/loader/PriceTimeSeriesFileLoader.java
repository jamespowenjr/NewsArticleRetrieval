package com.bah.finance.wordsearch.loader;

import com.bah.finance.wordsearch.TradingDateMap;
import com.bah.finance.wordsearch.timeseries.DateTimeSeries;
import com.bah.finance.wordsearch.timeseries.PriceTimeSeries;

public class PriceTimeSeriesFileLoader extends TimeSeriesFileLoader<Double> {

    public PriceTimeSeriesFileLoader(String searchDirectory, TradingDateMap dateMap) {
        super(searchDirectory, dateMap);
    }

    @Override
    protected Double parseValue_(String value) {
        return Double.parseDouble(value);
    }

    @Override
    protected boolean forceNextDate_() {
        return false;
    }

    @Override
    protected DateTimeSeries<Double> createTimeSeries_(String name) {
        return new PriceTimeSeries(name);
    }
}
