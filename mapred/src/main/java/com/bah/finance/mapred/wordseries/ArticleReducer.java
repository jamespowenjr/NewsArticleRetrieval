package com.bah.finance.mapred.wordseries;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ArticleReducer implements Reducer<Text, LongWritable, Text, TimeSeriesWritable> {

    @Override
    public void reduce(Text word, Iterator<LongWritable> dates, OutputCollector<Text, TimeSeriesWritable> collector, Reporter reporter) throws IOException {
        Map<Long, Integer> dateCounts = new HashMap<Long, Integer>();
        while (dates.hasNext()) {
            long date = dates.next().get();
            Integer count = dateCounts.get(date);
            if (count == null) {
                dateCounts.put(date, 1);
            } else {
                dateCounts.put(date, count + 1);
            }
        }

        collector.collect(word, new TimeSeriesWritable(dateCounts));
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void configure(JobConf entries) {

    }
}
