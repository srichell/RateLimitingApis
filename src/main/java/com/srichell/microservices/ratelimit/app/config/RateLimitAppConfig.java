package com.srichell.microservices.ratelimit.app.config;

import com.srichell.microservices.ratelimit.interfaces.IAppConfig;
import com.srichell.microservices.ratelimit.multithreading.ThreadPoolConfig;

import java.util.List;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class RateLimitAppConfig implements IAppConfig {
    @Override
    public List<String> getSpringProfileNames() {
        return null;
    }

    @Override
    public List<String> getSpringProfileClasses() {
        return null;
    }

    @Override
    public boolean isThreadPoolEnabled() {
        return false;
    }

    @Override
    public List<ThreadPoolConfig> getThreadPoolConfigs() {
        return null;
    }

    @Override
    public String getAppConfigClass() {
        return null;
    }
}
