package com.srichell.microservices.ratelimit.interfaces;

import com.srichell.microservices.ratelimit.app.config.RateLimitConfig;
import com.srichell.microservices.ratelimit.app.main.RateLimitAppState;
import com.srichell.microservices.ratelimit.pojos.ApiKey;

/**
 * Created by Sridhar Chellappa on 12/17/16.
 */
public interface RateLimitAlgorithm {
    public boolean isRateLimitViolated(ApiKey apiKey, RateLimitConfig rateLimitConfig);
    public boolean penaliseApiKey(ApiKey apiKey);
    public RateLimitAlgorithm setAppState(RateLimitAppState appState);
    public void resetCreditBalance(ApiKey apiKey, Long numRequestsPerMinute);
}
