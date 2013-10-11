package com.bah.finance.mapred.wordlist;

import com.bah.finance.mapred.ArticleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;

public class WordListMapper implements Mapper<Text, ArticleWritable, Text, IntWritable> {

    public static final IntWritable ONE_WRITABLE_ = new IntWritable(1);

    @Override
    public void map(Text filename, ArticleWritable article, OutputCollector<Text, IntWritable> collector, Reporter reporter) throws IOException {
        Text text = new Text();
        for (String word : article.getWords()) {
            text.set(word);
            collector.collect(text, ONE_WRITABLE_);
        }
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void configure(JobConf entries) {

    }
}
