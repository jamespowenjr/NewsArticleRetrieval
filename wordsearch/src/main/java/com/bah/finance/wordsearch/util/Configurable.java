package com.bah.finance.wordsearch.util;

import com.bah.finance.wordsearch.util.PropertyException;

import java.util.Properties;

public interface Configurable {
    public void configure(Properties props) throws PropertyException;
}
