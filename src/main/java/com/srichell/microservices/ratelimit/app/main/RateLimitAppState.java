package com.srichell.microservices.ratelimit.app.main;

import com.srichell.microservices.ratelimit.app.config.RateLimitAppConfig;
import com.srichell.microservices.ratelimit.interfaces.AbstractAppState;
import com.srichell.microservices.ratelimit.metrics.RateLimitMetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class RateLimitAppState extends AbstractAppState {
    @Autowired
    private RateLimitAppConfig appConfig;


    public RateLimitAppConfig getAppConfig() {
        return appConfig;
    }


    @Override
    public void init() throws InterruptedException {
        super.init();
        getAppMetricRegistry().registerMetrics();
    }

    public void setAppConfiguration(RateLimitAppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public RateLimitMetricRegistry getAppMetricRegistry() {
        return (RateLimitMetricRegistry) getMetricRegistry();
    }
}
