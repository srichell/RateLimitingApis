package com.srichell.microservices.ratelimit.algorithms;

import com.srichell.microservices.ratelimit.app.main.RateLimitAppState;
import com.srichell.microservices.ratelimit.data.utils.RateLimitKeyValueCache;
import com.srichell.microservices.ratelimit.interfaces.RateLimitAlgorithm;
import com.srichell.microservices.ratelimit.pojos.ApiKey;
import com.srichell.microservices.ratelimit.spring.constants.RateLimitBeanNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Created by Sridhar Chellappa on 12/17/16.
 */
public class RateLimitTokenBucketAlgorithm implements RateLimitAlgorithm {

    private RateLimitAppState rateLimitAppState;

    public RateLimitTokenBucketAlgorithm() {
    }


    @Override
    public boolean isRateLimitViolated(ApiKey apiKey) {
        return false;
    }

    @Override
    public boolean penaliseApiKey(ApiKey apiKey) {
        return false;
    }
}
