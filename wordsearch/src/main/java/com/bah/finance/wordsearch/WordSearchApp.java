package com.bah.finance.wordsearch;

import com.bah.finance.wordsearch.loader.CountTimeSeriesFileLoader;
import com.bah.finance.wordsearch.loader.MemoryCache;
import com.bah.finance.wordsearch.loader.PriceTimeSeriesFileLoader;
import com.bah.finance.wordsearch.loader.TimeSeriesFileLoader;
import com.bah.finance.wordsearch.timeseries.DateTimeSeries;
import com.bah.finance.wordsearch.util.Utils;

import java.io.File;
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

        String dateFilePath = Utils.getConfigValue(props, DATES_FILE_KEY_, String.class);

        System.out.print(String.format("Loading trading dates files from %s ...", dateFilePath));
        TradingDateMap dateMap = new TradingDateMap(new File(dateFilePath));
        System.out.println(" done");

        int threadCount = Utils.getConfigInt(props, THREAD_COUNT_KEY_, DEFAULT_THREAD_COUNT_);
        System.out.println(String.format("Using %d threads", threadCount));

        String wordsPath = Utils.getConfigValue(props, WORDS_PATH_KEY_, String.class);
        System.out.print(String.format("Searching for word time series in directory %s ...", wordsPath));
        TimeSeriesFileLoader<Integer> wordFileLoader = new CountTimeSeriesFileLoader(wordsPath, dateMap);
        Set<String> allWords = wordFileLoader.getAllSeriesNames();
        System.out.println(String.format(" %d files found", allWords.size()));

        String pricesPath = Utils.getConfigValue(props, PRICES_PATH_KEY_, String.class);
        System.out.print(String.format("Searching for price time series in directory %s ...", pricesPath));
        TimeSeriesFileLoader<Double> priceFileLoader = new PriceTimeSeriesFileLoader(pricesPath, dateMap);
        System.out.println(String.format(" %d files found", priceFileLoader.getAllSeriesNames().size()));

        MemoryCache<DateTimeSeries<Integer>> wordsCache = new MemoryCache<DateTimeSeries<Integer>>(wordFileLoader);
        MemoryCache<DateTimeSeries<Double>> pricesCache = new MemoryCache<DateTimeSeries<Double>>(priceFileLoader);

        String outputPath = Utils.getConfigValue(props, OUTPUT_PATH_KEY_, String.class, DEFAULT_OUTPUT_PATH_);
        ResultCollector<WordMatch> collector = new FileOutputCollector(new File(outputPath), dateMap);

        WordSearchContext context = new WordSearchContext();
        context.setAllWords(allWords.toArray(new String[allWords.size()]));
        context.setWordsCache(wordsCache);
        context.setPricesCache(pricesCache);
        context.setCollector(collector);
        context.setDateRangeGenerator(RandomDateRangeGenerator.class);

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

    private static final String DATES_FILE_KEY_ = "data.dates_path";
    private static final String WORDS_PATH_KEY_ = "data.words_path";
    private static final String PRICES_PATH_KEY_ = "data.prices_path";

    private static final String OUTPUT_PATH_KEY_ = "data.output_path";
    private static final String DEFAULT_OUTPUT_PATH_ = "output.txt";
}
