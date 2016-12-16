package com.srichell.microservices.ratelimit.data.utils;

import com.srichell.microservices.ratelimit.pojos.CityId;
import com.srichell.microservices.ratelimit.pojos.RoomInfo;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class RateLimitDataRecord extends AbstractDelimitedDataRecord<CityId, RoomInfo> {
    private final int CITY_INDEX = 0;
    private final int HOTEL_ID_INDEX = 1;
    private final int ROOMTYPE_INDEX = 2;
    private final int PRICE_INDEX = 3;

    @Override
    public CityId getKey() {
        return new CityId(
                getFields()[CITY_INDEX], Long.valueOf(getFields()[HOTEL_ID_INDEX])
        );
    }

    @Override
    public RoomInfo getValue() {
        return new RoomInfo(
                getFields()[ROOMTYPE_INDEX], Float.valueOf(getFields()[PRICE_INDEX])
        );
    }

    public RateLimitDataRecord(String[] fields) {
        super(fields);
    }
}
