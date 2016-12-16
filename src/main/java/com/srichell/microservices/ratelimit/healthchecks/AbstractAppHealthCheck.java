package com.srichell.microservices.ratelimit.healthchecks;

import com.codahale.metrics.health.HealthCheck;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public abstract class AbstractAppHealthCheck extends HealthCheck {
    protected abstract HealthCheckResult checkHealth();

    public abstract String getName();


    @Override
    protected HealthCheck.Result check() throws Exception {
        HealthCheckResult result = checkHealth();
        return result.isHealthy() ? HealthCheck.Result.healthy() : HealthCheck.Result.unhealthy(getName() + " : " + result.getReasonForHealthCheckFailure());
    }
}
