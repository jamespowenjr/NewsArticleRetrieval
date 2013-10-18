package com.bah.finance.wordsearch;

import com.bah.finance.wordsearch.timeseries.DateTimeSeries;
import com.bah.finance.wordsearch.util.PropertyException;
import com.bah.finance.wordsearch.util.Utils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;

// TODO: Because this will be called by multiple threads, in future implementations we might want to
// have the collect method place matches onto a queue and have a separate thread polling and writing those
// matches to a file.  However, right now we intend to use all of a machine's cores whenever this runs,
// so we don't want to have another thread preempting the main search threads.
public class FileOutputCollector implements ResultCollector<WordMatch> {

    @Override
    public void configure(Properties props) throws PropertyException {
        String outputPath = Utils.getConfigValue(props, OUTPUT_PATH_KEY_, DEFAULT_OUTPUT_PATH_);
        File outputFile = new File(outputPath);
        try {
            stream_ = new FileOutputStream(outputFile, true);
        } catch (FileNotFoundException e) {
            throw new PropertyException("Invalid output file path " + outputPath);
        }
    }

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
            SortedMap<Integer, Integer> subSeries =
                    series.getValues().subMap(result.getTimeframe().getStart(), result.getTimeframe().getEnd()+ 1);

            for (Map.Entry<Integer, Integer> entry : subSeries.entrySet()) {
                wordCount += entry.getValue();
            }
            builder.append(wordCount);
        }
        builder.append(',');

        builder.append(result.getEquity()).append(',');
        builder.append(dateMap_.asRealDate(result.getTimeframe().getStart())).append(',');
        builder.append(dateMap_.asRealDate(result.getTimeframe().getEnd())).append(',');
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
        if (stream_ != null) {
            try {
                stream_.close();
            } catch (IOException e) {

            }
        }
    }


    public FileOutputCollector(TradingDateMap dateMap) {
        dateMap_ = dateMap;
    }

    private FileOutputStream stream_;
    private TradingDateMap dateMap_;

    private static final String OUTPUT_PATH_KEY_ = "data.output_path";
    private static final String DEFAULT_OUTPUT_PATH_ = "output.txt";

    private final static Logger logger_ = Logger.getLogger(FileOutputCollector.class);
}
