package com.srichell.microservices.ratelimit.data.utils;

import com.codahale.metrics.Timer;
import com.srichell.microservices.ratelimit.app.main.RateLimitAppState;
import com.srichell.microservices.ratelimit.interfaces.IKeyValueDataCache;
import com.srichell.microservices.ratelimit.interfaces.IPersistentDelimitedDataStore;
import com.srichell.microservices.ratelimit.pojos.RoomInfo;
import com.srichell.microservices.ratelimit.spring.constants.RateLimitBeanNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class RateLimitDataLoader extends AbstractKeyValueDataLoader<String, RoomInfo>  {

    @Autowired
    @Qualifier(RateLimitBeanNames.RATE_LIMIT_APP_STATE)
    RateLimitAppState appState;

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitDataLoader.class);

    public RateLimitDataLoader(IPersistentDelimitedDataStore persistentDataStore, IKeyValueDataCache<String, RoomInfo> keyValueDataCache) {
        super(persistentDataStore, keyValueDataCache);
    }

    public RateLimitAppState getAppState() {
        return appState;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public Timer.Context getLoadTimerContext() {
        return getAppState().
                getAppMetricRegistry().
                getDataBaseDataLoadTime().
                time();
    }

    @Override
    public AbstractDelimitedDataParser<String, RoomInfo> getDataParser() {
        return new RateLimitDataParser();
    }
}
