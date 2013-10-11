import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;

import java.util.*;


public class WordSearchJob implements Runnable {

    @Override
    public void run() {

    }


    public WordSearchJob(WordSearchContext context) {
        context_ = context;
        totalWords_ = context_.getAllWords().length;
    }


    private WordSearchContext context_;
    private final int totalWords_;
    private final Random random_ = new Random();

    private final static Logger logger_ = Logger.getLogger(WordSearchJob.class);


    // TODO: Make these configurable
    private final static int START_DATE_ = Utils.intFromDate(new LocalDate(1993, 1, 1).toDate());
    private final static int END_DATE_ = Utils.intFromDate(new LocalDate(2013, 1, 1).toDate());

    // TODO: also figure out reasonable values for these
    private final static int MIN_RANGE_ = 100;
    private final static int MAX_RANGE_ = 365 * 5;
    private final static int[] LAG_WINDOWS_ = { 1, 2, 3, 5, 7, 10 };
    private final static int BAG_SIZE_ = 5;


    private class TimeRange {
        public int start;
        public int end;
    }


    private int randomDate() {
        return random_.nextInt(END_DATE_ - START_DATE_ + 1) + START_DATE_;
    }


    private TimeRange getTimeRange() {
        TimeRange range = new TimeRange();
        int rangeSize;
        do {
            range.start = randomDate();
            range.end = randomDate();
            rangeSize = Math.abs(range.end - range.start);
        } while (rangeSize >= MIN_RANGE_ && rangeSize <= MAX_RANGE_);

        if (range.start > range.end) {
            range.start ^= range.end;
            range.end ^= range.start;
            range.start ^= range.end;
        }

        return range;
    }


    private String[] getWordBag() {
        Set<Integer> indices = new HashSet<Integer>();
        while (indices.size() < BAG_SIZE_) {
            indices.add(random_.nextInt(totalWords_));
        }

        String[] bag = new String[BAG_SIZE_];
        int i = 0;
        Iterator<Integer> it = indices.iterator();
        while (it.hasNext()) {
            bag[i] = context_.getAllWords()[it.next()];
            ++i;
        }

        return bag;
    }


    private CountTimeSeries combineTimeSeries(Iterable<String> words) {
        CountTimeSeries timeSeries = new CountTimeSeries(StringUtils.join(words, "|"));
        for (String word : words) {
            TimeSeries<Integer, Integer> wordSeries = context_.getWordsCache().get(word);
            if (wordSeries == null) {
                logger_.error(String.format("Unable to retrieve time series for word %s", word));
                continue;
            }

            for (Map.Entry<Integer, Integer> entry : wordSeries.getValues().entrySet()) {
                timeSeries.addCount(entry.getKey(), entry.getValue());
            }
        }

        return timeSeries;
    }
}
