package com.bah.finance.mapred.wordseries;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DateCount implements Writable {

    public long getDate() {
        return date_;
    }

    public int getCount() {
        return count_;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeLong(date_);
        output.writeInt(count_);
    }

    @Override
    public void readFields(DataInput input) throws IOException {
        date_ = input.readLong();
        count_ = input.readInt();
    }

    public DateCount(long date) {
        this(date, 0);
    }

    public DateCount(long date, int count) {
        date_ = date;
        count_ = count;
    }


    public DateCount(DataInput input) throws IOException {
        readFields(input);
    }

    private long date_;
    private int count_;
}
