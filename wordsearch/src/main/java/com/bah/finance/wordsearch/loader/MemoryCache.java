package com.bah.finance.wordsearch.loader;

import com.bah.finance.wordsearch.util.Configurable;
import com.bah.finance.wordsearch.util.PropertyException;
import com.bah.finance.wordsearch.util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class MemoryCache<T> implements Configurable {

    @Override
    public void configure(Properties props) throws PropertyException {
        maxSize_ = Utils.getConfigInt(props, MAX_SIZE_KEY_, DEFAULT_MAX_SIZE_);
        loader_.configure(props);
    }

    public T get(String name) {
        synchronized (this) {
            T item = cache_.get(name);
            if (item != null) {
                return item;
            }

            load_(name);
            return cache_.get(name);
        }
    }


    public MemoryCache(Loader<T, String> loader) {
        cache_ = new HashMap<String, T>();
        loader_ = loader;
    }


    private final Map<String, T> cache_;
    private final Loader<T, String> loader_;

    private int maxSize_;
    private final static String MAX_SIZE_KEY_ = "cache.max_size";
    private final static int DEFAULT_MAX_SIZE_ = 10000;


    // This class could be extended to use a random caching strategy but because of the access patterns
    // of this program, it will remove an arbitrary item any time it needs to free space.
    protected void remove_() {
        cache_.remove(cache_.keySet().iterator().next());
    }


    private void load_(String name) {
        T item = loader_.load(name);
        if (item == null) {
            return;
        }

        if (cache_.size() >= maxSize_) {
            remove_();
        }

        cache_.put(name, item);
    }
}
