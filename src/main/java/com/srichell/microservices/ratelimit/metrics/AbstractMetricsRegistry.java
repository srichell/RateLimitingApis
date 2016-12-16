package com.srichell.microservices.ratelimit.metrics;

import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.*;
import com.srichell.microservices.ratelimit.interfaces.IMetricsRegistry;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public abstract class AbstractMetricsRegistry implements IMetricsRegistry {

    private MetricRegistry codahaleMetricRegistry;

    public AbstractMetricsRegistry(AbstractMetricsRegistry that) {
        this.setCodahaleMetricRegistry(that.getCodahaleMetricRegistry());
    }

    private String getHostName() {
        String hostName = null;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostName = " ";
        } finally {
            return hostName;
        }
    }

    public AbstractMetricsRegistry() {
    }

    public AbstractMetricsRegistry setCodahaleMetricRegistry(MetricRegistry codahaleMetricRegistry) {
        this.codahaleMetricRegistry = codahaleMetricRegistry;
        return this;
    }

    protected String formatNameWithHost(String metricType, String suffix) {
        return getGroupName() + "." + getHostName() + "." + metricType + "." + suffix;
    }

    protected String formatName(String metricType, String suffix) {
        return getGroupName() + "." + metricType + "." + suffix;
    }


    protected abstract String getGroupName();

    @Override
    public void registerMetrics() {
        //Cpu load metrics
        getCodahaleMetricRegistry().register(
                formatNameWithHost("gaugeset", "system.cpu"),new SystemCpuLoadGaugeSet()
        );
        getCodahaleMetricRegistry().register(
                formatNameWithHost( "gaugeset", "jvm.gc"), new GarbageCollectorMetricSet()
        );
        getCodahaleMetricRegistry().register(
                formatNameWithHost("gaugeset", "jvm.buffers"),
                new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer())
        );
        getCodahaleMetricRegistry().register(
                formatNameWithHost("gaugeset","jvm.memory"), new MemoryUsageGaugeSet()
        );
        getCodahaleMetricRegistry().register(
                formatNameWithHost("gauges", "jvm.threads"), new ThreadStatesGaugeSet()
        );

        getCodahaleMetricRegistry().register(
                formatNameWithHost("gauges", "jvm.attribute"), new JvmAttributeGaugeSet()
        );

        getCodahaleMetricRegistry().register(
                formatNameWithHost("gauges", "jvm.classloader"), new ClassLoadingGaugeSet()
        );

        getCodahaleMetricRegistry().register(
                formatNameWithHost("gauges", "jvm.filedescriptor"), new FileDescriptorRatioGauge()
        );

    }

    @Override
    public MetricRegistry getCodahaleMetricRegistry() {
        return this.codahaleMetricRegistry;
    }
}
