package com.bah.finance.wordsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class DateTimeSeries<V> extends TimeSeries<Integer, V> {

    public List<V> toList(Range<Integer> timeRange) {

        if (timeRange.start >= timeRange.end) {
            return new ArrayList<V>(0);
        }

        int listSize = timeRange.end - timeRange.start + 1;

        List<V> list = new ArrayList<V>();
        for (int i = 0 ; i < listSize ; ++i) {
            list.add(defaultValue_());
        }

        for (Map.Entry<Integer, V> entry : getValues().subMap(timeRange.start, timeRange.end + 1).entrySet()) {
            list.set(entry.getKey() - timeRange.start, entry.getValue());
        }

        return list;
    }


    public DateTimeSeries(String name) {
        super(name);
    }


    public <V2 extends V> DateTimeSeries(String name, Map<Integer, V2> map) {
        super(name, map);
    }


    protected abstract V defaultValue_();

}
