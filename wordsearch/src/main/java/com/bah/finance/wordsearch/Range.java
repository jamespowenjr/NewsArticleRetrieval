package com.bah.finance.wordsearch;

public class Range<T extends Comparable<T>> {

    public T getStart() {
        return start_;
    }

    public T getEnd() {
        return end_;
    }

    public Range(T start, T end) {
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException("Start of range must be less than or equal to end of range");
        }

        start_ = start;
        end_ = end;
    }

    private T start_;
    private T end_;

}
