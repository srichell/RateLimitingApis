package com.srichell.microservices.ratelimit.data.utils;

import com.srichell.microservices.ratelimit.app.main.RateLimitAppState;
import com.srichell.microservices.ratelimit.spring.constants.RateLimitBeanNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class RateLimitLocalFSDataStore extends AbstractPersistentDataStoreLocalFSImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitLocalFSDataStore.class);

    @Autowired
    @Qualifier(RateLimitBeanNames.RATE_LIMIT_APP_STATE)
    RateLimitAppState appState;

    @Override
    protected Logger getLogger() {
        return LOGGER;
    }

    @Override
    protected String getLocalDir() {
        return getDataConfig().getLocalConfig().getLocalDir();
    }

    @Override
    protected String getLocalFileName() {
        return getDataConfig().getLocalConfig().getLocalFileName();
    }

    private RateLimitAppState getAppState() {
        return appState;
    }
}
