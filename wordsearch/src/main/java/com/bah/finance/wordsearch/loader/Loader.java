package com.bah.finance.wordsearch.loader;

import com.bah.finance.wordsearch.util.Configurable;

public interface Loader<T, Q> extends Configurable {
    public T load(Q query);
}
