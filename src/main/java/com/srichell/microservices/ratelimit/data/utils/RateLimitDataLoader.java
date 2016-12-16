package com.srichell.microservices.ratelimit.data.utils;

import com.codahale.metrics.Timer;
import com.srichell.microservices.ratelimit.interfaces.IKeyValueDataCache;
import com.srichell.microservices.ratelimit.interfaces.IPersistentDelimitedDataStore;
import com.srichell.microservices.ratelimit.pojos.CityId;
import com.srichell.microservices.ratelimit.pojos.RoomInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class RateLimitDataLoader extends AbstractKeyValueDataLoader<CityId, RoomInfo>  {
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitDataLoader.class);

    public RateLimitDataLoader(IPersistentDelimitedDataStore persistentDataStore, IKeyValueDataCache<CityId, RoomInfo> keyValueDataCache) {
        super(persistentDataStore, keyValueDataCache);
    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public Timer.Context getLoadTimerContext() {
        return null;
    }

    @Override
    public AbstractDelimitedDataParser<CityId, RoomInfo> getDataParser() {
        return null;
    }
}
