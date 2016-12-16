package com.srichell.microservices.ratelimit.app.main;

import com.srichell.microservices.ratelimit.app.config.RateLimitAppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * Created by Sridhar Chellappa on 12/17/16.
 */
public class RateLimitApp extends AbstractApp<RateLimitAppConfig> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitApp.class);

    @Override
    public void postBootInitialization() {

    }

    /*
     * Hook to register any custom SPRING Beans before boot process begins
     */
    @Override
    public DefaultListableBeanFactory registerCustomBeans(DefaultListableBeanFactory beanFactory, RateLimitAppConfig appConfig) {
        return beanFactory;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    public static void main(String[] args) throws Exception {
        new RateLimitApp().boot(args);
    }
}
