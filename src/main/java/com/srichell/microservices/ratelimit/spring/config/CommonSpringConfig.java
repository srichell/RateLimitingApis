package com.srichell.microservices.ratelimit.spring.config;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.srichell.microservices.ratelimit.spring.constants.CommonBeanNames;
import org.springframework.context.annotation.*;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
@Configuration
@Profile(CommonBeanNames.COMMON_SPRING_PROFILE_NAME)
@ComponentScan({ "com.srichell.microservices.ratelimit.*" })
public class CommonSpringConfig {

    @Bean(name = CommonBeanNames.APP_HEALTH_CHECK_REGISTRY)
    @Scope("singleton")
    public HealthCheckRegistry healthCheckRegistry() {

        return new HealthCheckRegistry();
    }

}

