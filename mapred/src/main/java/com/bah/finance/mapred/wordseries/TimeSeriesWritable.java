package com.bah.finance.mapred.wordseries;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;

public class TimeSeriesWritable implements Writable {

    public void addValue(long date, int value) {
        values_.put(date, value);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(values_.size());
        for (Map.Entry<Long, Integer> entry : values_.entrySet()) {
            dataOutput.writeLong(entry.getKey());
            dataOutput.writeInt(entry.getValue());
        }
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        int size = dataInput.readInt();
        values_.clear();

        for (int i = 0 ; i < size ; ++i) {
            long date = dataInput.readLong();
            int value = dataInput.readInt();
            values_.put(date, value);
        }
    }


    public TimeSeriesWritable() {
        values_ = new TreeMap<Long, Integer>();
    }


    public TimeSeriesWritable(Map<Long, Integer> values) {
        this();
        values_.putAll(values);
    }


    private SortedMap<Long, Integer> values_;
}
