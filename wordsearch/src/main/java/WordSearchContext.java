public class WordSearchContext {

    public TimeSeriesCache<Integer, Double> getPricesCache() {
        return pricesCache_;
    }

    public void setPricesCache(TimeSeriesCache pricesCache) {
        pricesCache_ = pricesCache;
    }

    public TimeSeriesCache<Integer, Integer> getWordsCache() {
        return wordsCache_;
    }

    public void setWordsCache(TimeSeriesCache wordsCache) {
        wordsCache_ = wordsCache;
    }

    public String[] getAllWords() {
        return allWords_;
    }

    public void setAllWords(String[] allWords) {
        allWords_ = allWords;
    }

    private TimeSeriesCache<Integer, Double> pricesCache_;
    private TimeSeriesCache<Integer, Integer> wordsCache_;
    private String[] allWords_;
}
