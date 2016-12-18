package com.srichell.microservices.ratelimit.unittests.common;

import com.codahale.metrics.MetricRegistry;
import com.srichell.microservices.ratelimit.app.config.DataConfig;
import com.srichell.microservices.ratelimit.app.config.DataLocalConfig;
import com.srichell.microservices.ratelimit.app.config.RateLimitAppConfig;
import com.srichell.microservices.ratelimit.app.config.RateLimitConfig;
import com.srichell.microservices.ratelimit.app.main.RateLimitAppState;
import com.srichell.microservices.ratelimit.data.utils.RateLimitDataLoader;
import com.srichell.microservices.ratelimit.interfaces.IAppState;
import com.srichell.microservices.ratelimit.metrics.RateLimitMetricRegistry;
import com.srichell.microservices.ratelimit.multithreading.ThreadPoolConfig;
import com.srichell.microservices.ratelimit.rest.apis.AbstractRestResource;
import com.srichell.microservices.ratelimit.rest.apis.RestResources;
import com.srichell.microservices.ratelimit.spring.constants.CommonBeanNames;
import com.srichell.microservices.ratelimit.spring.constants.RateLimitBeanNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sridhar Chellappa on 12/18/16.
 */
public class TestSetup {
    private static IAppState appState;
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSetup.class);
    private static AtomicBoolean setUpValid = new AtomicBoolean(false);
    private static AtomicBoolean tearDownInProgress = new AtomicBoolean(false);
    private static AtomicInteger setUpRefCount = new AtomicInteger(0);
    private static RateLimitDataLoader dataLoader;

    public static RateLimitAppState getAppState() {
        return (RateLimitAppState) appState;
    }

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public static boolean isSetUpValid() {
        return setUpValid.get();
    }

    public static RateLimitDataLoader getDataLoader() {
        return dataLoader;
    }

    private static boolean isTearDownInProgress() {
        return tearDownInProgress.get();
    }

    private static void setTearDownInProgress() {
        tearDownInProgress.set(true);
    }

    public static int getSetUpRefCount() {
        return setUpRefCount.get();
    }

    private static List<DataConfig> populateDataConfigs() {
        List<DataConfig> dataConfigs = new ArrayList<DataConfig>();
        dataConfigs.add(
                new DataConfig().
                        setDataSourceType("fs").
                        setLocalConfig(new DataLocalConfig().setLocalDir("/tmp").setLocalFileName("hoteldb.csv"))
        );
        return dataConfigs;
    }

    private DefaultListableBeanFactory createBeanFactoryForDropWizardConfig() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        List<ThreadPoolConfig> threadPoolConfigs = new ArrayList<ThreadPoolConfig>();
        threadPoolConfigs.add(
                new ThreadPoolConfig().setPoolName("ApiThreadPool").
                        setPoolStartSize(1).
                        setPoolMaxSize(5).
                        setKeepAliveTime(1000L).
                        setQueueStartSize(10)
        );
        RateLimitAppConfig appConfiguration = new RateLimitAppConfig();

        appConfiguration.setThreadPoolEnabled(true);
        appConfiguration.setThreadPoolConfigList(threadPoolConfigs);
        appConfiguration.setDataConfigs(populateDataConfigs());
        appConfiguration.setRateLimitAlgorithm("TokenBucket");
        appConfiguration.setRateLimitConfigs(populateRateLimitConfigs());



        // First add the App Configuration itself
        BeanDefinition appConfigBeanDefinition = BeanDefinitionBuilder.
                rootBeanDefinition(RateLimitAppConfig.class).
                addConstructorArgValue(appConfiguration).
                getBeanDefinition();
        beanFactory.registerBeanDefinition(CommonBeanNames.APP_CONFIG, appConfigBeanDefinition);

        MetricRegistry codahaleMetricsRegistry = new MetricRegistry();

        RateLimitMetricRegistry rateLimitMetricRegistry = (RateLimitMetricRegistry) new RateLimitMetricRegistry().
                setCodahaleMetricRegistry(codahaleMetricsRegistry);
        BeanDefinition metricRegistryBeanDefinition = BeanDefinitionBuilder.
                rootBeanDefinition(RateLimitMetricRegistry.class).
                addConstructorArgValue(rateLimitMetricRegistry).
                getBeanDefinition();
        beanFactory.registerBeanDefinition(CommonBeanNames.APP_METRIC_REGISTRY, metricRegistryBeanDefinition);


        return beanFactory;
    }

    private void setDataLoader (RateLimitDataLoader loader) {
        this.dataLoader = loader;
    }

    private List<RateLimitConfig> populateRateLimitConfigs() {
        List<RateLimitConfig> rateLimitConfigs = new ArrayList<RateLimitConfig>();

        rateLimitConfigs.add(
                new RateLimitConfig().setBlessedApiKey("abcd").setRequestsPerMinute(1L).setRateViolationPenaltyMinutes(5)
        );
        return rateLimitConfigs;
    }

    private void setActiveSpringProfiles(AnnotationConfigApplicationContext springContext) {
        String[] profiles = new String[] {"WhereIsMyDriverSpringProfile",
                "RateLimitSpringProfile",
                "DefaultHealthCheck"
        };
        springContext.getEnvironment().setActiveProfiles(
                profiles
        );
    }

    private void registerSpringProfileClasses(AnnotationConfigApplicationContext springContext) throws ClassNotFoundException {
        springContext.register(Class.forName("com.srichell.microservices.ratelimit.spring.config.RateLimitSpringConfig"));
        springContext.register(Class.forName("com.srichell.microservices.ratelimit.spring.config.CommonSpringConfig"));
        springContext.register(Class.forName("com.srichell.microservices.ratelimit.spring.config.DefaultHealthCheckSpringConfig"));
    }

    public static void setupValid() {
        setUpValid.set(true);
    }


    /**
     * See the Setup()/tearDown() routines as DynamicRankingApp.start()/stop().
     *
     * @throws Exception
     */
    @BeforeSuite
    public void setUp() throws Exception {
        if(isSetUpValid()) {
            setUpRefCount.incrementAndGet();
            return;
        }

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                createBeanFactoryForDropWizardConfig()
        );

        getLOGGER().debug("Before Suite Setup Starting");
        //Enable a "live" profile

        setActiveSpringProfiles(context);
        registerSpringProfileClasses(context);

        context.refresh();
        context.start();
        this.appState = (IAppState) context.getBean(CommonBeanNames.APP_STATE);
        //appState.getRankingStatsClient().start();
        getAppState().init();

        RestResources restResources = (RestResources) context.getBean(CommonBeanNames.REST_RESOURCES);
        for (AbstractRestResource restResource : restResources.getResources()) {
            restResource.init();
        }

        setDataLoader(
                (RateLimitDataLoader) context.getBean(RateLimitBeanNames.RATE_LIMIT_CATEGORY_DATA_LOADER)
        );

        setupValid();
        getLOGGER().info("Before Suite Setup Done");
    }

    @AfterSuite
    public void tearDown() throws Exception {
        if(!isSetUpValid()) {
            return;
        }

        if(!isTearDownInProgress()) {
            setTearDownInProgress();
            setUpRefCount.decrementAndGet();

            if (setUpRefCount.get() != 0) {
                getLOGGER().info("TearDown. RefCount is {}. Returning", setUpRefCount);
                return;
            }
            getLOGGER().debug("After Suite tearDown DONE");
            setUpValid.set(false);
        }
    }

}
