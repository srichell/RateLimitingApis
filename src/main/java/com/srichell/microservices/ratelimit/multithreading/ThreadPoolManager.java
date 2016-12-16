package com.srichell.microservices.ratelimit.multithreading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class ThreadPoolManager {
    private List<ThreadPoolConfig> threadPoolConfigs;
    private Map<String, ThreadPoolExecutor> threadPoolExecutorMap = new ConcurrentHashMap<String, ThreadPoolExecutor>();
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPoolManager.class);

    public ThreadPoolManager(List<ThreadPoolConfig> threadPoolConfigs) {
        this.threadPoolConfigs = threadPoolConfigs;

        for(ThreadPoolConfig threadPoolConfig: threadPoolConfigs) {

            /*
             * NOTE!!!! NOTE!!!! NOTE!!!!
             *
             * When there are a flurry of requests, we can run out of threads in the threadpool causing
             * RejectedHandlerExecutions. While we can handle those exceptions by waiting
             * and retrying, this does add to the latency. Hence, We use CallerRunsPolicy() so that
             * the WorkItem will run in the context of the thread that pushes the item (sort of
             * Automatic Backpressure handling)
             *
             */
            ThreadPoolExecutor threadPoolExecutor =
                    new ThreadPoolExecutor(
                            threadPoolConfig.poolStartSize(),
                            threadPoolConfig.poolMaxSize(),
                            threadPoolConfig.keepAliveTime(),
                            TimeUnit.MILLISECONDS,
                            new ArrayBlockingQueue<Runnable>(threadPoolConfig.queueStartSize()),
                            new ThreadPoolExecutor.CallerRunsPolicy()
                    );
            /*
             * By default, even core threads are initially created and started only when new tasks arrive,
             * Override this using  prestartAllCoreThreads() to make sure threads are available.
             */
            threadPoolExecutor.prestartAllCoreThreads();

            getThreadPoolExecutorMap().put(
                    threadPoolConfig.getPoolName(),
                    threadPoolExecutor

            );
        }
    }

    private Map<String, ThreadPoolExecutor> getThreadPoolExecutorMap() {
        return threadPoolExecutorMap;
    }

    public ThreadPoolExecutor getByName(String poolName) {
        return getThreadPoolExecutorMap().get(poolName);
    }

}
