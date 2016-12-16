package com.srichell.microservices.ratelimit.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.srichell.microservices.ratelimit.interfaces.IAppConfig;
import com.srichell.microservices.ratelimit.multithreading.ThreadPoolConfig;
import io.dropwizard.Configuration;

import java.util.List;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public abstract class AbstractAppConfig extends Configuration implements IAppConfig {

    /**
     * This attribute dictates the spring profile that injects
     * beans for Inversion of Control
     *
     */
    @JsonProperty
    private List<String> springProfileNames;

    /**
     * Fully qualified path (including the Package) of the
     * Class that injects the beans for this application.
     *
     */
    @JsonProperty
    private List<String> springProfileClasses;

    /**
     * Enable/Disable threadpool
     */
    @JsonProperty
    private boolean threadPoolEnabled;

    /**
     * Fully qualified path (including the Package) of the
     * Class that injects the beans for this application.
     *
     */
    @JsonProperty
    private List<ThreadPoolConfig> threadPoolConfigList;

    /**
     * Fully qualified path (including the Package) of the
     * Class which holds the values from the App Configuration YAML file.
     *
     */
    @JsonProperty
    private String appConfigClass;

    /**
     * Fully qualified path (including the Package) of the
     * Class which holds the metrics for this application.
     */
    @JsonProperty
    private String metricsClass;


    @Override
    public String toString() {
        return "AbstractAppConfig{" +
                "springProfileNames=" + springProfileNames +
                ", springProfileClasses=" + springProfileClasses +
                ", threadPoolEnabled=" + threadPoolEnabled +
                ", threadPoolConfigList=" + threadPoolConfigList +
                ", appConfigClass='" + appConfigClass + '\'' +
                ", metricsClass='" + metricsClass + '\'' +
                '}';
    }

    /*
     * Copy Constructor. Make sure this gets called from the sub class
     */
    public AbstractAppConfig(AbstractAppConfig that) {
        this.setSpringProfileNames(that.getSpringProfileNames()).
                setSpringProfileClasses(that.getSpringProfileClasses()).
                setAppConfigClass(that.getAppConfigClass()).
                setMetricsClass(that.getMetricsClass()).
                setThreadPoolEnabled(that.isThreadPoolEnabled()).
                setThreadPoolConfigs(that.getThreadPoolConfigs());
    }

    public AbstractAppConfig() {

    }

    public List<String> getSpringProfileNames() {
        return springProfileNames;
    }

    private AbstractAppConfig setSpringProfileNames(List<String> springProfileNames) {
        this.springProfileNames = springProfileNames;
        return this;
    }

    public List<String> getSpringProfileClasses() {
        return springProfileClasses;
    }

    private AbstractAppConfig setSpringProfileClasses(List<String> springProfileClasses) {
        this.springProfileClasses = springProfileClasses;
        return this;
    }

    @Override
    public String getAppConfigClass() {
        return appConfigClass;
    }

    public String getMetricsClass() {
        return metricsClass;
    }

    @Override
    public List<ThreadPoolConfig> getThreadPoolConfigs() {
        return this.threadPoolConfigList;
    }

    @Override
    public boolean isThreadPoolEnabled() {
        return threadPoolEnabled;
    }

    public AbstractAppConfig setThreadPoolEnabled(boolean threadPoolEnabled) {
        this.threadPoolEnabled = threadPoolEnabled;
        return this;
    }

    public List<ThreadPoolConfig> getThreadPoolConfigList() {
        return threadPoolConfigList;
    }

    public AbstractAppConfig setThreadPoolConfigList(List<ThreadPoolConfig> threadPoolConfigList) {
        this.threadPoolConfigList = threadPoolConfigList;
        return this;
    }

    private AbstractAppConfig setThreadPoolConfigs(List<ThreadPoolConfig> threadPoolConfigList) {
        this.threadPoolConfigList = threadPoolConfigList;
        return this;
    }

    private AbstractAppConfig setAppConfigClass(String appConfigClass) {
        this.appConfigClass = appConfigClass;
        return this;
    }

    private AbstractAppConfig setMetricsClass(String metricsClass) {
        this.metricsClass = metricsClass;
        return this;
    }

}