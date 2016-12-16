package com.srichell.microservices.ratelimit.data.utils;

import java.io.UnsupportedEncodingException;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */

// Abstracting out the common code required to parse a delimited file. Any class that extends this
// should provide the two abstract methods.

public abstract class AbstractDelimitedDataParser<KEY, VALUE> {
    public abstract String getDelimiter(); // Comma seperated, tab seperated, space seperated, et etc etc.

    public abstract AbstractDelimitedDataRecord<KEY,VALUE> getDataRecord(String[] fields) throws UnsupportedEncodingException;

    public AbstractDelimitedDataRecord getDataRecord(String line) throws UnsupportedEncodingException {
        return getDataRecord(line.split(getDelimiter()));
    }
}
