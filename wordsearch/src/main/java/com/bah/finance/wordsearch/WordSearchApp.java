package com.bah.finance.wordsearch;

import java.io.File;
import java.util.Set;

public class WordSearchApp {

    public static final int DEFAULT_THREAD_COUNT = 4;


    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: WordSearchApp <data_path> [thread_count]");
            System.exit(0);
        }

        TradingDateMap dateMap = new TradingDateMap(new File(args[0], DATES_FILE_));
        int threadCount;
        if (args.length >= 2) {
            try {
                threadCount = Integer.parseInt(args[1]);
                if (threadCount <= 0) {
                    throw new Exception();
                }
            } catch (Exception e) {
                System.err.println("Thread count must be a positive integer");
                return;
            }
        } else {
            threadCount = DEFAULT_THREAD_COUNT;
        }
        System.out.println(String.format("Using %d threads", threadCount));


        TimeSeriesFileLoader<Integer> wordFileLoader = new CountTimeSeriesFileLoader(
                new File(args[0], WORD_TIME_SERIES_DIRECTORY_).toString(), dateMap);
        TimeSeriesFileLoader<Double> priceFileLoader = new PriceTimeSeriesFileLoader(
                new File(args[0], PRICES_TIME_SERIES_DIRECTORY_).toString(), dateMap);

        MemoryCache<DateTimeSeries<Integer>> wordsCache = new MemoryCache<DateTimeSeries<Integer>>(wordFileLoader);
        MemoryCache<DateTimeSeries<Double>> pricesCache = new MemoryCache<DateTimeSeries<Double>>(priceFileLoader);

        Set<String> allWords = wordFileLoader.getAllSeriesNames();

        ResultCollector<WordMatch> collector = new FileOutputCollector(new File(OUTPUT_PATH_), dateMap);

        WordSearchContext context = new WordSearchContext();
        context.setAllWords(allWords.toArray(new String[allWords.size()]));
        context.setWordsCache(wordsCache);
        context.setPricesCache(pricesCache);
        context.setCollector(collector);
        context.setDateMap(dateMap);

        Thread[] threads = new Thread[threadCount];
        for (int i = 0 ; i < threadCount ; ++i) {
            Thread thread = new Thread(new WordSearchJob(context));
            threads[i] = thread;
            thread.start();
        }

        for (int i = 0 ; i < threadCount ; ++i) {
            while (true) {
                try {
                    threads[i].join();
                    break;
                } catch (InterruptedException e) { }
            }
        }

        collector.finalize();
    }


    // TODO: make these configurable
    private static final String WORD_TIME_SERIES_DIRECTORY_ = "words";
    private static final String PRICES_TIME_SERIES_DIRECTORY_ = "prices";
    private static final String OUTPUT_PATH_ = "output.txt";
    private static final String DATES_FILE_ = "trading_dates.txt";
}
