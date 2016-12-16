package com.srichell.microservices.ratelimit.interfaces;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public interface IAppState {
    IMetricsRegistry getMetricRegistry();
    IAppConfig getAppConfig();
    /**
     * Hook for any initialization of this App state and its dependancies after creation. Use this
     * for post creation initializations like stateful Rest resources (data preLoad), threadpool
     * initialization, etc etc.
     */
    void init() throws InterruptedException;
}
