package com.bah.finance.wordsearch;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOutputCollector implements ResultCollector<WordMatch> {

    @Override
    public void collect(WordMatch result) {
        StringBuilder builder = new StringBuilder();
        builder.append(StringUtils.join(result.getWords(), "|")).append(',');
        builder.append(result.getEquity()).append(',');
        builder.append(result.getTimeframe().start).append('-').append(result.getTimeframe().end).append(',');
        builder.append(result.getPValue()).append('\n');

        try {
            stream_.write(builder.toString().getBytes());
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


    public FileOutputCollector(File file) throws FileNotFoundException {
        stream_ = new FileOutputStream(file, true);
    }

    private FileOutputStream stream_;

    private final static Logger logger_ = Logger.getLogger(FileOutputCollector.class);
}
