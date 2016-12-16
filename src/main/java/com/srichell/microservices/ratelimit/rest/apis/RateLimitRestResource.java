package com.srichell.microservices.ratelimit.rest.apis;

import com.srichell.microservices.ratelimit.app.main.RateLimitAppState;
import com.srichell.microservices.ratelimit.data.utils.RateLimitDataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

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

    public RateLimitAppState getRateLimitAppState() {
        return rateLimitAppState;
    }

    public RateLimitDataLoader getRateLimitDataLoader() {
        return rateLimitDataLoader;
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
    public Response getRoomInfoById(
                @Suspended final AsyncResponse asyncResponse,
                @QueryParam("apikey")     String apiKey,
                @QueryParam("city")     String searchQuery,
                @QueryParam("id") String category,
                @DefaultValue("false") @QueryParam("sort") boolean sort,
                @DefaultValue("false") @QueryParam("sortOrder") String sortOrder
            ) {

        return null;
    }



}
