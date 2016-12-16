package com.srichell.microservices.ratelimit.rest.apis;

import com.codahale.metrics.Timer;
import com.srichell.microservices.ratelimit.app.main.RateLimitAppState;
import com.srichell.microservices.ratelimit.data.utils.RateLimitDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
@Path("/v1/hotels")
public class RateLimitRestResource extends AbstractRestResource {
    private static String API_THREAD_POOL = "ApiThreadPool";
    private static final long DATA_LOAD_CHECK_INTERVAL_MILLIS = 500L;
    private static int MAX_RETRIES = 3;
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitRestResource.class);
    private final RateLimitAppState rateLimitAppState;
    private final RateLimitDataLoader rateLimitDataLoader;

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

    @Override
    public void init() throws InterruptedException {
        super.init();
        loadDataInternal();
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

    @GET
    @Path("/rooms")
    public void getRoomsByCity(
                @Suspended final AsyncResponse asyncResponse,
                @QueryParam("apikey")     String apiKey,
                @QueryParam("city")     String city,
                @QueryParam("id") int hotelId,
                @DefaultValue("false") @QueryParam("sort") boolean sort,
                @DefaultValue("false") @QueryParam("sortOrder") String sortOrder
            ) throws InterruptedException {
        int numRetries = 0;
        Timer.Context timer = getRateLimitAppState().getAppMetricRegistry().getFindHotelByCityIdQueryTime().time();
        while(numRetries < MAX_RETRIES) {
            try {
                getRateLimitAppState().
                        getThreadPoolManager().
                        getByName(API_THREAD_POOL).
                        execute(
                                new FindHotelsWorkItem(
                                        getRateLimitAppState(),
                                        asyncResponse,
                                        city, sort, sortOrder, timer)
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
                getLOGGER().error("updateDriverPosition() : Caught RejectedExecutionException. Retrying {} of {} ", numRetries, MAX_RETRIES);
                Thread.sleep(0l);
            }
        }
    }



}
