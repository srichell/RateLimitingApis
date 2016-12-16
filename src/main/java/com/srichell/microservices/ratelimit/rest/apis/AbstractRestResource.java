package com.srichell.microservices.ratelimit.rest.apis;

import com.srichell.microservices.ratelimit.interfaces.IAppState;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public abstract class AbstractRestResource {
    IAppState appState;

    public AbstractRestResource(IAppState appState) {
        this.appState = appState;
    }

    public IAppState getAppState() {
        return appState;
    }

    /**
     * Most of the Rest resources do not have anything to initialize. This is dummy init() function
     * addresses that segment.
     *
     * In the rare case of an initialization needed for your RestResource, make sure you override this
     * function. And yes, kindly do a super.init() in that function, just in case some common code gets
     * put in here.
     *
     * @throws InterruptedException
     */
    public void init() throws InterruptedException {

    }

}
