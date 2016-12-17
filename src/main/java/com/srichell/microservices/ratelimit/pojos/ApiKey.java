package com.srichell.microservices.ratelimit.pojos;

/**
 * Created by Sridhar Chellappa on 12/17/16.
 */

/*
 * This class is an Abstraction for API keys. As of now, it just uses a plain string but
 * it can be extended out to any other Data Type
 */
public class ApiKey {
    private final String apiKey;

    public ApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiKey)) return false;

        ApiKey apiKey1 = (ApiKey) o;

        return getApiKey().equals(apiKey1.getApiKey());

    }

    @Override
    public int hashCode() {
        return getApiKey().hashCode();
    }

    @Override
    public String toString() {
        return "ApiKey{" +
                "apiKey='" + apiKey + '\'' +
                '}';
    }
}
