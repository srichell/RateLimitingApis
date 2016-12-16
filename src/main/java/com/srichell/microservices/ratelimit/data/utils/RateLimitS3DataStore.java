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
public class RateLimitS3DataStore  extends AbstractPersistentDelimitedDataStoreS3Impl {
    @Autowired
    @Qualifier(RateLimitBeanNames.RATE_LIMIT_APP_STATE)
    RateLimitAppState appState;


    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitS3DataStore.class);


    private RateLimitAppState getAppState() {
        return appState;
    }



}
