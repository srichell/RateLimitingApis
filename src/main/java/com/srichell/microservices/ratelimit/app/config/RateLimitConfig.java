package com.srichell.microservices.ratelimit.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class RateLimitConfig {

    @JsonProperty
    private String blessedApiKey;

    @JsonProperty
    private String rateLimitAlgorithm;

    @JsonProperty
    private int rateViolationPenaltyMinutes;

    @JsonProperty
    private long requestsPerMinute;

    public String getBlessedApiKey() {
        return blessedApiKey;
    }

    public RateLimitConfig setBlessedApiKey(String blessedApiKey) {
        this.blessedApiKey = blessedApiKey;
        return this;
    }

    public String getRateLimitAlgorithm() {
        return rateLimitAlgorithm;
    }

    public RateLimitConfig setRateLimitAlgorithm(String rateLimitAlgorithm) {
        this.rateLimitAlgorithm = rateLimitAlgorithm;
        return this;
    }

    public Long getMaxRequestsPerMinute() {
        return requestsPerMinute;
    }

    public RateLimitConfig setRequestsPerMinute(int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
        return this;
    }

    public int getRateViolationPenaltyMinutes() {
        return rateViolationPenaltyMinutes;
    }

    public RateLimitConfig setRateViolationPenaltyMinutes(int rateViolationPenaltyMinutes) {
        this.rateViolationPenaltyMinutes = rateViolationPenaltyMinutes;
        return this;
    }
}
