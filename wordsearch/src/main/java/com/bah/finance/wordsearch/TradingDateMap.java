package com.bah.finance.wordsearch;

import com.bah.finance.wordsearch.util.Configurable;
import com.bah.finance.wordsearch.util.PropertyException;
import com.bah.finance.wordsearch.util.Utils;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class TradingDateMap implements Configurable {

    @Override
    public void configure(Properties props) throws PropertyException {
        String dateFilePath = Utils.getConfigValue(props, DATES_FILE_KEY_);
        File dateFile = new File(dateFilePath);
        try {
            loadFromFile(dateFile);
        } catch (IOException e) {
            throw new PropertyException(String.format("Error reading from trading dates file %s: %s", dateFilePath, e.getMessage()));
        }
    }


    public enum DateSearchType { ExactOnly, Next, Previous }


    public Integer asTradingDate(int date) {
        return asTradingDate(date, DateSearchType.ExactOnly);
    }


    // When useNext is true, if the given date is not a valid trading date, then return the next
    // valid trading date.  If it is false, then return null.
    public Integer asTradingDate(int date, DateSearchType type) {
        Integer tradingDate = map_.get(date);
        if (tradingDate != null) {
            return tradingDate;
        } else if (type == DateSearchType.ExactOnly) {
            return null;
        } else if (type == DateSearchType.Next) {
            // This is technically O(n), but in practice we know that there will never be more than 3 or 4 consecutive
            // non-trading days, so the number of iterations is strictly bounded and the real runtime is O(1).
            while (tradingDate == null && date <= maxValue_) {
                tradingDate = map_.get(++date);
            }
            return tradingDate;
        } else if (type == DateSearchType.Previous) {
            while (tradingDate == null && date >= minValue_) {
                tradingDate = map_.get(--date);
            }
            return tradingDate;
        } else {
            throw new IllegalArgumentException("Invalid search type");
        }
    }


    // No notion of searching for next days here-- if the date is outside of our range, then it effectively does not exist
    public Integer asRealDate(int tradingDate) {
        return map_.inverse().get(tradingDate);
    }


    public int size() {
        return map_.size();
    }


    public Integer startDate() {
        return minValue_;
    }


    public Integer endDate() {
        return maxValue_;
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
        minValue_ = null;
        maxValue_ = null;
        while ((line = reader.readLine()) != null) {
            Integer date = Integer.parseInt(line);
            map_.put(date, lineNumber++);
            if (minValue_ == null) {
                minValue_ = date;
            }
            maxValue_ = date;
        }
    }


    public Map<Integer, Integer> getMap() {
        return map_;
    }


    public TradingDateMap() {
        map_ = HashBiMap.create();
        minValue_ = null;
        maxValue_ = null;
    }


    public TradingDateMap(File file) throws IOException {
        this();
        loadFromFile(file);
    }

    private final BiMap<Integer, Integer> map_;
    private Integer minValue_;
    private Integer maxValue_;

    private static final String DATES_FILE_KEY_ = "data.dates_path";
}
