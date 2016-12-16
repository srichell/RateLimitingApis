package com.srichell.microservices.ratelimit.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class RateLimitMetricRegistry extends AbstractMetricsRegistry {

    private Timer findHotelByCityIdQueryTime;
    private Meter findHotelThroughput;

    private Meter findHotelErrorRate;
    private Counter rateLimitViolations;

    private Counter http2xxErrors;
    private Counter http3xxErrors;
    private Counter http4xxErrors;
    private Counter http5xxErrors;


    private Timer dataBaseDataLoadTime;

    @Override
    protected String getGroupName() {
        return "RateLimit";
    }

    public RateLimitMetricRegistry() {
    }

    public RateLimitMetricRegistry(AbstractMetricsRegistry that) {
        super(that);
    }

    @Override
    public void registerMetrics() {
        super.registerMetrics();

        setFindHotelByCityIdQueryTime(
                getCodahaleMetricRegistry().timer(formatNameWithHost("timer", "findhotel.query.time"))
        ).
        setDataBaseDataLoadTime(
                getCodahaleMetricRegistry().timer(formatNameWithHost("timer", "db.load.time"))
        ).
        setFindHotelThroughput(
                getCodahaleMetricRegistry().meter(formatNameWithHost("meter", "findhotel.query.throughput"))
        ).
        setFindHotelErrorRate(
                getCodahaleMetricRegistry().meter(formatNameWithHost("meter", "findhotel.query.errorrate"))
        ).
        setRateLimitViolations(
                getCodahaleMetricRegistry().counter(formatNameWithHost("counter", "ratelimit.violations"))
        ).
        setHttp2xxErrors(
                getCodahaleMetricRegistry().counter(formatNameWithHost("counter", "http.errors.2xx"))
        ).
        setHttp3xxErrors(
                getCodahaleMetricRegistry().counter(formatNameWithHost("counter", "http.errors.3xx"))
        ).
        setHttp4xxErrors(
                getCodahaleMetricRegistry().counter(formatNameWithHost("counter", "http.errors.4xx"))
        ).
        setHttp5xxErrors(
                getCodahaleMetricRegistry().counter(formatNameWithHost("counter", "ratelimit.http.errors.5xx"))
        );

    }



    public Timer getFindHotelByCityIdQueryTime() {
        return findHotelByCityIdQueryTime;
    }



    public Counter getRateLimitViolations() {
        return rateLimitViolations;
    }

    public Counter getHttp2xxErrors() {
        return http2xxErrors;
    }

    public Counter getHttp3xxErrors() {
        return http3xxErrors;
    }

    public Counter getHttp4xxErrors() {
        return http4xxErrors;
    }

    public Counter getHttp5xxErrors() {
        return http5xxErrors;
    }

    public Timer getDataBaseDataLoadTime() {
        return dataBaseDataLoadTime;
    }

    public Meter getFindHotelThroughput() {
        return findHotelThroughput;
    }

    public Meter getFindHotelErrorRate() {
        return findHotelErrorRate;
    }

    public RateLimitMetricRegistry setFindHotelByCityIdQueryTime(Timer findHotelByCityIdQueryTime) {
        this.findHotelByCityIdQueryTime = findHotelByCityIdQueryTime;
        return this;
    }



    public RateLimitMetricRegistry setRateLimitViolations(Counter rateLimitViolations) {
        this.rateLimitViolations = rateLimitViolations;
        return this;
    }

    public RateLimitMetricRegistry setHttp2xxErrors(Counter http2xxErrors) {
        this.http2xxErrors = http2xxErrors;
        return this;
    }

    public RateLimitMetricRegistry setHttp3xxErrors(Counter http3xxErrors) {
        this.http3xxErrors = http3xxErrors;
        return this;
    }

    public RateLimitMetricRegistry setHttp4xxErrors(Counter http4xxErrors) {
        this.http4xxErrors = http4xxErrors;
        return this;
    }

    public RateLimitMetricRegistry setHttp5xxErrors(Counter http5xxErrors) {
        this.http5xxErrors = http5xxErrors;
        return this;
    }

    public RateLimitMetricRegistry setDataBaseDataLoadTime(Timer dataBaseDataLoadTime) {
        this.dataBaseDataLoadTime = dataBaseDataLoadTime;
        return this;
    }

    public RateLimitMetricRegistry setFindHotelThroughput(Meter findHotelThroughput) {
        this.findHotelThroughput = findHotelThroughput;
        return this;
    }

    public RateLimitMetricRegistry setFindHotelErrorRate(Meter findHotelErrorRate) {
        this.findHotelErrorRate = findHotelErrorRate;
        return this;
    }
}
