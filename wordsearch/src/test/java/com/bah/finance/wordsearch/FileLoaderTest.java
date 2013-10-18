package com.bah.finance.wordsearch;

import com.bah.finance.wordsearch.loader.CountTimeSeriesFileLoader;
import com.bah.finance.wordsearch.loader.TimeSeriesFileLoader;
import com.bah.finance.wordsearch.timeseries.DateTimeSeries;
import com.bah.finance.wordsearch.util.Range;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class FileLoaderTest {

    public static final String WORDS_PATH = "/home/brendon/data/gresham/news/timeseries/words";

    private TradingDateMap dateMap_;

    @Before
    public void setUp() throws Exception {
        dateMap_ = new TradingDateMap();
        dateMap_.loadFromFile(new File(TradingDateMapTest.FILE_PATH));
    }

    @Test
    public void testLoad() throws Exception {
        CountTimeSeriesFileLoader loader = new CountTimeSeriesFileLoader(WORDS_PATH, new TradingDateMap(new File(TradingDateMapTest.FILE_PATH)));
        Assert.assertEquals((long) 8849, (long)loader.getAllSeriesNames().size());

        DateTimeSeries<Integer> timeseries = loader.load("biodiesel");
        Range<Integer> timeframe = new Range<Integer>(
                dateMap_.asTradingDate(20010628, TradingDateMap.DateSearchType.Next),
                dateMap_.asTradingDate(20021015, TradingDateMap.DateSearchType.Next)
        );
        List<Integer> list = timeseries.toList(timeframe);
        Assert.assertEquals((long)1, (long)list.get(0));
        Assert.assertEquals((long)16, (long)list.get(dateMap_.asTradingDate(20020512, TradingDateMap.DateSearchType.Next) - dateMap_.asTradingDate(20010628, TradingDateMap.DateSearchType.Next)));
        Assert.assertEquals((long)0, (long)list.get(dateMap_.asTradingDate(20011201, TradingDateMap.DateSearchType.Next) - dateMap_.asTradingDate(20010628, TradingDateMap.DateSearchType.Next)));
    }
}
