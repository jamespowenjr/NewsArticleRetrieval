package com.bah.finance.mapred.wordlist;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.Tool;

public class WordListJob implements Tool {
    @Override
    public int run(String[] strings) throws Exception {
        JobConf job = new JobConf(getConf(), WordListJob.class);
        job.setJobName("word_list");
        job.setMapperClass(WordListMapper.class);
        job.setCombinerClass(WordListCombiner.class);
        job.setReducerClass(WordListReducer.class);

        JobClient.runJob(job);
        return 0;
    }

    @Override
    public void setConf(Configuration config) {
        config_ = config;
    }

    @Override
    public Configuration getConf() {
        return config_;
    }

    Configuration config_;
}
