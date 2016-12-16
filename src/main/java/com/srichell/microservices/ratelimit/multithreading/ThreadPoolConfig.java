package com.srichell.microservices.ratelimit.multithreading;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class ThreadPoolConfig {
    /*
 * Name of the Pool. All the threads in this pool will have the pool name as the suffix.
 */
    @JsonProperty
    private String poolName;

    /*
     * the Min number of threads to start with, even if idle
     */
    @JsonProperty
    private int poolStartSize;

    /*
     * the maximum number of threads that the pool is ever allowed to grow into
     */
    @JsonProperty
    private int poolMaxSize;

    /*
     * Max time that excess idle threads will wait for new tasks before terminating if the num threads is > poolStartSize
     */
    @JsonProperty
    private long keepAliveTime;

    /*
     * Capacity of the Blocking queue to start with.
     */
    @JsonProperty
    private int queueStartSize;


    public int poolStartSize() {
        return poolStartSize;
    }

    public int poolMaxSize() {
        return poolMaxSize;
    }

    public long keepAliveTime() {
        return keepAliveTime;
    }

    public int queueStartSize() {
        return queueStartSize;
    }

    public String getPoolName() { return poolName; }

    public ThreadPoolConfig setPoolName(String poolName) {
        this.poolName = poolName;
        return this;
    }

    public ThreadPoolConfig setPoolStartSize(int poolStartSize) {
        this.poolStartSize = poolStartSize;
        return this;
    }

    public ThreadPoolConfig setPoolMaxSize(int poolMaxSize) {
        this.poolMaxSize = poolMaxSize;
        return this;
    }

    public ThreadPoolConfig setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public ThreadPoolConfig setQueueStartSize(int queueStartSize) {
        this.queueStartSize = queueStartSize;
        return this;
    }

    @Override
    public String toString() {
        return "ThreadPoolConfig{" +
                "poolName='" + poolName + '\'' +
                ", poolStartSize=" + poolStartSize +
                ", poolMaxSize=" + poolMaxSize +
                ", keepAliveTime=" + keepAliveTime +
                ", queueStartSize=" + queueStartSize +
                '}';
    }
}
