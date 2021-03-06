package com.bah.finance.wordsearch.timeseries;

public class CountTimeSeries extends DateTimeSeries<Integer> {

   public void addCount(int date, int value) {
        // This kind of violates the spirit of the interface-- the map should never be modified via
        // calls to getValues(), and this should probably be done via a protected interface instead.
        Integer previous = getValues().get(date);
        if (previous == null) {
            previous = 0;
        }
        getValues().put(date, previous + value);
    }

    public CountTimeSeries(String name) {
        super(name);
    }

    @Override
    protected Integer defaultValue_() {
        return 0;
    }
}
