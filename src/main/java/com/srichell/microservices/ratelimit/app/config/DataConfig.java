package com.srichell.microservices.ratelimit.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class DataConfig {
    @JsonProperty
    private String dataSourceType;

    @JsonProperty
    private DataLocalConfig localConfig;

    @JsonProperty
    private DataS3Config s3Config;


    public String getDataSourceType() { return dataSourceType; }

    public DataLocalConfig getLocalConfig() { return localConfig; }

    public DataS3Config getS3Config() { return s3Config; }

    public DataConfig setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
        return this;
    }

    public DataConfig setLocalConfig(DataLocalConfig localConfig) {
        this.localConfig = localConfig;
        return this;
    }

    public DataConfig setS3Config(DataS3Config s3Config) {
        this.s3Config = s3Config;
        return this;
    }

    @Override
    public String toString() {
        return "DataConfig{" +
                "dataSourceType='" + dataSourceType + '\'' +
                ", localConfig=" + localConfig +
                ", s3Config=" + s3Config +
                '}';
    }
}
