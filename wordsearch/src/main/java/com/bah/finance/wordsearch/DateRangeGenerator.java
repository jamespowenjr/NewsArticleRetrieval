package com.bah.finance.wordsearch;

import com.bah.finance.wordsearch.util.Configurable;
import com.bah.finance.wordsearch.util.Range;

public interface DateRangeGenerator extends Configurable {
    public Range<Integer> getDateRange();
}
