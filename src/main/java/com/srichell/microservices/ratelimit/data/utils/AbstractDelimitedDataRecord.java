package com.srichell.microservices.ratelimit.data.utils;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public abstract class AbstractDelimitedDataRecord<KEY, VALUE> {
    private String[] fields;

    public abstract KEY getKey();
    public abstract VALUE getValue();

    public AbstractDelimitedDataRecord(String[] fields) {
        this.fields = fields;
    }

    public String[] getFields() {
        return fields;
    }
}
