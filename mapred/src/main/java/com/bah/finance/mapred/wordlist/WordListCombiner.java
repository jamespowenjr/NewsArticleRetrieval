package com.bah.finance.mapred.wordlist;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.Iterator;

public class WordListCombiner implements Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    public void reduce(Text word, Iterator<IntWritable> counts, OutputCollector<Text, IntWritable> collector, Reporter reporter) throws IOException {
        int wordCount = 0;
        while (counts.hasNext()) {
            wordCount += counts.next().get();
        }

        collector.collect(word, new IntWritable(wordCount));
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void configure(JobConf entries) {

    }
}
