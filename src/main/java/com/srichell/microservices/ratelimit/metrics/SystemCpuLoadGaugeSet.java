package com.srichell.microservices.ratelimit.metrics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;
import com.sun.management.UnixOperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class SystemCpuLoadGaugeSet implements MetricSet {
    UnixOperatingSystemMXBean systemMXBean;

    public SystemCpuLoadGaugeSet() {
        this.systemMXBean = (UnixOperatingSystemMXBean)
                ManagementFactory.getOperatingSystemMXBean();
    }

    @Override
    public Map<String, Metric> getMetrics() {
        final Map<String, Metric> gauges = new HashMap<String, Metric>();

        gauges.put("cpu.load", new Gauge<Double>() {
            @Override
            public Double getValue() {
                return systemMXBean.getProcessCpuLoad();
            }
        });

        gauges.put("cpu.time", new Gauge<Long>() {
            @Override
            public Long getValue() {
                return systemMXBean.getProcessCpuTime();
            }
        });

        return Collections.unmodifiableMap(gauges);
    }
}
