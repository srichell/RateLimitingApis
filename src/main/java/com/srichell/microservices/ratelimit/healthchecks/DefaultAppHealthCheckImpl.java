package com.srichell.microservices.ratelimit.healthchecks;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class DefaultAppHealthCheckImpl extends AbstractAppHealthCheck {
    @Override
    protected HealthCheckResult checkHealth() {
        return new HealthCheckResult(true, "PASS");
    }

    @Override
    public String getName() {
        return "DefaultAppHealthChecker";
    }
}