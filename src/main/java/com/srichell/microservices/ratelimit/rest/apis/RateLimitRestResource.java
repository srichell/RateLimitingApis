package com.srichell.microservices.ratelimit.rest.apis;

import com.codahale.metrics.Timer;
import com.srichell.microservices.ratelimit.algorithms.RateLimitTokenBucketAlgorithm;
import com.srichell.microservices.ratelimit.app.config.RateLimitConfig;
import com.srichell.microservices.ratelimit.app.main.RateLimitAppState;
import com.srichell.microservices.ratelimit.data.utils.RateLimitDataLoader;
import com.srichell.microservices.ratelimit.interfaces.RateLimitAlgorithm;
import com.srichell.microservices.ratelimit.pojos.ApiKey;
import com.srichell.microservices.ratelimit.spring.config.RateLimitSpringConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
@Path("/v1/hotels")
public class RateLimitRestResource extends AbstractRestResource {
    private static String API_THREAD_POOL = "ApiThreadPool";
    private static String ASC_SORT_ORDER = "ASC";
    private static String DESC_SORT_ORDER = "DESC";
    private static final long DATA_LOAD_CHECK_INTERVAL_MILLIS = 500L;
    private static int MAX_RETRIES = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitRestResource.class);
    private final RateLimitAppState rateLimitAppState;
    private final RateLimitDataLoader rateLimitDataLoader;

    private RateLimitRulesCheck rulesCheck;
    private RateLimitAlgorithm rateLimitAlgorithm;


    public RateLimitRestResource(RateLimitAppState appState, RateLimitDataLoader rateLimitDataLoader) {
        super(appState);
        this.rateLimitAppState = appState;
        this.rateLimitDataLoader = rateLimitDataLoader;

    }

    private RateLimitAppState getRateLimitAppState() {
        return rateLimitAppState;
    }

    private RateLimitDataLoader getRateLimitDataLoader() {
        return rateLimitDataLoader;
    }

    private static Logger getLOGGER() {
        return LOGGER;
    }

    private RateLimitAlgorithm getRateLimitAlgorithm() {
        return rateLimitAlgorithm;
    }

    public RateLimitRestResource setRateLimitAlgorithm(RateLimitAlgorithm rateLimitAlgorithm) {
        this.rateLimitAlgorithm = rateLimitAlgorithm;
        return this;
    }

    @Override
    public void init() throws InterruptedException, IllegalAccessException, InstantiationException {
        super.init();
        loadDataInternal();
        RateLimitAlgorithm rateLimitAlgorithm = (RateLimitAlgorithm) RateLimitAlgorithms.getByType(
                                                    getRateLimitAppState().getAppConfig().getRateLimitAlgorithm()
                                                ).getAlgorithmImplClass().newInstance();
        this.setRateLimitAlgorithm(
                rateLimitAlgorithm.setAppState(getRateLimitAppState())
        );
        this.rulesCheck = new RateLimitRulesCheck();
    }

    private RateLimitRulesCheck getRulesCheck() {
        return rulesCheck;
    }

    private void loadDataInternal() throws InterruptedException {
        getRateLimitDataLoader().start();

        while (
                (!getRateLimitDataLoader().isLoadCompleted())) {
            Thread.sleep(DATA_LOAD_CHECK_INTERVAL_MILLIS);
        }
    }

    @POST
    @Path("/data")
    public Response loadData() throws InterruptedException {

        loadDataInternal();

        return Response.ok(
                "Data Load Complete"
        ).build();
    }

    /**
     * This is an Async API. Processing is taken care of in the context of another thread and the same thread
     * sends the response When the Response is ready,
     *
     * @param asyncResponse
     * @param apiKey
     * @param city
     * @param sort
     * @param sortOrder
     * @throws InterruptedException
     */

    @GET
    @Path("/rooms")
    public void getRoomsByCity(
                @Suspended final AsyncResponse asyncResponse,
                @QueryParam("apikey")     String apiKey,
                @QueryParam("city")     String city,
                @DefaultValue("false") @QueryParam("sort") boolean sort,
                @DefaultValue("ASC") @QueryParam("sortOrder") String sortOrder
            ) throws InterruptedException {
        int numRetries = 0;
        Timer.Context timer = getRateLimitAppState().getAppMetricRegistry().getFindHotelByCityIdQueryTime().time();
        /*
         * Scheduling variances can occur causing threads to be scheduled milliseconds apart. So, carry out the
         * Rules Check inline. If the Rules pass, then process the request asynchronously.
         */
        RateLimitRulesCheckResult result = getRulesCheck().check(apiKey);

        if(!result.isPassed()) {
            asyncResponse.resume(result.getResponse());
            return;
        }

        while(numRetries < MAX_RETRIES) {
            try {
                getRateLimitAppState().
                        getThreadPoolManager().
                        getByName(API_THREAD_POOL).
                        execute(
                                new FindHotelsWorkItem(
                                        getRateLimitAppState(),
                                        getRateLimitDataLoader().getKeyValueDataCache(),
                                        asyncResponse,
                                        city, sort,
                                        sortOrder.equalsIgnoreCase("ASC") ? SortOrder.ASCENDING : SortOrder.DESCENDING,
                                        timer)
                        );
                return;
            } catch (RejectedExecutionException e) {
                numRetries++;
                if(numRetries >= MAX_RETRIES) {
                    getRateLimitAppState().getAppMetricRegistry().getFindHotelErrorRate().mark();
                    getRateLimitAppState().getAppMetricRegistry().getHttp5xxErrors().inc();
                    timer.stop();
                    throw(e);
                }
                getLOGGER().error("getRoomsByCity() : Caught RejectedExecutionException. Retrying {} of {} ", numRetries, MAX_RETRIES);
                Thread.sleep(0l);
            }
        }
    }

    private class RateLimitRulesCheck {
        List<ApiKey> blessedApiKeys = new ArrayList<ApiKey>();
        Map<ApiKey, RateLimitConfig> blessedApiKeyInfoMap = new HashMap<ApiKey, RateLimitConfig>();

        /**
         * Build a hash map of valid API keys for a faster lookup.
         *
         * Assumption. All the Valid API keys will fit into one single Map.
         */
        private void buildRateInfoMap() {
            List<RateLimitConfig> rateLimitConfigs =
                    getOuterClass().
                            getRateLimitAppState().
                            getAppConfig().
                            getRateLimitConfigs();

            for (RateLimitConfig rateLimitConfig : rateLimitConfigs) {
                getBlessedApiKeyInfoMap().put(new ApiKey(rateLimitConfig.getBlessedApiKey()), rateLimitConfig);
            }
        }

        private Map<ApiKey, RateLimitConfig> getBlessedApiKeyInfoMap() {
            return blessedApiKeyInfoMap;
        }

        public RateLimitRulesCheck() {
            buildRateInfoMap();
        }

        private RateLimitRestResource getOuterClass() {
            return RateLimitRestResource.this;
        }

        public RateLimitRulesCheckResult check(String apiKey) {
            /*
             * First check for API Key validity
             */
            Response.ResponseBuilder builder = Response.ok();
            boolean checkPassed = true;
            String errorMessage = "SUCCESS";
            if((getBlessedApiKeyInfoMap().get(new ApiKey(apiKey))) == null) {
                // Key not valid.
                checkPassed = false;
                builder =  Response.status(Response.Status.UNAUTHORIZED);
                errorMessage = String.format( "%s %s", "UnAuthorized API key", apiKey);
            }

            return new RateLimitRulesCheckResult(checkPassed, builder.entity(errorMessage).build());
        }

    }

    private static class RateLimitRulesCheckResult {
        private final boolean passed;
        private final Response response;

        public RateLimitRulesCheckResult(boolean passed, Response response) {
            this.passed = passed;
            this.response = response;
        }

        public boolean isPassed() {
            return passed;
        }

        public Response getResponse() {
            return response;
        }
    }

    private enum RateLimitAlgorithms {
        TOKEN_BUCKET("TokenBucket", RateLimitTokenBucketAlgorithm.class),
        ;

        RateLimitAlgorithms(String type, Class<?> algorithmImplClass) {
            this.type = type;
            this.algorithmImplClass = algorithmImplClass;
        }

        private String type;
        private Class<?> algorithmImplClass;

        public String getType() {
            return type;
        }


        public Class<?> getAlgorithmImplClass() {
            return algorithmImplClass;
        }

        public static RateLimitAlgorithms getByType(String algoType) {
            for (RateLimitAlgorithms algo : RateLimitAlgorithms.values()) {
                if (algo.getType().equalsIgnoreCase(algoType)) {
                    return algo;
                }
            }
            return null;
        }
    }

}
