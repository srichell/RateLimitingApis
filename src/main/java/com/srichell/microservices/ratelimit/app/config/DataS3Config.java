package com.srichell.microservices.ratelimit.app.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class DataS3Config {
    @JsonProperty
    private String s3EndPoint;

    @JsonProperty
    private String bucketName;

    @JsonProperty
    private String s3Key;

    public String getEndPoint() { return s3EndPoint; }

    public String getBucketName() { return bucketName; }

    public String getS3Key() { return s3Key; }


    public DataS3Config setS3EndPoint(String s3EndPoint) {
        this.s3EndPoint = s3EndPoint;
        return this;
    }

    public DataS3Config setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public DataS3Config setS3Key(String s3Key) {
        this.s3Key = s3Key;
        return this;
    }
}
