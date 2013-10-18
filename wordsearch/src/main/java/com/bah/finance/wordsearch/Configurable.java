package com.bah.finance.wordsearch;

import java.util.Properties;

public interface Configurable {
    public void configure(Properties props) throws PropertyException;
}
