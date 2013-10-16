package com.bah.finance.wordsearch;

import java.io.File;
import java.util.Set;

public class WordSearchApp {

    public static final int THREAD_COUNT = 4;


    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: WordSearchApp <data_path>");
            System.exit(0);
        }

        TradingDateMap dateMap = new TradingDateMap(new File(args[0], DATES_FILE_));

        TimeSeriesFileLoader<Integer> wordFileLoader = new CountTimeSeriesFileLoader(
                new File(args[0], WORD_TIME_SERIES_DIRECTORY_).toString(), dateMap);
        TimeSeriesFileLoader<Double> priceFileLoader = new PriceTimeSeriesFileLoader(
                new File(args[0], PRICES_TIME_SERIES_DIRECTORY_).toString(), dateMap);

        MemoryCache<DateTimeSeries<Integer>> wordsCache = new MemoryCache<DateTimeSeries<Integer>>(wordFileLoader);
        MemoryCache<DateTimeSeries<Double>> pricesCache = new MemoryCache<DateTimeSeries<Double>>(priceFileLoader);

        Set<String> allWords = wordFileLoader.getAllSeriesNames();

        ResultCollector<WordMatch> collector = new FileOutputCollector(OUTPUT_PATH_);

        WordSearchContext context = new WordSearchContext();
        context.setAllWords(allWords.toArray(new String[allWords.size()]));
        context.setWordsCache(wordsCache);
        context.setPricesCache(pricesCache);
        context.setCollector(collector);
        context.setDateMap(dateMap);

        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0 ; i < THREAD_COUNT ; ++i) {
            Thread thread = new Thread(new WordSearchJob(context));
            threads[i] = thread;
            thread.start();
        }

        for (int i = 0 ; i < THREAD_COUNT ; ++i) {
            while (true) {
                try {
                    threads[i].join();
                    break;
                } catch (InterruptedException e) { }
            }
        }

    }


    // TODO: make these configurable
    private static final String WORD_TIME_SERIES_DIRECTORY_ = "words";
    private static final String PRICES_TIME_SERIES_DIRECTORY_ = "prices";
    private static final String OUTPUT_PATH_ = "output.txt";
    private static final String DATES_FILE_ = "trading_dates.txt";
}
