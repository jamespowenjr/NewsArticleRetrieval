package com.bah.finance.wordsearch;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class MemoryCacheTest {

    private TradingDateMap dateMap_;
    private MemoryCache<DateTimeSeries<Integer>> cache_;

    @Before
    public void setUp() throws Exception {
        dateMap_ = new TradingDateMap();
        dateMap_.loadFromFile(new File(TradingDateMapTest.FILE_PATH));
        TimeSeriesFileLoader<Integer> wordFileLoader = new CountTimeSeriesFileLoader(
                FileLoaderTest.WORDS_PATH, dateMap_);
        cache_ = new MemoryCache<DateTimeSeries<Integer>>(wordFileLoader);
    }

    @Test
    public void testGet() throws Exception {
        DateTimeSeries<Integer> timeseries = cache_.get("biodiesel");
        Range<Integer> timeframe = new Range<Integer>(
            dateMap_.asTradingDate(20010628, true),
            dateMap_.asTradingDate(20021015, true)
        );
        List<Integer> list = timeseries.toList(timeframe);
        Assert.assertEquals((long)1, (long)list.get(0));
        Assert.assertEquals((long)16, (long)list.get(dateMap_.asTradingDate(20020512, true) - dateMap_.asTradingDate(20010628, true)));
    }

}
