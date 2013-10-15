package com.bah.finance.wordsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DateTimeSeries<V> extends TimeSeries<Integer, V> {

    public List<V> toList(Range<Integer> timeRange) {
        if (getValues().isEmpty()) {
            return new ArrayList<V>(0);
        }

        int start = Math.max(getValues().firstKey(), timeRange.start);
        int end = Math.min(getValues().lastKey(), timeRange.end);
        int listSize = end - start + 1;

        List<V> list = new ArrayList<V>();
        for (int i = 0 ; i < listSize ; ++i) {
            list.add(null);
        }

        for (Map.Entry<Integer, V> entry : getValues().subMap(start, end + 1).entrySet()) {
            list.set(entry.getKey() - start, entry.getValue());
        }

        return list;
    }


    public DateTimeSeries(String name) {
        super(name);
    }


    public <V2 extends V> DateTimeSeries(String name, Map<Integer, V2> map) {
        super(name, map);
    }

}
