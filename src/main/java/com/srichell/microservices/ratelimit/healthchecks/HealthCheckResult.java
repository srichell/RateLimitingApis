package com.srichell.microservices.ratelimit.healthchecks;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class HealthCheckResult {
    private boolean healthy;
    private String reasonForHealthCheckFailure;

    public HealthCheckResult(boolean healthy, String reasonForHealthCheckFailure) {
        this.healthy = healthy;
        this.reasonForHealthCheckFailure = reasonForHealthCheckFailure;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public String getReasonForHealthCheckFailure() {
        return reasonForHealthCheckFailure;
    }

    public HealthCheckResult setHealthy(boolean healthy) {
        this.healthy = healthy;
        return this;
    }

    public HealthCheckResult setReasonForHealthCheckFailure(String reasonForHealthCheckFailure) {
        this.reasonForHealthCheckFailure = reasonForHealthCheckFailure;
        return this;
    }
}

