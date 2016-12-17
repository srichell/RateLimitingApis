package com.srichell.microservices.ratelimit.rest.apis;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.srichell.microservices.ratelimit.app.main.RateLimitAppState;
import com.srichell.microservices.ratelimit.interfaces.IKeyValueDataCache;
import com.srichell.microservices.ratelimit.pojos.RoomInfo;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Sridhar Chellappa on 12/17/16.
 */
public class FindHotelsWorkItem implements Runnable {
    private final RateLimitAppState rateLimitAppState;
    private final IKeyValueDataCache keyValueDataCache;
    private final AsyncResponse asyncResponse;
    private final String city;
    private final boolean sort;
    private final SortOrder sortOrder;
    private final Timer.Context timer;

    public FindHotelsWorkItem(RateLimitAppState rateLimitAppState,
                              IKeyValueDataCache keyValueDataCache,
                              AsyncResponse asyncResponse,
                              String city,
                              boolean sort,
                              SortOrder sortOrder,
                              Timer.Context timer) {
        this.rateLimitAppState = rateLimitAppState;
        this.keyValueDataCache = keyValueDataCache;
        this.asyncResponse = asyncResponse;
        this.city = city;
        this.sort = sort;
        this.sortOrder = sortOrder;
        this.timer = timer;
    }

    private RateLimitAppState getRateLimitAppState() {
        return rateLimitAppState;
    }

    private AsyncResponse getAsyncResponse() {
        return asyncResponse;
    }

    private String getCity() {
        return city;
    }

    private boolean isSort() {
        return sort;
    }

    private SortOrder getSortOrder() {
        return sortOrder;
    }

    private Timer.Context getTimer() {
        return timer;
    }


    public IKeyValueDataCache getKeyValueDataCache() {
        return keyValueDataCache;
    }

    @Override
    public void run() {
        List<RoomInfo> results = getKeyValueDataCache().get(getCity());

        if (isSort()) {
            Collections.sort(results, new RoomInfoComparator((getSortOrder())));
        }

        String entity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter writer = objectMapper.writer();
        Response response = null;

        try {
            entity = writer.withRootName("Hotel Rooms").writeValueAsString(results);
            response = Response.ok(entity).build();
            getRateLimitAppState().getAppMetricRegistry().getFindHotelThroughput().mark();
            getRateLimitAppState().getAppMetricRegistry().getHttp2xxErrors().inc();
        } catch (JsonProcessingException e) {
            Response.Status status = Response.Status.BAD_REQUEST;
            response = Response.status(status).build();
            getRateLimitAppState().getAppMetricRegistry().getFindHotelErrorRate().mark();
            getRateLimitAppState().getAppMetricRegistry().getHttp4xxErrors().inc();
        } finally {
            sendResponse(response);
            getTimer().stop();
        }

    }

    private void sendResponse(Response result) {
        getAsyncResponse().resume(result);
    }

    private static class RoomInfoComparator implements Comparator<RoomInfo> {
        SortOrder sortOrder;

        public RoomInfoComparator(SortOrder sortOrder) {
            this.sortOrder = sortOrder;
        }

        @Override
        public int compare(RoomInfo room1, RoomInfo room2) {
            int compareResult = (room1.getPrice() < room2.getPrice()) ?
                    -1 : (room1.getPrice() > room2.getPrice()) ? 1 : 0;

            return compareResult * getSortOrder().getSignFlip();
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }


        public SortOrder getSortOrder() {
            return sortOrder;
        }

    }
}
