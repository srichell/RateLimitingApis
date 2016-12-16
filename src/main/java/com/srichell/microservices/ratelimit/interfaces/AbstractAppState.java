package com.srichell.microservices.ratelimit.interfaces;

import com.srichell.microservices.ratelimit.multithreading.ThreadPoolManager;
import com.srichell.microservices.ratelimit.spring.config.constants.CommonBeanNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class AbstractAppState implements IAppState {

    @Autowired
    @Qualifier(CommonBeanNames.APP_METRIC_REGISTRY)
    private IMetricsRegistry appMetricRegistry;

    @Autowired
    @Qualifier(CommonBeanNames.APP_CONFIG)
    private IAppConfig appConfig;

    private ThreadPoolManager threadPoolManager;


    public ThreadPoolManager getThreadPoolManager() {
        return threadPoolManager;
    }

    @Override
    public IMetricsRegistry getMetricRegistry() {
        return appMetricRegistry;
    }

    @Override
    public IAppConfig getAppConfig() {
        return appConfig;
    }

    private AbstractAppState setThreadPoolManager(ThreadPoolManager threadPoolManager) {
        this.threadPoolManager = threadPoolManager;
        return this;
    }

    @Override
    public void init() throws InterruptedException {
        if(getAppConfig().isThreadPoolEnabled()) {
            setThreadPoolManager(
                    new ThreadPoolManager(getAppConfig().getThreadPoolConfigs())
            );
        }
    }

}
