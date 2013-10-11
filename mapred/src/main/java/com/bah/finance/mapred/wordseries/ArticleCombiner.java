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

public class ArticleCombiner implements Reducer<Text, LongWritable, Text, DateCount> {

    @Override
    public void reduce(Text word, Iterator<LongWritable> dates, OutputCollector<Text, DateCount> collector, Reporter reporter) throws IOException {
        Map<Long, Integer> counts = new HashMap<Long, Integer>();
        while (dates.hasNext()) {
            long date = dates.next().get();
            Integer count = counts.get(date);
            if (count == null) {
                count = 0;
            }
            counts.put(date, count + 1);

        }

        for (Map.Entry<Long, Integer> entry : counts.entrySet()) {
            collector.collect(word, new DateCount(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void configure(JobConf entries) {

    }
}
