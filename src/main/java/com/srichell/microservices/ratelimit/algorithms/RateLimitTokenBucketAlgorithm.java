package com.srichell.microservices.ratelimit.algorithms;

import com.srichell.microservices.ratelimit.app.config.RateLimitConfig;
import com.srichell.microservices.ratelimit.app.main.RateLimitAppState;
import com.srichell.microservices.ratelimit.interfaces.RateLimitAlgorithm;
import com.srichell.microservices.ratelimit.pojos.ApiKey;

/**
 * Created by Sridhar Chellappa on 12/17/16.
 */
public class RateLimitTokenBucketAlgorithm implements RateLimitAlgorithm {

    private RateLimitAppState rateLimitAppState;

    public RateLimitTokenBucketAlgorithm() {
    }


    @Override
    public boolean isRateLimitViolated(ApiKey apiKey, RateLimitConfig rateLimitConfig) {
        return false;
    }

    @Override
    public boolean penaliseApiKey(ApiKey apiKey) {
        return false;
    }

    @Override
    public RateLimitAlgorithm setAppState(RateLimitAppState appState) {
        this.rateLimitAppState = appState;
        return this;
    }

}
