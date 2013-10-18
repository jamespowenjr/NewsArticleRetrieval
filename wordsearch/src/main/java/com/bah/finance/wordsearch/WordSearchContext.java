package com.bah.finance.wordsearch;

import com.bah.finance.wordsearch.loader.MemoryCache;
import com.bah.finance.wordsearch.timeseries.DateTimeSeries;

public class WordSearchContext {

    // TODO: If we reach a point where the cache will have continuous turnover, it may be worth using a separate
    // cache for each thread and using some kind of strategy to reduce turnover, such as having each thread pick
    // a subset of words equal to the size of the cache, and picking a number of sub-subsets of that before
    // moving on to a new subset.  This obviously sacrifices a degree of randomness, but it would greatly increase
    // overall throughput.

    public MemoryCache<DateTimeSeries<Double>> getPricesCache() {
        return pricesCache_;
    }

    public void setPricesCache(MemoryCache<DateTimeSeries<Double>> pricesCache) {
        pricesCache_ = pricesCache;
    }

    public MemoryCache<DateTimeSeries<Integer>> getWordsCache() {
        return wordsCache_;
    }

    public void setWordsCache(MemoryCache<DateTimeSeries<Integer>> wordsCache) {
        wordsCache_ = wordsCache;
    }

    public String[] getAllWords() {
        return allWords_;
    }

    public void setAllWords(String[] allWords) {
        allWords_ = allWords;
    }

    public ResultCollector<WordMatch> getCollector() {
        return collector_;
    }

    public void setCollector(ResultCollector<WordMatch> collector) {
        this.collector_ = collector;
    }

    public DateRangeGenerator getDateRangeGenerator() {
        return dateRangeGenerator_;
    }

    public void setDateRangeGenerator(DateRangeGenerator generator) {
        dateRangeGenerator_ = generator;
    }

    private MemoryCache<DateTimeSeries<Double>> pricesCache_;
    private MemoryCache<DateTimeSeries<Integer>> wordsCache_;
    private String[] allWords_;
    private ResultCollector<WordMatch> collector_;
    private DateRangeGenerator dateRangeGenerator_;
}
