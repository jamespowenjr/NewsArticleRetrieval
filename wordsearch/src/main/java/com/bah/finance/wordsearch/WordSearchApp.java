package com.bah.finance.wordsearch;

import com.bah.finance.wordsearch.loader.CountTimeSeriesFileLoader;
import com.bah.finance.wordsearch.loader.MemoryCache;
import com.bah.finance.wordsearch.loader.PriceTimeSeriesFileLoader;
import com.bah.finance.wordsearch.loader.TimeSeriesFileLoader;
import com.bah.finance.wordsearch.timeseries.DateTimeSeries;
import com.bah.finance.wordsearch.util.Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class WordSearchApp {

    public static void main(String[] args) throws Exception {

        Properties props = new Properties();
        try {
            props.load(new FileInputStream(CONFIG_FILE_NAME_));
        } catch (IOException e) {
            System.err.println("!!! Unable to load configuration file !!!");
        }

        int threadCount = Utils.getConfigInt(props, THREAD_COUNT_KEY_, DEFAULT_THREAD_COUNT_);
        System.out.println(String.format("Using %d threads", threadCount));

        // TODO: We really need to convert dates as a preprocessing step (outside of this program) because now
        // everything is completely dependent on the conversion map
        TradingDateMap dateMap = new TradingDateMap();

        TimeSeriesFileLoader<Integer> wordFileLoader = new CountTimeSeriesFileLoader(WORDS_PATH_KEY_, dateMap);
        TimeSeriesFileLoader<Double> priceFileLoader = new PriceTimeSeriesFileLoader(PRICES_PATH_KEY_, dateMap);

        MemoryCache<DateTimeSeries<Integer>> wordsCache = new MemoryCache<DateTimeSeries<Integer>>(wordFileLoader);
        MemoryCache<DateTimeSeries<Double>> pricesCache = new MemoryCache<DateTimeSeries<Double>>(priceFileLoader);
        ResultCollector<WordMatch> collector = new FileOutputCollector(dateMap);
        DateRangeGenerator dateRangeGenerator = new RandomDateRangeGenerator(dateMap);

        dateMap.configure(props);
        wordsCache.configure(props);
        pricesCache.configure(props);
        collector.configure(props);
        dateRangeGenerator.configure(props);

        WordSearchContext context = new WordSearchContext();
        Set<String> allWords = wordFileLoader.getAllSeriesNames();
        context.setAllWords(allWords.toArray(new String[allWords.size()]));
        context.setWordsCache(wordsCache);
        context.setPricesCache(pricesCache);
        context.setCollector(collector);
        context.setDateRangeGenerator(dateRangeGenerator);

        Thread[] threads = new Thread[threadCount];
        WordSearchJob[] jobs = new WordSearchJob[threadCount];

        for (int i = 0 ; i < threadCount ; ++i) {
            WordSearchJob job = new WordSearchJob(context);
            job.configure(props);
            jobs[i] = job;

            Thread thread = new Thread(job);
            threads[i] = thread;
            thread.start();
        }

        int totalIterations = 0;
        for (int i = 0 ; i < threadCount ; ++i) {
            while (true) {
                try {
                    threads[i].join();
                    totalIterations += jobs[i].getCompletedIterations();
                    break;
                } catch (InterruptedException e) { }
            }
        }

        System.out.println(String.format("%d iterations completed", totalIterations));
    }


    private static final String CONFIG_FILE_NAME_ = "wordsearch.config";

    private static final String THREAD_COUNT_KEY_ = "thread_count";
    private static final int DEFAULT_THREAD_COUNT_ = 4;

    private static final String WORDS_PATH_KEY_ = "data.words_path";
    private static final String PRICES_PATH_KEY_ = "data.prices_path";
}
