package com.srichell.microservices.ratelimit.rest.apis;

/**
 * Created by Sridhar Chellappa on 12/17/16.
 */
public enum SortOrder {
    /**
     * Sort in Ascending Order
     */
    ASCENDING (1),
    /**
     * Sort in Descending Order
     */
    DESCENDING (-1)
    ;

    SortOrder(int signFlip) {
        this.signFlip = signFlip;
    }

    public int getSignFlip() {
        return signFlip;
    }

    int signFlip;
}
