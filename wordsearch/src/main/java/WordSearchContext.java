public class WordSearchContext {

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

    private MemoryCache<DateTimeSeries<Double>> pricesCache_;
    private MemoryCache<DateTimeSeries<Integer>> wordsCache_;
    private String[] allWords_;
    private ResultCollector<WordMatch> collector_;
}
