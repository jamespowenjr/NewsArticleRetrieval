package com.bah.finance.wordsearch;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

// TODO: Because this will be called by multiple threads, in future implementations we might want to
// have the collect method place matches onto a queue and have a separate thread polling and writing those
// matches to a file.  However, right now we intend to use all of a machine's cores whenever this runs,
// so we don't want to have another thread preempting the main search threads.
public class FileOutputCollector implements ResultCollector<WordMatch> {

    @Override
    public void collect(WordMatch result) {
        StringBuilder builder = new StringBuilder();

        boolean firstWord = true;
        for (DateTimeSeries<Integer> series : result.getSeries()) {
            if (firstWord) {
                firstWord = false;
            } else {
                builder.append("|");
            }
            builder.append(series.getName()).append('*');

            int wordCount = 0;
            for (Map.Entry<Integer, Integer> entry : series.getValues().subMap(result.getTimeframe().start, result.getTimeframe().end + 1).entrySet()) {
                wordCount += entry.getValue();
            }
            builder.append(wordCount);
        }
        builder.append(',');

        builder.append(result.getEquity()).append(',');
        builder.append(dateMap_.asRealDate(result.getTimeframe().start)).append(',');
        builder.append(dateMap_.asRealDate(result.getTimeframe().end)).append(',');
        builder.append(result.getPValue()).append('\n');

        try {
            synchronized (this) {
                stream_.write(builder.toString().getBytes());
            }
        } catch (IOException e) {
            logger_.error(String.format("Error writing result to file: %s\nResult was %s", e.getMessage(), builder.toString()));
        }
    }


    public void finalize() {
        try {
            stream_.close();
        } catch (IOException e) {

        }
    }


    public FileOutputCollector(File file, TradingDateMap dateMap) throws FileNotFoundException {
        stream_ = new FileOutputStream(file, true);
        dateMap_ = dateMap;
    }

    private FileOutputStream stream_;
    private TradingDateMap dateMap_;

    private final static Logger logger_ = Logger.getLogger(FileOutputCollector.class);
}
