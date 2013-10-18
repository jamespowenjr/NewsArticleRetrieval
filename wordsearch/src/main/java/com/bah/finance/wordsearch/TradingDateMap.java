package com.bah.finance.wordsearch;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.io.*;
import java.util.Map;

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
            // This is technically O(n), but in practice we know that there will never be more than 3 or 4 consecutive
            // non-trading days, so the number of iterations is strictly bounded and the real runtime is O(1).
            while (tradingDate == null && date <= maxValue_) {
                tradingDate = map_.get(++date);
            }
            return tradingDate;
        }
    }


    // No notion of searching for next days here-- if the date is outside of our range, then it effectively does not exist
    public Integer asRealDate(int tradingDate) {
        return map_.inverse().get(tradingDate);
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
        // This also assumes that the dates in the file are in order
        map_.clear();
        while ((line = reader.readLine()) != null) {
            Integer date = Integer.parseInt(line);
            map_.put(date, lineNumber++);
            maxValue_ = date;
        }
    }


    public Map<Integer, Integer> getMap() {
        return map_;
    }


    public TradingDateMap() {
        map_ = HashBiMap.create();
        maxValue_ = null;
    }


    public TradingDateMap(File file) throws IOException {
        this();
        loadFromFile(file);
    }

    private BiMap<Integer, Integer> map_;
    private Integer maxValue_;
}
