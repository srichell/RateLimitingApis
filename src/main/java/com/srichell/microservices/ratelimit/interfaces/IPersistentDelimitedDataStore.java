package com.srichell.microservices.ratelimit.interfaces;

import com.srichell.microservices.ratelimit.app.config.DataConfig;
import com.srichell.microservices.ratelimit.exceptions.DataReadException;

import java.io.BufferedReader;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */

/**
 * Interface to abstract out a Delimited Data Store. Examples, include a CSV file on NFS, Local File System, On AWS S3, etc etc.
 */
public interface IPersistentDelimitedDataStore {

    public BufferedReader getBufferedReader() throws DataReadException;
    public long getLastModifiedDate() throws DataReadException;
    public IPersistentDelimitedDataStore setDataConfig(DataConfig dataConfig);

}
