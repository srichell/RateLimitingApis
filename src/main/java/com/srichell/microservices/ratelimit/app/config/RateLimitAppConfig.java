package com.srichell.microservices.ratelimit.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srichell.microservices.ratelimit.interfaces.IAppConfig;
import com.srichell.microservices.ratelimit.multithreading.ThreadPoolConfig;

import java.util.List;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class RateLimitAppConfig extends AbstractAppConfig {

    /*
     * Ideally, this (set of Blessed API Keys and their rate Limit configs) must be gotten from an external service
     * But for the sake of this problem, I am injecting this VIA a config file
     */
    @JsonProperty
    private String rateLimitAlgorithm;

    /*
     * Ideally, this (set of Blessed API Keys and their rate Limit configs) must be gotten from an external service
     * But for the sake of this problem, I am injecting this VIA a config file
     */
    @JsonProperty
    private List<RateLimitConfig> rateLimitConfigs;

    public RateLimitAppConfig(RateLimitAppConfig that) {
        super(that);
        this.setRateLimitConfigs(that.getRateLimitConfigs()).
             setRateLimitAlgorithm(that.getRateLimitAlgorithm());
    }

    public RateLimitAppConfig() {
    }

    public List<RateLimitConfig> getRateLimitConfigs() {
        return rateLimitConfigs;
    }

    public RateLimitAppConfig setRateLimitConfigs(List<RateLimitConfig> rateLimitConfigs) {
        this.rateLimitConfigs = rateLimitConfigs;
        return this;
    }

    public String getRateLimitAlgorithm() {
        return rateLimitAlgorithm;
    }

    public RateLimitAppConfig setRateLimitAlgorithm(String rateLimitAlgorithm) {
        this.rateLimitAlgorithm = rateLimitAlgorithm;
        return this;
    }
}
