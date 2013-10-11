package com.bah.finance.mapred.wordlist;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.Iterator;

public class WordListReducer implements Reducer<Text, IntWritable, Text, Text> {

    public static final String THRESHOLD_CONFIG_KEY_ = "wordlist.threshold";
    public static final int DEFAULT_COUNT_THRESHOLD_ = 100;

    public static final Text EMPTY_TEXT = new Text("");

    @Override
    public void reduce(Text word, Iterator<IntWritable> counts, OutputCollector<Text, Text> collector, Reporter reporter) throws IOException {
        int wordCount = 0;
        while (counts.hasNext()) {
            wordCount += counts.next().get();
            if (wordCount >= threshold_) {
                collector.collect(EMPTY_TEXT, word);
                break;
            }
        }
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void configure(JobConf conf) {
        String thresholdStr = conf.get(THRESHOLD_CONFIG_KEY_);
        if (thresholdStr != null) {
            try {
                threshold_ = Integer.parseInt(thresholdStr);
            } catch (NumberFormatException e) {
                // TODO: Log an error here or something
            }
        }
    }


    private int threshold_ = DEFAULT_COUNT_THRESHOLD_;
}
