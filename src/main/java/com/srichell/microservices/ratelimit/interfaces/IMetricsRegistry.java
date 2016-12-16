package com.srichell.microservices.ratelimit.interfaces;

import com.codahale.metrics.MetricRegistry;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public interface IMetricsRegistry {
    public void registerMetrics();
    public MetricRegistry getCodahaleMetricRegistry();
}
