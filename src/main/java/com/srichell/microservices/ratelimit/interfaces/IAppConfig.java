package com.srichell.microservices.ratelimit.interfaces;

import com.srichell.microservices.ratelimit.multithreading.ThreadPoolConfig;

import java.util.List;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */

// A Generic App configuration. Any App that gets built, must provide an implementation of this class
public interface IAppConfig {
        public List<String> getSpringProfileNames();
        public List<String> getSpringProfileClasses();
        public boolean isThreadPoolEnabled();
        public List<ThreadPoolConfig> getThreadPoolConfigs();
        public String getAppConfigClass();
}
