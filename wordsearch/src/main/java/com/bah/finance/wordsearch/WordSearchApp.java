package com.bah.finance.wordsearch;

import java.io.File;
import java.util.Set;

public class WordSearchApp {

    public static final int THREAD_COUNT = 4;


    public static void main(String[] args) {
        TimeSeriesFileLoader<Integer> wordFileLoader = new CountTimeSeriesFileLoader(
                new File(args[1], WORD_TIME_SERIES_DIRECTORY_).toString());
        TimeSeriesFileLoader<Double> priceFileLoader = new PriceTimeSeriesFileLoader(
                new File(args[1], PRICES_TIME_SERIES_DIRECTORY_).toString());

        MemoryCache<DateTimeSeries<Integer>> wordsCache = new MemoryCache<DateTimeSeries<Integer>>(wordFileLoader);
        MemoryCache<DateTimeSeries<Double>> pricesCache = new MemoryCache<DateTimeSeries<Double>>(priceFileLoader);

        Set<String> allWords = wordFileLoader.getAllSeriesNames();

        ResultCollector<WordMatch> collector = new FileOutputCollector(OUTPUT_PATH);

        WordSearchContext context = new WordSearchContext();
        context.setAllWords(allWords.toArray(new String[allWords.size()]));
        context.setWordsCache(wordsCache);
        context.setPricesCache(pricesCache);
        context.setCollector(collector);

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
    private static final String OUTPUT_PATH = "output.txt";
}
