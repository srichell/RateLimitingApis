package com.srichell.microservices.ratelimit.interfaces;

import com.srichell.microservices.ratelimit.pojos.ApiKey;

/**
 * Created by Sridhar Chellappa on 12/17/16.
 */
public interface RateLimitAlgorithm {
    public boolean isRateLimitViolated(ApiKey apiKey);
    public boolean penaliseApiKey(ApiKey apiKey);
}
