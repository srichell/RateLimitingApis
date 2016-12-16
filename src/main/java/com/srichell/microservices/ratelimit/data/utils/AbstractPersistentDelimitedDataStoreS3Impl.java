package com.srichell.microservices.ratelimit.data.utils;

import com.srichell.microservices.ratelimit.app.config.DataConfig;
import com.srichell.microservices.ratelimit.exceptions.DataReadException;
import com.srichell.microservices.ratelimit.interfaces.IPersistentDelimitedDataStore;

import java.io.BufferedReader;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class AbstractPersistentDelimitedDataStoreS3Impl implements IPersistentDelimitedDataStore {
    // Complete the implementation
    @Override
    public BufferedReader getBufferedReader() throws DataReadException {
        return null;
    }

    @Override
    public long getLastModifiedDate() throws DataReadException {
        return 0;
    }

    @Override
    public IPersistentDelimitedDataStore setDataConfig(DataConfig dataConfig) {
        return null;
    }
}
