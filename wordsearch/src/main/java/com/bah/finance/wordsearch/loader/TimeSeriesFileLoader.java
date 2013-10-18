package com.bah.finance.wordsearch.loader;

import com.bah.finance.wordsearch.TradingDateMap;
import com.bah.finance.wordsearch.timeseries.DateTimeSeries;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;


public abstract class TimeSeriesFileLoader<V> extends FileLoader<DateTimeSeries<V>> {

    public Set<String> getAllSeriesNames() {
        Set<String> names = new HashSet<String>();
        File directory = new File(searchDirectory_);
        File[] files = directory.listFiles();

        if (files == null) {
            return names;
        }

        for (File file : files) {
            String filename = file.getName();
            if (!file.isFile() || !FilenameUtils.getExtension(filename).equals(FILE_EXTENSION_)) {
                continue;
            }

            names.add(FilenameUtils.getBaseName(filename));
        }

        return names;
    }


    public TimeSeriesFileLoader(String searchDirectory, TradingDateMap dateMap) {
        searchDirectory_ = searchDirectory;
        dateMap_ = dateMap;
    }


    private String searchDirectory_;
    private TradingDateMap dateMap_;

    // TODO: make configurable
    private final static String FILE_EXTENSION_ = "txt";
    private final static String FIELD_SEPARATOR_ = "\\s+";

    private final static Logger logger_ = Logger.getLogger(TimeSeriesFileLoader.class);


    @Override
    protected String createPath_(String query) {
        return new File(searchDirectory_, query + "." + FILE_EXTENSION_).toString();
    }


    @Override
    protected DateTimeSeries<V> parseFile_(String query, String path, FileInputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        int lineNumber = 0;
        DateTimeSeries<V> timeSeries = createTimeSeries_(query);

        while ((line = reader.readLine()) != null) {
            ++lineNumber;
            String[] fields = line.split(FIELD_SEPARATOR_);

            Integer date;
            V value;
            try {
                if (fields.length != 2) {
                    throw new ParseException("All lines must have exactly 2 fields", 0);
                }
                date = Integer.parseInt(fields[0]);
                date = dateMap_.asTradingDate(date, forceNextDate_());
                if (date == null) {
                    continue;
                }

                value = parseValue_(fields[1]);
                timeSeries.addEntry(date, value);
            } catch (ParseException e) {
                logger_.error(String.format("Skipping line %d in file %s: %s", lineNumber, path, e.getMessage()));
            }
        }

        return timeSeries;
    }


    protected abstract V parseValue_(String value);
    protected abstract boolean forceNextDate_();
    protected abstract DateTimeSeries<V> createTimeSeries_(String name);
}
