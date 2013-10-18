package com.bah.finance.wordsearch.util;

import java.util.*;

public class Utils {

    public static double[] asDoubles(Collection<Integer> ints) {
        double[] doubles = new double[ints.size()];
        int index = 0;
        for (Integer i : ints) {
            if (i == null) {
                throw new IllegalArgumentException("List cannot contain null values");
            }
            doubles[index++] = i;
        }
        return doubles;
    }


    public static double[] asArray(Collection<Double> list) {
        double[] doubles = new double[list.size()];
        int index = 0;
        for (Double d : list) {
            if (d == null) {
                throw new IllegalArgumentException("List cannot contain null values");
            }
            doubles[index++] = d;
        }
        return doubles;
    }


    public static double sum(double[] array) {
        double sum = 0;
        for (double d : array) {
            sum += d;
        }
        return sum;
    }


    public static <T> T getConfigValue(Properties props, String key, Class<? extends T> clazz) throws PropertyException {
        return getConfigValue(props, key, clazz, null);
    }


    // If defaultValue is null, then the field is considered to be required
    public static <T> T getConfigValue(Properties props, String key, Class<? extends T> clazz, T defaultValue) throws PropertyException {
        Object value = props.get(key);
        if (value == null) {
            if (defaultValue == null) {
                throw new MissingPropertyException(key);
            } else {
                return defaultValue;
            }
        } else {
            if (clazz.isInstance(value)) {
                return (T) value;
            } else {
                throw new PropertyTypeException(key, clazz, value.getClass());
            }
        }
    }

    private interface Parser<T> {
        public T parse(String s) throws IllegalArgumentException;
    }


    public static <T> T getConfigValueParsed(Properties props, String key, Class<? extends T> clazz,
                                             Parser<? extends T> parser) throws PropertyException {
        return getConfigValueParsed(props, key, clazz, parser, null);
    }


    public static <T> T getConfigValueParsed(Properties props, String key, Class<? extends T> clazz,
                                             Parser<? extends T> parser, T defaultValue) throws PropertyException {
        String valueString = getConfigValue(props, key, String.class, defaultValue == null ? null : defaultValue.toString());
        if (valueString == null) {
            return null;
        }
        try {
            return parser.parse(valueString);
        } catch (IllegalArgumentException e) {
            throw new PropertyTypeException(key, clazz, String.class);
        }
    }


    public static Integer getConfigInt(Properties props, String key) throws PropertyException {
        return getConfigInt(props, key, null);
    }


    public static Integer getConfigInt(Properties props, String key, Integer defaultValue) throws PropertyException {
        return getConfigValueParsed(props, key, Integer.class, new Parser<Integer>() {
            @Override
            public Integer parse(String s) throws IllegalArgumentException {
                return Integer.parseInt(s);
            }
        }, defaultValue);
    }


    public static Double getConfigDouble(Properties props, String key) throws PropertyException {
        return getConfigDouble(props, key, null);
    }


    public static Double getConfigDouble(Properties props, String key, Double defaultValue) throws PropertyException {
        return getConfigValueParsed(props, key, Double.class, new Parser<Double>() {
            @Override
            public Double parse(String s) throws IllegalArgumentException {
                return Double.parseDouble(s);
            }
        }, defaultValue);
    }


}
