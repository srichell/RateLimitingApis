package com.srichell.microservices.ratelimit.algorithms;

import com.srichell.microservices.ratelimit.app.config.RateLimitConfig;
import com.srichell.microservices.ratelimit.app.main.RateLimitAppState;
import com.srichell.microservices.ratelimit.interfaces.RateLimitAlgorithm;
import com.srichell.microservices.ratelimit.pojos.ApiKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private Map<ApiKey, Long> blackListedApiKeys = new HashMap<ApiKey, Long>();
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitTokenBucketAlgorithm.class);


    public static Logger getLOGGER() {
        return LOGGER;
    }

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

    public Map<ApiKey, Long> getBlackListedApiKeys() {
        return blackListedApiKeys;
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
         * Users do not get extra credit for unused past. The best they get
         * for unused credit is max number of requests per minute.
         */
        long requestsRemaining = Math.min(
                rateLimitConfig.getMaxRequestsPerMinute(),
                getCreditBalanceForUnusedPast(newCreditBalance, rateLimitConfig)
        );

        if(requestsRemaining == 0) {
            /*
             * Api Key needs to be blacklisted. Add it to the Blacklist
             */
            getLOGGER().error("Blacklisting apiKey {} for {} minutes", apiKey, rateLimitConfig.getRateViolationPenaltyMinutes());
            getBlackListedApiKeys().put(apiKey, System.currentTimeMillis());
            return true;
        }

        if((getBlackListedApiKeys().get(apiKey) != null) &&
            ((diffInSeconds(System.currentTimeMillis(), getBlackListedApiKeys().get(apiKey))/NUM_SECONDS_PER_MINUTE) < rateLimitConfig.getRateViolationPenaltyMinutes())) {
            /*
             * This blacklisted ApiKey again violated the rate limit. Renew its blacklist
             */
            getLOGGER().error("Renewing Blacklisted apiKey {} for another {} minutes", apiKey, rateLimitConfig.getRateViolationPenaltyMinutes());
            getBlackListedApiKeys().put(apiKey, System.currentTimeMillis());
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
        removeIfBlacklisted(apiKey);
    }

    private void removeIfBlacklisted(ApiKey apiKey) {
        if(getBlackListedApiKeys().get(apiKey) != null) {
            getBlackListedApiKeys().remove(apiKey);
        }
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
