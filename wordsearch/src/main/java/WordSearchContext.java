public class WordSearchContext {

    public TimeSeriesCache getPricesCache() {
        return pricesCache_;
    }

    public void setPricesCache(TimeSeriesCache pricesCache) {
        pricesCache_ = pricesCache;
    }

    public TimeSeriesCache getWordsCache() {
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

    private TimeSeriesCache pricesCache_;
    private TimeSeriesCache wordsCache_;
    private String[] allWords_;
}
