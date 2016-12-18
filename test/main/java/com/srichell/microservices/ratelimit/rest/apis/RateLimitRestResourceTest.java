package com.srichell.microservices.ratelimit.rest.apis;

import com.codahale.metrics.Timer;
import com.srichell.microservices.ratelimit.unittests.common.TestSetup;
import mockit.Mock;
import mockit.MockUp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.Response;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.*;

/**
 * Created by Sridhar Chellappa on 12/18/16.
 */
public class RateLimitRestResourceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimitRestResourceTest.class);
    private static final String VALID_API_KEY = "abcd";
    private static final String city = "Bangkok";
    private static final String INVALID_API_KEY_FOR_TEST = "1234";
    private MyAsyncResponse asyncResponse;

    public static Logger getLOGGER() {
        return LOGGER;
    }

    private TestSetup testSetup = new TestSetup();
    private  RateLimitRestResource restResource;


    public TestSetup getTestSetup() {
        return testSetup;
    }

    public RateLimitRestResource getRestResource() {
        return restResource;
    }


    private  void setAsyncResponse(MyAsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
    }

    private  MyAsyncResponse getAsyncResponse() {
        return asyncResponse;
    }

    @BeforeMethod
    public void setUp() throws Exception {
        getLOGGER().info("begin setup. Setup");
        if(!getTestSetup().isSetUpValid()) getTestSetup().setUp();
        Timer.Context timer =  TestSetup.getAppState().getAppMetricRegistry().getFindHotelByCityIdQueryTime().time();
        this.restResource = new RateLimitRestResource(
                TestSetup.getAppState(), TestSetup.getDataLoader()
        );
        getRestResource().init();
        setAsyncResponse(new MyAsyncResponse());

    }

    @AfterMethod
    public void tearDown() throws Exception {
        if(getTestSetup().isSetUpValid()) getTestSetup().tearDown();
    }

    @Test
    public void testLoadData() throws Exception {
        Response response = restResource.loadData();
        Assert.assertEquals(true, (response.getStatus() == 200));

    }

    @Test
    public void testResetCreditBalanceHappyCase() throws Exception {
        Response response = getRestResource().resetCreditBalance(VALID_API_KEY, 10, 10);
        Assert.assertEquals(true, (response.getStatus() == 200));

    }

    @Test
    public void testResetCreditErrorCaseInvalidApiKey() throws Exception {
        Response response = getRestResource().resetCreditBalance(INVALID_API_KEY_FOR_TEST, 10, 10);
        Assert.assertEquals(true, (response.getStatus() == 401));

    }


    @Test
    public void testGetRoomsByCityHappyCase() throws Exception {
        getRestResource().getRoomsByCity(
                getAsyncResponse(), VALID_API_KEY, city, true, SortOrder.ASCENDING.name()

        );

        // Wait for the Async API threadpool thread to populate Response
        Thread.sleep(1000);
        Assert.assertEquals(true, (getAsyncResponse().getResponse().getStatus() == 200));

    }

    @Test
    public void testGetRoomsByCityErrorCaseInvalidApiKey() throws Exception {
        getRestResource().getRoomsByCity(
                getAsyncResponse(), INVALID_API_KEY_FOR_TEST, city, true, SortOrder.ASCENDING.name()

        );

        Assert.assertEquals(true, (getAsyncResponse().getResponse().getStatus() == 401));
    }

    @Test
    public void testGetRoomsByCityErrorCaseRateLimitExhausted() throws Exception {
        getRestResource().getRoomsByCity(
                getAsyncResponse(), VALID_API_KEY, city, true, SortOrder.ASCENDING.name()

        );
        // Wait for the Async API threadpool thread to populate Response
        Thread.sleep(1000);
        // First expect a 200
        Assert.assertEquals(true, (getAsyncResponse().getResponse().getStatus() == 200));

        // Now we should hit a 429 - Rate Limit exceeded
        getRestResource().getRoomsByCity(
                getAsyncResponse(), VALID_API_KEY, city, true, SortOrder.ASCENDING.name()

        );
        Assert.assertEquals(true, (getAsyncResponse().getResponse().getStatus() == 429));
    }

    private static class MyAsyncResponse implements AsyncResponse {

        Response response;
        @Override
        public boolean resume(Object response) {
            this.response = (Response) response;
            return true;
        }

        public Response getResponse() {
            return this.response;
        }

        @Override
        public boolean resume(Throwable response) {
            return false;
        }

        @Override
        public boolean cancel() {
            return false;
        }

        @Override
        public boolean cancel(int retryAfter) {
            return false;
        }

        @Override
        public boolean cancel(Date retryAfter) {
            return false;
        }

        @Override
        public boolean isSuspended() {
            return false;
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public boolean setTimeout(long time, TimeUnit unit) {
            return false;
        }

        @Override
        public void setTimeoutHandler(TimeoutHandler handler) {

        }

        @Override
        public Collection<Class<?>> register(Class<?> callback) {
            return null;
        }

        @Override
        public Map<Class<?>, Collection<Class<?>>> register(Class<?> callback, Class<?>... callbacks) {
            return null;
        }

        @Override
        public Collection<Class<?>> register(Object callback) {
            return null;
        }

        @Override
        public Map<Class<?>, Collection<Class<?>>> register(Object callback, Object... callbacks) {
            return null;
        }
    }

}