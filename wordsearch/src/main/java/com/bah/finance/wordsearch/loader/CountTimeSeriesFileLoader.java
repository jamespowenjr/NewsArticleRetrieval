package com.bah.finance.wordsearch.loader;

import com.bah.finance.wordsearch.TradingDateMap;
import com.bah.finance.wordsearch.timeseries.CountTimeSeries;
import com.bah.finance.wordsearch.timeseries.DateTimeSeries;

public class CountTimeSeriesFileLoader extends TimeSeriesFileLoader<Integer> {

    public CountTimeSeriesFileLoader(String searchDirectory, TradingDateMap dateMap) {
        super(searchDirectory, dateMap);
    }

    @Override
    protected Integer parseValue_(String value) {
        return Integer.parseInt(value);
    }

    @Override
    protected TradingDateMap.DateSearchType dateSearchType_() {
        return TradingDateMap.DateSearchType.Next;
    }

    @Override
    protected DateTimeSeries<Integer> createTimeSeries_(String name) {
        return new CountTimeSeries(name);
    }
}
