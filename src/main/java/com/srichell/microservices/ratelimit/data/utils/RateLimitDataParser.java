package com.srichell.microservices.ratelimit.data.utils;

import com.srichell.microservices.ratelimit.pojos.RoomInfo;

import java.io.UnsupportedEncodingException;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class RateLimitDataParser extends AbstractDelimitedDataParser<String, RoomInfo> {
    @Override
    public String getDelimiter() {
        return ",";
    }

    @Override
    public AbstractDelimitedDataRecord<String, RoomInfo> getDataRecord(String[] fields) throws UnsupportedEncodingException {
        return new RateLimitDataRecord(fields);
    }
}
