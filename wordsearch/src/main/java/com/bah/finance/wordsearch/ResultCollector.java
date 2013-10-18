package com.bah.finance.wordsearch;

import com.bah.finance.wordsearch.util.Configurable;

public interface ResultCollector<T> extends Configurable {
    public void collect(T result);
    public void finalize();
}
