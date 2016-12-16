package com.srichell.microservices.ratelimit.pojos;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class CityId {
    private final String city;
    private final long hotelId;

    public CityId(String city, long hotelId) {
        this.city = city;
        this.hotelId = hotelId;
    }

    public String getCity() {
        return city;
    }

    public long getHotelId() {
        return hotelId;
    }
}
