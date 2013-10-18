package com.bah.finance.wordsearch;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class TradingDateMapTest {

    public static final String FILE_PATH = "/home/brendon/data/gresham/news/timeseries/trading_dates.txt";
    public static final int FILE_SIZE = 5360;

    private static TradingDateMap dateMap_;

    @Before
    public void setUp() throws Exception {
        dateMap_ = new TradingDateMap();
        dateMap_.loadFromFile(new File(FILE_PATH));
    }

    @Test
    public void testAsTradingDate() throws Exception {
        Assert.assertEquals((long) 0, (long) dateMap_.asTradingDate(19930101));
        Assert.assertEquals((long)5359, (long)dateMap_.asTradingDate(20130725));
        Assert.assertEquals((long)5327, (long)dateMap_.asTradingDate(20130608, TradingDateMap.DateSearchType.Next));
        Assert.assertNull(dateMap_.asTradingDate(20130608, TradingDateMap.DateSearchType.ExactOnly));
        Assert.assertNull(dateMap_.asTradingDate(999999999, TradingDateMap.DateSearchType.Next));
    }

    @Test
    public void testAsRealDate() throws Exception {
        Assert.assertEquals((long) 19930101, (long)dateMap_.asRealDate(0));
        Assert.assertEquals((long) 19930104, (long)dateMap_.asRealDate(1));
        Assert.assertEquals((long) 19930105, (long)dateMap_.asRealDate(2));
        Assert.assertEquals((long) 20130725, (long)dateMap_.asRealDate(5359));
    }

    @Test
    public void testLoadFromFile() throws Exception {
        TradingDateMap map = new TradingDateMap();
        map.loadFromFile(new File(FILE_PATH));
        Assert.assertEquals(FILE_SIZE, map.size());
    }

}
