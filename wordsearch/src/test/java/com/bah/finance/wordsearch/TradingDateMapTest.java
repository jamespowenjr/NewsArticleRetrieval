package com.bah.finance.wordsearch;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class TradingDateMapTest {

    public static final String FILE_PATH = "/home/brendon/data/gresham/news/timeseries/trading_dates.txt";
    public static final int FILE_SIZE = 5360;

    @Test
    public void testAsTradingDate() throws Exception {
        TradingDateMap map = new TradingDateMap();
        map.loadFromFile(new File(FILE_PATH));
        Assert.assertEquals((long) 0, (long) map.asTradingDate(19930101));
        Assert.assertEquals((long)5359, (long)map.asTradingDate(20130725));
        Assert.assertEquals((long)5327, (long)map.asTradingDate(20130608, true));
        Assert.assertNull(map.asTradingDate(20130608, false));
    }

    @Test
    public void testLoadFromFile() throws Exception {
        TradingDateMap map = new TradingDateMap();
        map.loadFromFile(new File(FILE_PATH));
        Assert.assertEquals(FILE_SIZE, map.size());
    }

}
