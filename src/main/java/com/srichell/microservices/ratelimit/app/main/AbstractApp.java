package com.srichell.microservices.ratelimit.app.main;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.srichell.microservices.ratelimit.app.config.AbstractAppConfig;
import com.srichell.microservices.ratelimit.exceptions.BootException;
import com.srichell.microservices.ratelimit.healthchecks.AbstractAppHealthCheck;
import com.srichell.microservices.ratelimit.healthchecks.DefaultAppHealthCheckImpl;
import com.srichell.microservices.ratelimit.interfaces.IAppState;
import com.srichell.microservices.ratelimit.metrics.AbstractMetricsRegistry;
import com.srichell.microservices.ratelimit.rest.apis.AbstractRestResource;
import com.srichell.microservices.ratelimit.rest.apis.RestResources;
import com.srichell.microservices.ratelimit.spring.constants.CommonBeanNames;
import io.dropwizard.Application;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */

public abstract class AbstractApp<T extends AbstractAppConfig> extends Application<T> {
    private static final String DROPWIZARD_MUST_PASS_BOOTUP_STRING = "server";
    private static final int DROPWIZARD_MUST_PASS_BOOTUP_STRING_INDEX = 0;
    private static final int DROPWIZARD_APP_ARGS_START_INDEX = DROPWIZARD_MUST_PASS_BOOTUP_STRING_INDEX + 1;
    private final AppManager appManager;

    protected AbstractApp() {
        this.appManager = new AppManager(this);
    }


    private AppManager getAppManager() {
        return appManager;
    }

    /**
     *  A Hook for the App to do any Initialization post boot.
     */
    public abstract void postBootInitialization();

    /**
     * A hook for the App to register custom beans. Applications must have this function.
     * If they do not have any custom beans to register, then you can have one with an empty body
     */
    public abstract DefaultListableBeanFactory registerCustomBeans(DefaultListableBeanFactory beanFactory, T appConfig);

    /**
     * This function will be called when you stop the App
     * ie, when you send a SIGINT (kill -2) to the process.
     *
     * Use this hook to gracefully shutdown your process.
     */
    public void shutDown() {

    }

    /*
     * The Actual App built on top of this class must provide a logger.
     */
    public abstract Logger getLogger();

    public void boot(String[] args) throws Exception {
        String[] dropWizardArgs = new String[args.length + 1];
        dropWizardArgs[DROPWIZARD_MUST_PASS_BOOTUP_STRING_INDEX] = DROPWIZARD_MUST_PASS_BOOTUP_STRING;
        int i = DROPWIZARD_APP_ARGS_START_INDEX;
        for (String arg : args) {
            dropWizardArgs[i] = arg;
        }

        //Generics.getTypeParameter(getClass());
        run(dropWizardArgs);
    }

    /**
     * Any custom beanFactory that needs to be injected by Spring, can be plugged in by this method. Examples
     * of a custom beanfactory that requires injection is th Ap
     *
     * @param appConfiguration
     * @return
     */

    private DefaultListableBeanFactory createBeanFactoryForDropWizardConfig(
            T appConfiguration,
            Environment environment) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

            /*
             * Create the app config bean.
             * !!!!!!!!! Note !!!!!!! Note !!!!!! Note
             * Since we are creating a Bean from a pre-Allocated Object
             * (of Type springBeans.getBeanClass()), we need a Copy
             * constructor defined for springBeans.getBeanClass() class.
             *
             */
        BeanDefinition appConfigBeanDefinition = BeanDefinitionBuilder.
                rootBeanDefinition(appConfiguration.getAppConfigClass()).
                addConstructorArgValue(appConfiguration).
                getBeanDefinition();
        beanFactory.registerBeanDefinition(CommonBeanNames.APP_CONFIG, appConfigBeanDefinition);

        AbstractMetricsRegistry appMetricsRegistry =
                (AbstractMetricsRegistry) Class.forName(appConfiguration.getMetricsClass()).newInstance();
        appMetricsRegistry.setCodahaleMetricRegistry(environment.metrics());
        BeanDefinition metricsBeanDefinition = BeanDefinitionBuilder.
                rootBeanDefinition(appConfiguration.getMetricsClass()).
                addConstructorArgValue(appMetricsRegistry).
                getBeanDefinition();
        beanFactory.registerBeanDefinition(CommonBeanNames.APP_METRIC_REGISTRY, metricsBeanDefinition);


        return beanFactory;
    }



    /*
     * As part of the Boot Process, Dropwizard will call into this function to complete the boot process
     * and start running.
     *
     * We intercept this and register our custom beans before we begin with our boot process.
     */
    @Override
    public void run(T appconfiguration, Environment environment) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                createBeanFactoryForDropWizardConfig(appconfiguration, environment)
        );

        context.getEnvironment().setActiveProfiles(
                appconfiguration.getSpringProfileNames().toArray(
                        new String[appconfiguration.getSpringProfileNames().size()]
                )
        );

        for (String springProfileClass : appconfiguration.getSpringProfileClasses()) {
            context.register(Class.forName(springProfileClass));
        }
        context.refresh();
        context.start();
        IAppState appState = (IAppState) context.getBean(CommonBeanNames.APP_STATE);
        registerMetrics(appState, appconfiguration, environment);

        /*
         * invoke post creation initialization
         */
        appState.init();

         /*
         * Tie the Entire App with the Lifecycle of the Dropwizard Environment
         * (Post boot init and shutdown are made possible because of this tie-up
         */
        environment.lifecycle().manage(getAppManager());

        /*
         * Get the Rest Resources to plugin. Application is required
         * to provide all the Rest Resources to be injected via a Spring Bean
         */
        RestResources restResources = (RestResources) context.getBean(CommonBeanNames.REST_RESOURCES);
        for (AbstractRestResource restResource : restResources.getResources()) {
            restResource.init();
            environment.jersey().register(restResource);
        }

        registerHealthChecks(appconfiguration, environment, context);

        getLogger().info("Initialization Done. Ready to Serve APIs");
    }

    private void registerHealthChecks(T searchAppconfiguration,
                                      Environment environment,
                                      AnnotationConfigApplicationContext context)  throws Exception {
        HealthCheckRegistry healthCheckRegistry = (HealthCheckRegistry) context.getBean(CommonBeanNames.APP_HEALTH_CHECK_REGISTRY);

        if(healthCheckRegistry == null) {
            throw new BootException("Cannot Boot. No HealthChecks defined");
        }

        AbstractAppHealthCheck appHealthCheck =
                (AbstractAppHealthCheck) context.getBean(CommonBeanNames.APP_HEALTHCHECKER);

        if(appHealthCheck == null) {
            /*
             * Application has chosen to not implement the healthChecker.
             * Wire the default implementation
             */
            registerHealthChecker(environment, healthCheckRegistry, new DefaultAppHealthCheckImpl());
        } else {
            registerHealthChecker(environment, healthCheckRegistry, appHealthCheck);
        }
    }

    private void registerHealthChecker(Environment environment, HealthCheckRegistry registry, AbstractAppHealthCheck healthChecker) {
        environment.healthChecks().register(healthChecker.getName(), healthChecker);
    }

    /*
     * Tie the Metrics Registry with the Lifecycle of this App. This way, metrics can be captured all the
     * way until the App is gracefully shutdown
     */
    private void registerMetrics(IAppState appState, AbstractAppConfig configuration, Environment environment) {
        configuration.getMetricsFactory().configure(
                environment.lifecycle(),
                appState.getMetricRegistry().getCodahaleMetricRegistry()
        );
    }


    /*
     * App Manager gives functionalities for an App to:
     *      a) do custom initialization after boot but before serving traffic.
     *      b) shutdown gracefully
     */
    private static class AppManager implements Managed {
        AbstractApp appToBeManaged;

        public AppManager(AbstractApp appToBeManaged) {
            this.appToBeManaged = appToBeManaged;
        }

        public AbstractApp getAppToBeManaged() {
            return appToBeManaged;
        }

        @Override
        public void start() throws Exception {
            getAppToBeManaged().postBootInitialization();

        }

        @Override
        public void stop() throws Exception {
            getAppToBeManaged().shutDown();
        }
    }
}
