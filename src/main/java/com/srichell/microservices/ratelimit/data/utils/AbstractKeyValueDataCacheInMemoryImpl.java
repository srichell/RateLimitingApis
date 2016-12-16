package com.srichell.microservices.ratelimit.data.utils;

import com.srichell.microservices.ratelimit.interfaces.IKeyValueDataCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class AbstractKeyValueDataCacheInMemoryImpl<KEY,VALUE> implements IKeyValueDataCache<KEY, VALUE> {
    /*
     * This NOTE is pretty Long and deep Rooted, so please pay attention.
     * While we can implement locks using synchronized we lose out
     * on performance. we take advantage of the
     * fact that there is only one writer and multiple readers for which volatile
     * works very well.
     */
    private volatile Map<KEY, List<VALUE>> keyValueCache = new HashMap<KEY,List<VALUE>>();
    private volatile long lastReadSourceTimestamp = 0;

    protected Map<KEY, List<VALUE>> getKeyValueCache() {
        return keyValueCache;
    }

    @Override
    public void setLastReadSourceTimestamp(long lastReadSourceTimestamp) {
        this.lastReadSourceTimestamp = lastReadSourceTimestamp;
    }

    public long getLastReadSourceTimestamp() { return lastReadSourceTimestamp; }

    @Override
    public AbstractKeyValueDataCacheInMemoryImpl loadData(Map<KEY, List<VALUE>> data) {
        this.keyValueCache = data;
        return this;
    }

    @Override
    public List<VALUE> get(KEY key) {
        return getKeyValueCache().get(key);
    }

    @Override
    public boolean hasData() {
        return (getKeyValueCache().size() > 0);
    }
}
