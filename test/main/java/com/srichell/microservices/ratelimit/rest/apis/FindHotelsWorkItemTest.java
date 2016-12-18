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

import javax.ws.rs.core.Response;

import static org.testng.Assert.*;

/**
 * Created by Sridhar Chellappa on 12/18/16.
 */
public class FindHotelsWorkItemTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(FindHotelsWorkItemTest.class);
    private TestSetup testSetup = new TestSetup();
    private FindHotelsWorkItem workItem;

    public static Logger getLOGGER() {
        return LOGGER;
    }

    public TestSetup getTestSetup() {
        return testSetup;
    }
    @BeforeMethod
    public void setUp() throws Exception {
        getLOGGER().info("begin setup. Setup");
        if(!getTestSetup().isSetUpValid()) getTestSetup().setUp();
        Timer.Context timer =  TestSetup.getAppState().getAppMetricRegistry().getFindHotelByCityIdQueryTime().time();
        this.workItem = new FindHotelsWorkItem (
                TestSetup.getAppState(),
                TestSetup.getDataLoader().getKeyValueDataCache(),
                null,
                "Bangkok", true,
                SortOrder.ASCENDING,
                timer
        );


    }

    @AfterMethod
    public void tearDown() throws Exception {
        if(getTestSetup().isSetUpValid()) getTestSetup().tearDown();
    }

    @Test
    public void testRunHappyCase() throws Exception {
        final MockedFindHotelsWorkItem mockedWorkItem = new MockedFindHotelsWorkItem();
        getWorkItem().run();
        Assert.assertEquals(true, (mockedWorkItem.getResponse().getStatus() == 200));

    }

    public FindHotelsWorkItem getWorkItem() {
        return workItem;
    }

    final class MockedFindHotelsWorkItem extends MockUp<FindHotelsWorkItem> {
        Response response;

        public Response getResponse() {
            return response;
        }

        public MockedFindHotelsWorkItem setResponse(Response response) {
            this.response = response;
            return this;
        }

        @Mock
        void sendResponse(Response result) {
            setResponse(result);
        }
    }


}