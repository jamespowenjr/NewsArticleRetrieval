package com.bah.finance.wordsearch;

import com.bah.finance.wordsearch.timeseries.DateTimeSeries;
import com.bah.finance.wordsearch.util.Range;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class FileLoaderTest {

    public static final String WORDS_PATH = "/home/brendon/data/gresham/news/timeseries/words";
    public static final int FILE_COUNT = 8849;

    private TradingDateMap dateMap_;

    @Before
    public void setUp() throws Exception {
        dateMap_ = new TradingDateMap();
        dateMap_.loadFromFile(new File(TradingDateMapTest.FILE_PATH));
        TimeSeriesFileLoader<Integer> wordFileLoader = new CountTimeSeriesFileLoader(
                FileLoaderTest.WORDS_PATH, dateMap_);
    }

    @Test
    public void testLoad() throws Exception {
        CountTimeSeriesFileLoader loader = new CountTimeSeriesFileLoader(WORDS_PATH, new TradingDateMap(new File(TradingDateMapTest.FILE_PATH)));
        Assert.assertEquals((long) 8849, (long)loader.getAllSeriesNames().size());

        DateTimeSeries<Integer> timeseries = loader.load("biodiesel");
        Range<Integer> timeframe = new Range<Integer>(
                dateMap_.asTradingDate(20010628, true),
                dateMap_.asTradingDate(20021015, true)
        );
        List<Integer> list = timeseries.toList(timeframe);
        Assert.assertEquals((long)1, (long)list.get(0));
        Assert.assertEquals((long)16, (long)list.get(dateMap_.asTradingDate(20020512, true) - dateMap_.asTradingDate(20010628, true)));
        Assert.assertEquals((long)0, (long)list.get(dateMap_.asTradingDate(20011201, true) - dateMap_.asTradingDate(20010628, true)));
    }
}
