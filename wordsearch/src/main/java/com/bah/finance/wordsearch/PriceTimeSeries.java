package com.bah.finance.wordsearch;

import java.util.Map;

public class PriceTimeSeries extends DateTimeSeries<Double> {

    public PriceTimeSeries(String name) {
        super(name);
    }

    public <V2 extends Double> PriceTimeSeries(String name, Map<Integer, V2> map) {
        super(name, map);
    }

    @Override
    protected Double defaultValue_() {
        return 0.0;
    }
}
