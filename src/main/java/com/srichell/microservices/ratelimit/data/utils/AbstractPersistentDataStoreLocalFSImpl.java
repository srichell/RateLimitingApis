package com.srichell.microservices.ratelimit.data.utils;

import com.srichell.microservices.ratelimit.app.config.DataConfig;
import com.srichell.microservices.ratelimit.exceptions.DataReadException;
import com.srichell.microservices.ratelimit.interfaces.IPersistentDelimitedDataStore;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public abstract class AbstractPersistentDataStoreLocalFSImpl implements IPersistentDelimitedDataStore {
    private DataConfig dataConfig;

    public AbstractPersistentDataStoreLocalFSImpl() {
        getLogger().info("Using Local File System as the Persistent Data Store");
    }

    protected abstract Logger getLogger();
    protected abstract String getLocalDir();
    protected abstract String getLocalFileName();

    public DataConfig getDataConfig() {
        return dataConfig;
    }

    @Override
    public IPersistentDelimitedDataStore setDataConfig(DataConfig dataConfig) {
        this.dataConfig = dataConfig;
        return this;
    }

    private Path getPath() {
        Path path = FileSystems.getDefault().getPath(getLocalDir(), getLocalFileName());
        return path;
    }

    @Override
    public long getLastModifiedDate() throws DataReadException {
        try {
            Path path = FileSystems.getDefault().getPath(getLocalDir(), getLocalFileName());
            return Files.getLastModifiedTime(path, java.nio.file.LinkOption.NOFOLLOW_LINKS).toMillis();
        } catch (IOException e) {
            throw new DataReadException("Error getting last modified file timestamp", e);
        }
    }

    @Override
    public BufferedReader getBufferedReader() throws DataReadException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        getInputStream()
                )
        );
        if (bufferedReader == null) {
            throw new DataReadException("Error Creating Buffered Reader for " + getPath().toString());
        }

        return bufferedReader;
    }

    private InputStream getInputStream() throws DataReadException {
        try {
            return Files.newInputStream(getPath(), StandardOpenOption.READ);
        } catch (IOException e) {
            throw new DataReadException("Error getting file input stream for " + getPath().toString(), e);
        }
    }
}
