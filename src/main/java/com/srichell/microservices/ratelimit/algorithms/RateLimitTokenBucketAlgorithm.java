package com.srichell.microservices.ratelimit.algorithms;

import com.srichell.microservices.ratelimit.app.config.RateLimitConfig;
import com.srichell.microservices.ratelimit.app.main.RateLimitAppState;
import com.srichell.microservices.ratelimit.interfaces.RateLimitAlgorithm;
import com.srichell.microservices.ratelimit.pojos.ApiKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sridhar Chellappa on 12/17/16.
 */
public class RateLimitTokenBucketAlgorithm implements RateLimitAlgorithm {

    private static final int NUM_SECONDS_PER_MINUTE = 60;
    private static final int NUM_MILLI_SECONDS_PER_SECOND = 1000;
    private RateLimitAppState rateLimitAppState;
    private Map<ApiKey, RateLimitCreditBalance> existingRateLimits = new HashMap<ApiKey, RateLimitCreditBalance>();

    public RateLimitTokenBucketAlgorithm() {
    }


    private long diffInSeconds(long timeStampinMillis1, long timeStampinMillis2) {
        return (Math.abs(timeStampinMillis1 - timeStampinMillis2))/NUM_MILLI_SECONDS_PER_SECOND;
    }

    private long getCreditBalanceForUnusedPast(RateLimitCreditBalance creditBalance, RateLimitConfig rateLimitConfig) {
        /*
         * How to interpret this? The credit balance could have moved forward by this many requests per minute (num minutes calculated by time diff)
         */
        return (creditBalance.getNumRequestsPerMinute() + rateLimitConfig.getMaxRequestsPerMinute() * diffInSeconds(System.currentTimeMillis(), creditBalance.getLastReceivedTimeStamp()) / NUM_SECONDS_PER_MINUTE
        );
    }

    @Override
    public boolean isRateLimitViolated(ApiKey apiKey, RateLimitConfig rateLimitConfig) {

        /*
         * Get the current balance remaining. If not present, put in a new balance from
         * the Configuration
         */
        RateLimitCreditBalance newCreditBalance = (getExistingRateLimits().get(apiKey) == null) ?
                new RateLimitCreditBalance(rateLimitConfig): new RateLimitCreditBalance(getExistingRateLimits().get(apiKey));


        /*
         * We calculate the num requests remaining based on the past history.
         * Users do not get extra credit for unused past. The best they get is max
         * number of requests per minute.
         */
        long requestsRemaining = Math.min(
                rateLimitConfig.getMaxRequestsPerMinute(),
                getCreditBalanceForUnusedPast(newCreditBalance, rateLimitConfig)
        );

        if(requestsRemaining == 0) {
            return true;
        }
        newCreditBalance.setNumRequestsPerMinute(requestsRemaining - 1).
                          setLastReceivedTimeStamp(System.currentTimeMillis());
        getExistingRateLimits().put(apiKey, newCreditBalance);

        return false;
    }

    @Override
    public boolean penaliseApiKey(ApiKey apiKey) {
        return false;
    }

    private RateLimitAppState getRateLimitAppState() {
        return rateLimitAppState;
    }

    private Map<ApiKey, RateLimitCreditBalance> getExistingRateLimits() {
        return existingRateLimits;
    }

    @Override
    public void resetCreditBalance(ApiKey apiKey, Long numRequestsPerMinute) {
        RateLimitCreditBalance newCreditBalance = new RateLimitCreditBalance(numRequestsPerMinute, System.currentTimeMillis());
        getExistingRateLimits().put(apiKey, newCreditBalance);
    }

    @Override
    public RateLimitAlgorithm setAppState(RateLimitAppState appState) {
        this.rateLimitAppState = appState;
        return this;
    }










    private static class RateLimitCreditBalance {
        private Long numRequestsPerMinute;
        private Long lastReceivedTimeStamp;

        public RateLimitCreditBalance() {
        }

        public RateLimitCreditBalance(Long numRequestsPerMinute, Long lastReceivedTimeStamp) {
            this.numRequestsPerMinute = numRequestsPerMinute;
            this.lastReceivedTimeStamp = lastReceivedTimeStamp;
        }

        public RateLimitCreditBalance(RateLimitCreditBalance that) {
            this.setNumRequestsPerMinute(that.getNumRequestsPerMinute()).
                 setLastReceivedTimeStamp(that.getLastReceivedTimeStamp());
        }

        public RateLimitCreditBalance(RateLimitConfig rateLimitConfig) {
            this.setNumRequestsPerMinute(rateLimitConfig.getMaxRequestsPerMinute()).setLastReceivedTimeStamp(System.currentTimeMillis());
        }

        public Long getNumRequestsPerMinute() {
            return numRequestsPerMinute;
        }

        public RateLimitCreditBalance setNumRequestsPerMinute(Long numRequestsPerMinute) {
            this.numRequestsPerMinute = numRequestsPerMinute;
            return this;
        }

        public Long getLastReceivedTimeStamp() {
            return lastReceivedTimeStamp;
        }

        public RateLimitCreditBalance setLastReceivedTimeStamp(Long lastReceivedTimeStamp) {
            this.lastReceivedTimeStamp = lastReceivedTimeStamp;
            return this;
        }
    }

}
