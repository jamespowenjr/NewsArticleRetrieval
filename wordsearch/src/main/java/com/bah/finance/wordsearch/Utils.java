package com.bah.finance.wordsearch;

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
}
