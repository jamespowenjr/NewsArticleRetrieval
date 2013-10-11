package com.bah.finance.mapred;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ArticleWritable implements Writable {

    public static final char RECORD_SEPARATOR = 0x1e; // this is the ASCII record separator character


    public String getId() {
        return articleId_;
    }

    public Date getDate() {
        return articleDate_;
    }

    public List<String> getWords() {
        return articleWords_;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(articleDate_.getTime());
        StringBuilder builder = new StringBuilder();
        builder.append(articleId_).append(RECORD_SEPARATOR);
        for (String word : articleWords_) {
            builder.append(word).append(RECORD_SEPARATOR);
        }

        new Text(builder.toString()).write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        articleDate_ = new Date(dataInput.readLong());

        Text text = new Text();
        text.readFields(dataInput);
        String[] fields = text.toString().split(Character.toString(RECORD_SEPARATOR));
        if (fields.length < 3) {
            throw new IOException("Malformed article representation-- not enough fields");
        }

        int i = 0;
        articleId_ = fields[i++];
        articleWords_ = new ArrayList<String>(fields.length - i);
        for (int j = i ; j < fields.length ; ++j) {
            articleWords_.add(fields[j]);
        }
    }


    public ArticleWritable(String articleId, Date articleDate, List<String> articleWords) {
        articleId_ = articleId;
        articleDate_ = articleDate;
        articleWords_ = articleWords;
    }


    public ArticleWritable(DataInput input) throws IOException {
        readFields(input);
    }

    private String articleId_;
    private Date articleDate_;
    private List<String> articleWords_;
}
