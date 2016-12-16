package com.srichell.microservices.ratelimit.rest.apis;

import com.codahale.metrics.Timer;
import com.srichell.microservices.ratelimit.app.main.RateLimitAppState;

import javax.ws.rs.container.AsyncResponse;

/**
 * Created by Sridhar Chellappa on 12/17/16.
 */
public class FindHotelsWorkItem implements Runnable {

    private final RateLimitAppState rateLimitAppState;
    private final AsyncResponse asyncResponse;
    private final String city;
    private final boolean sort;
    private final String sortOrder;
    private final Timer.Context timer;

    public FindHotelsWorkItem(RateLimitAppState rateLimitAppState, AsyncResponse asyncResponse, String city, boolean sort, String sortOrder, Timer.Context timer) {
        this.rateLimitAppState = rateLimitAppState;
        this.asyncResponse = asyncResponse;
        this.city = city;
        this.sort = sort;
        this.sortOrder = sortOrder;
        this.timer = timer;
    }

    @Override
    public void run() {

    }
}
