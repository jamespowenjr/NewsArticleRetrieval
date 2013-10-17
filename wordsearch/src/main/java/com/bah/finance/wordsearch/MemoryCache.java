package com.bah.finance.wordsearch;

import java.util.HashMap;
import java.util.Map;


public class MemoryCache<T> {

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
        this(loader, DEFAULT_MAX_SIZE_);
    }


    public MemoryCache(Loader<T, String> loader, int maxSize) {
        cache_ = new HashMap<String, T>();
        loader_ = loader;
        maxSize_ = maxSize;
    }


    private Map<String, T> cache_;
    private Loader<T, String> loader_;

    private final int maxSize_;
    private static final int DEFAULT_MAX_SIZE_ = 10000;


    // This class could be extended to use a random caching strategy but because of the access patterns
    // of this program, it will remove a random item any time it needs to free space.
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
