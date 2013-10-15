package com.bah.finance.wordsearch;

public interface Loader<T, Q> {
    public T load(Q query);
}
