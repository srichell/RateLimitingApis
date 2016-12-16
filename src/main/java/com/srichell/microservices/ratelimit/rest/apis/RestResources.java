package com.srichell.microservices.ratelimit.rest.apis;

import java.util.List;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class RestResources {
    List<AbstractRestResource> resources;

    public RestResources(List<AbstractRestResource> resources) {
        this.resources = resources;
    }

    public List<AbstractRestResource> getResources() {
        return resources;
    }

    public RestResources setResources(List<AbstractRestResource> resources) {
        this.resources = resources;
        return this;
    }
}
