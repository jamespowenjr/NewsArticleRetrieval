package com.bah.finance.mapred.wordseries;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;

public class ArticleMapper implements Mapper<Text, ArticleWritable, Text, LongWritable> {


    @Override
    public void map(Text articleId, ArticleWritable article, OutputCollector<Text, LongWritable> collector, Reporter reporter) throws IOException {
        long timeValue = article.getDate().getTime();
        for (String word : article.getWords()) {
            collector.collect(new Text(word), new LongWritable(timeValue));
        }
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void configure(JobConf entries) {

    }
}
