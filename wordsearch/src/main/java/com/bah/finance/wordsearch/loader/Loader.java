package com.bah.finance.wordsearch.loader;

public interface Loader<T, Q> {
    public T load(Q query);
}
