package com.bah.finance.wordsearch;

import java.io.*;
import java.util.SortedMap;
import java.util.TreeMap;

public class TradingDateMap {

    public Integer asTradingDate(int date) {
        return asTradingDate(date, false);
    }


    // When useNext is true, if the given date is not a valid trading date, then return the next
    // valid trading date.  If it is false, then return null.
    public Integer asTradingDate(int date, boolean useNext) {
        Integer tradingDate = map_.get(date);
        if (tradingDate != null) {
            return tradingDate;
        } else if (!useNext) {
            return null;
        } else {
            SortedMap<Integer, Integer> greater = map_.tailMap(date);
            return greater.isEmpty() ? null : greater.firstKey();
        }
    }


    public Integer getStartDate() {
        return map_.isEmpty() ? null : map_.firstKey();
    }


    public Integer getEndDate() {
        return map_.isEmpty() ? null : map_.lastKey();
    }


    public int size() {
        return map_.size();
    }


    public void loadFromFile(File file) throws IOException {
        FileInputStream stream = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        String line;
        int lineNumber = 0;

        // TODO: This is essentially the same as writing to a list and performing binary search for each lookup,
        // but this is easier to write for now
        map_.clear();
        while ((line = reader.readLine()) != null) {
            Integer date = Integer.parseInt(line);
            map_.put(date, ++lineNumber);
        }
    }


    public TradingDateMap() {
        map_ = new TreeMap<Integer, Integer>();
    }


    public TradingDateMap(File file) throws IOException {
        this();
        loadFromFile(file);
    }

    private SortedMap<Integer, Integer> map_;
}
