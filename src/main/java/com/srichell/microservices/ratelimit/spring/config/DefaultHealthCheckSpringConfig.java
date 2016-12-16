package com.srichell.microservices.ratelimit.spring.config;

import com.srichell.microservices.ratelimit.healthchecks.AbstractAppHealthCheck;
import com.srichell.microservices.ratelimit.healthchecks.DefaultAppHealthCheckImpl;
import com.srichell.microservices.ratelimit.spring.constants.CommonBeanNames;
import org.springframework.context.annotation.*;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
@Configuration
@Profile(CommonBeanNames.DEFAULT_HEALTH_CHECK_PROFILE_NAME)
@ComponentScan({ "com.srichell.microservices.*" })
public class DefaultHealthCheckSpringConfig {
    @Bean(name = CommonBeanNames.APP_HEALTHCHECKER)
    @Scope("singleton")
    public AbstractAppHealthCheck healthChecker() {
        return new DefaultAppHealthCheckImpl();
    }
}
