package com.bah.finance.mapred.wordseries;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.Tool;

public class WordTimeSeriesJob implements Tool {

    @Override
    public int run(String[] strings) throws Exception {
        JobConf job = new JobConf(getConf(), WordTimeSeriesJob.class);
        job.setJobName("word_time_series");
        job.setMapperClass(ArticleMapper.class);
        job.setCombinerClass(ArticleCombiner.class);
        job.setReducerClass(ArticleReducer.class);

        JobClient.runJob(job);
        return 0;
    }

    @Override
    public void setConf(Configuration conf) {
        conf_ = conf;
    }

    @Override
    public Configuration getConf() {
        return conf_;
    }

    Configuration conf_;
}
