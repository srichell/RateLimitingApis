package com.srichell.microservices.ratelimit.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class DataLocalConfig {
    @JsonProperty
    private String localDir;

    @JsonProperty
    private String localFileName;

    public String getLocalDir() { return localDir; }

    public String getLocalFileName() { return localFileName; }

    public DataLocalConfig setLocalDir(String localDir) {
        this.localDir = localDir;
        return this;
    }


    public DataLocalConfig setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
        return this;
    }
}
