package com.bah.finance.wordsearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {

    public final static int MS_IN_DAY = 60 * 60 * 24 * 1000;

    public static int intFromDate(Date date) {
        return (int) (date.getTime() / MS_IN_DAY);
    }

    public static Date dateFromInt(int n) {
        return new Date(((long) n) * MS_IN_DAY);
    }

    public static double[] asDoubles(List<Integer> ints) {
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


    public static double[] asArray(List<Double> list) {
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
