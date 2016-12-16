package com.srichell.microservices.ratelimit.data.utils;

import com.srichell.microservices.ratelimit.pojos.CityId;
import com.srichell.microservices.ratelimit.pojos.RoomInfo;

import java.io.UnsupportedEncodingException;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class RateLimitDataParser extends AbstractDelimitedDataParser<CityId, RoomInfo> {
    @Override
    public String getDelimiter() {
        return ",";
    }

    @Override
    public AbstractDelimitedDataRecord<CityId, RoomInfo> getDataRecord(String[] fields) throws UnsupportedEncodingException {
        return new RateLimitDataRecord(fields);
    }
}
