package com.bah.finance.wordsearch.util;

public class PropertyTypeException extends PropertyException {

    public PropertyTypeException(String key, Class<?> expectedType, Class<?> foundType) {
        super(String.format("Property key %s must have type %s, but type %s was found instead",
                key, expectedType.getCanonicalName(), foundType.getCanonicalName()));
    }

}
