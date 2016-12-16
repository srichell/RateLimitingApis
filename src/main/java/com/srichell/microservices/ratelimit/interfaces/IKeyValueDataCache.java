package com.srichell.microservices.ratelimit.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */

/*
 * Generic interface that functions as an In Memory Data Cache.
 */
public interface IKeyValueDataCache<KEY,VALUE> {
    public List<VALUE> get(KEY key);
    public boolean hasData();
    public IKeyValueDataCache<KEY,VALUE> loadData(Map<KEY, List<VALUE>> data);
    public void setLastReadSourceTimestamp(long timestamp);
}

