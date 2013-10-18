package com.bah.finance.wordsearch;

public class MissingPropertyException extends PropertyException {

    public MissingPropertyException(String key) {
        super(String.format("Missing required property key %s", key));
    }

}
