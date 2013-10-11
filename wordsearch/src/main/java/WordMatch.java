import java.util.List;

public class WordMatch {

    public List<String> getWords() {
        return words_;
    }

    public String getEquity() {
        return equity_;
    }

    public Range<Integer> getTimeframe() {
        return timeframe_;
    }

    public double getPValue() {
        return pValue_;
    }

    public WordMatch(List<String> words, String equity, Range<Integer> timeframe, double pValue) {
        words_ = words;
        equity_ = equity;
        timeframe_ = timeframe;
        pValue_ = pValue;
    }

    private List<String> words_;
    private String equity_;
    private Range<Integer> timeframe_;
    private double pValue_;
}
