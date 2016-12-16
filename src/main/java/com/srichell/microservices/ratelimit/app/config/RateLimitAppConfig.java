package com.srichell.microservices.ratelimit.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srichell.microservices.ratelimit.interfaces.IAppConfig;
import com.srichell.microservices.ratelimit.multithreading.ThreadPoolConfig;

import java.util.List;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class RateLimitAppConfig extends AbstractAppConfig {

    @JsonProperty
    private String rateLimitAlgorithm;

    public RateLimitAppConfig(RateLimitAppConfig that) {
        super(that);
        this.setRateLimitAlgorithm(that.getRateLimitAlgorithm());
    }

    public RateLimitAppConfig() {
    }

    public String getRateLimitAlgorithm() {
        return rateLimitAlgorithm;
    }

    public RateLimitAppConfig setRateLimitAlgorithm(String rateLimitAlgorithm) {
        this.rateLimitAlgorithm = rateLimitAlgorithm;
        return this;
    }
}
