package com.srichell.microservices.ratelimit.spring.constants;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public interface CommonBeanNames {
    /*
    * Our Whole App Configuration. We do not use XML to
    * All Configuration pushed VIA the YAML file is driven by this Bean
    */
    public final String APP_CONFIG = "appConfiguration";

    public final String APP_METRIC_REGISTRY = "AppMetricRegistry";

    public final String APP_HEALTH_CHECK_REGISTRY = "AppHealthCheckRegistry";

    public final String APP_STATE = "AppState";

    public final String REST_RESOURCES = "RestResources";

    public final String COMMON_SPRING_PROFILE_NAME = "CommonSpringProfile";

    public final String DEFAULT_HEALTH_CHECK_PROFILE_NAME = "DefaultHealthCheck";

    public final String APP_HEALTHCHECKER = "HealthChecker";

    public final String APP_DATA_STORE = "DataStore";

}
