package com.srichell.microservices.ratelimit.spring.config;

import com.srichell.microservices.ratelimit.app.config.DataConfig;
import com.srichell.microservices.ratelimit.app.main.RateLimitAppState;
import com.srichell.microservices.ratelimit.data.utils.RateLimitDataLoader;
import com.srichell.microservices.ratelimit.data.utils.RateLimitKeyValueCache;
import com.srichell.microservices.ratelimit.data.utils.RateLimitLocalFSDataStore;
import com.srichell.microservices.ratelimit.data.utils.RateLimitS3DataStore;
import com.srichell.microservices.ratelimit.interfaces.IAppState;
import com.srichell.microservices.ratelimit.interfaces.IKeyValueDataCache;
import com.srichell.microservices.ratelimit.interfaces.IPersistentDelimitedDataStore;
import com.srichell.microservices.ratelimit.pojos.CityId;
import com.srichell.microservices.ratelimit.pojos.RoomInfo;
import com.srichell.microservices.ratelimit.rest.apis.AbstractRestResource;
import com.srichell.microservices.ratelimit.rest.apis.RateLimitRestResource;
import com.srichell.microservices.ratelimit.rest.apis.RestResources;
import com.srichell.microservices.ratelimit.spring.constants.CommonBeanNames;
import com.srichell.microservices.ratelimit.spring.constants.RateLimitBeanNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
@Configuration
@Profile(RateLimitBeanNames.RATE_LIMIT_SPRING_PROFILE_NAME)
@ComponentScan({ "com.srichell.microservices.ratelimit.*" })
public class RateLimitSpringConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitSpringConfig.class);
    private static final int HOTEL_DB_CONFIG_INDEX = 0;

    @Autowired
    @Qualifier(CommonBeanNames.APP_STATE)
    private IAppState appState;

    @Autowired
    @Qualifier(RateLimitBeanNames.RATE_LIMIT_DATA_LOADER)
    private RateLimitDataLoader rateLimitDataLoader;


    @Bean(name = RateLimitBeanNames.RATE_LIMIT_APP_STATE)
    @DependsOn(CommonBeanNames.APP_STATE)
    @Scope("singleton")
    public RateLimitAppState getAppState() {
        return (RateLimitAppState) appState;
    }

    @Bean(name = CommonBeanNames.APP_STATE)
    @Scope("singleton")
    public IAppState AppState() {
        return new RateLimitAppState();
    }

    @Bean(name = CommonBeanNames.REST_RESOURCES)
    @DependsOn({ CommonBeanNames.APP_STATE})
    public RestResources restResources() throws InterruptedException {
        List<AbstractRestResource> resourcesList = new ArrayList<AbstractRestResource>();
        resourcesList.add(
                new RateLimitRestResource(getAppState(), getRateLimitDataLoader())
        );
        return new RestResources(resourcesList);
    }


    @Bean(name = RateLimitBeanNames.RATE_LIMIT_CATEGORY_DATA_LOADER)
    @DependsOn({ RateLimitBeanNames.RATE_LIMIT_APP_STATE})
    public RateLimitDataLoader rateLimitDataLoader() throws InstantiationException, IllegalAccessException {
        return new RateLimitDataLoader(getRateLimitDataStore(), getRateLimitDataCache());
    }




    private RateLimitDataLoader getRateLimitDataLoader() {
        return rateLimitDataLoader;
    }

    private IKeyValueDataCache<CityId,RoomInfo> getRateLimitDataCache() {
        return new RateLimitKeyValueCache();
    }


    private IPersistentDelimitedDataStore getRateLimitDataStore() throws IllegalAccessException, InstantiationException {
        DataConfig dataConfig = getAppState().
                                    getAppConfig().
                                    getDataConfigs().get(HOTEL_DB_CONFIG_INDEX);

        return  ((IPersistentDelimitedDataStore) PersistentDataStores.getByName(dataConfig.getDataSourceType()).
                getDataStore().
                newInstance()).
                setDataConfig(dataConfig);

    }


    private enum PersistentDataStores {
        S3_DATASOURCE("s3", RateLimitS3DataStore.class),
        LOCAL_FS_DATASOURCE("fs", RateLimitLocalFSDataStore.class);

        PersistentDataStores(String type, Class<?> dataStore) {
            this.type = type;
            this.dataStore = dataStore;
        }

        private String type;
        private Class<?> dataStore;

        public String getType() {
            return type;
        }


        public Class<?> getDataStore() {
            return dataStore;
        }

        public static PersistentDataStores getByName(String dataSourceTyoe) {
            for (PersistentDataStores store : PersistentDataStores.values()) {
                if (store.getType().equalsIgnoreCase(dataSourceTyoe)) {
                    return store;
                }
            }
            return null;
        }
    }

}
