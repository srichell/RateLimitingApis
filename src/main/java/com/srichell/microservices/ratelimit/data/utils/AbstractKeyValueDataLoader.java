package com.srichell.microservices.ratelimit.data.utils;

import com.codahale.metrics.Timer;
import com.srichell.microservices.ratelimit.exceptions.DataReadException;
import com.srichell.microservices.ratelimit.interfaces.IKeyValueDataCache;
import com.srichell.microservices.ratelimit.interfaces.IPersistentDelimitedDataStore;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public abstract class AbstractKeyValueDataLoader<KEY, VALUE> {
    private IPersistentDelimitedDataStore persistentDataStore; // DataSource
    private IKeyValueDataCache<KEY,VALUE> keyValueDataCache;    // dataStore

    private static final String LOADER_DAEMON_NAME = "keyValueDataLoaderDaemon";
    private volatile boolean loadCompleted = false;

    private Thread keyValueDataLoaderDaemon;

    public abstract Logger getLogger();

    public AbstractKeyValueDataLoader(IPersistentDelimitedDataStore persistentDataStore, IKeyValueDataCache<KEY, VALUE> keyValueDataCache) {
        this.persistentDataStore = persistentDataStore;
        this.keyValueDataCache = keyValueDataCache;
    }

    public boolean isLoadCompleted() {
        return loadCompleted;
    }

    public AbstractKeyValueDataLoader setLoadCompleted(boolean loadCompleted) {
        this.loadCompleted = loadCompleted;
        return this;
    }

    public void start() {
        loadCompleted = false;
        keyValueDataLoaderDaemon = new Thread(new KeyValueDataLoaderThread<KEY, VALUE>(persistentDataStore, keyValueDataCache));
        keyValueDataLoaderDaemon.setDaemon(true);
        keyValueDataLoaderDaemon.setName(LOADER_DAEMON_NAME);
        getLogger().info("Starting {}", LOADER_DAEMON_NAME);
        keyValueDataLoaderDaemon.start();
    }

    public IKeyValueDataCache<KEY, VALUE> getKeyValueDataCache() {
        return keyValueDataCache;
    }

    public abstract Timer.Context getLoadTimerContext();
    public abstract AbstractDelimitedDataParser<KEY,VALUE> getDataParser();


    private class KeyValueDataLoaderThread<KEY, VALUE> implements Runnable {
        private final IPersistentDelimitedDataStore store;
        private final IKeyValueDataCache<KEY,VALUE> cache;


        public KeyValueDataLoaderThread(IPersistentDelimitedDataStore store, IKeyValueDataCache<KEY,VALUE> cache) {
            this.store = store;
            this.cache = cache;
        }

        private IPersistentDelimitedDataStore getStore() {
            return store;
        }

        private IKeyValueDataCache<KEY,VALUE> getCache() {
            return cache;
        }

        private Timer.Context getLoadTimerContext() {
            return AbstractKeyValueDataLoader.this.getLoadTimerContext();
        }

        private AbstractDelimitedDataParser<KEY, VALUE> getDataParser() {
            return (AbstractDelimitedDataParser<KEY, VALUE>) AbstractKeyValueDataLoader.this.getDataParser();
        }

        /*
         * This code assumes that data has to be loaded regardless of the last modified date.
         * The caller of this code must take care of timeStamp comparisons before invoking this code.
         */
        private LoadResult loadDelimitedFile() throws IOException, DataReadException {
            LoadResult loadResult = new LoadResult();
            Map<KEY, List<VALUE>> dataMap = new HashMap<KEY, List<VALUE>>();
            BufferedReader bufferedReader = getStore().getBufferedReader();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                try {
                    AbstractDelimitedDataRecord<KEY, VALUE> record = getDataParser().getDataRecord(line);
                    KEY key = record.getKey();
                    VALUE value = record.getValue();

                    if (dataMap.get(key) != null) {
                        dataMap.get(key).add(value);
                    } else {
                        ArrayList<VALUE> values = new ArrayList<VALUE>();
                        values.add(value);
                        dataMap.put(key, values);
                    }
                    loadResult.incSuccessFulLines();
                } catch (UnsupportedEncodingException uee) {
                    loadResult.incFailedLines();
                } catch (IOException e) {
                    loadResult.incFailedLines();
                } catch (NumberFormatException nfe) {
                    loadResult.incFailedLines();
                } catch (ArrayIndexOutOfBoundsException e) {
                    loadResult.incFailedLines();
                } catch (IllegalArgumentException iae) {
                    loadResult.incFailedLines();
                } catch (NullPointerException npe) {
                    loadResult.incFailedLines();
                }
            }
            getCache().loadData(dataMap);
            loadResult.setTotalSize(dataMap.size());

            // Note.If we made it through here, we are sure that bufferedreader is not null.
            // This can throw an IO Exception too. Handled in the caller's code
            bufferedReader.close();

            return loadResult;
        }

        public void run() {
            //Incase of any failure in reading data from source, retry after 1 min for 30 times
            final int RETRY_INTERVAL_SECS = 60;
            final int MAX_RETRIES = 30;
            int numRetries = 0;

            while (numRetries < MAX_RETRIES) {
                LoadResult loadResult = null;
                long start = System.currentTimeMillis();
                Timer.Context loadTimeContext = getLoadTimerContext();
                try {
                    loadResult = loadDelimitedFile();
                    getLogger().info(
                            "numProcessed = {}, successfulLines={}, failedLines={}, map size={}, time taken={} ms",
                            (loadResult.getSuccessFulLines() + loadResult.getFailedLines()),
                            loadResult.getSuccessFulLines(), loadResult.getFailedLines(),
                            loadResult.getTotalSize(), (System.currentTimeMillis()-start)
                    );
                    loadTimeContext.stop();
                    AbstractKeyValueDataLoader.this.setLoadCompleted(true);
                    getCache().setLastReadSourceTimestamp(getStore().getLastModifiedDate());
                    break;
                } catch (IOException e) {
                    numRetries++;
                    getLogger().error("Hit an Exception " + e.getMessage() + e.getCause() + "Retrying {} of {}", numRetries, MAX_RETRIES);
                } catch (DataReadException e) {
                    numRetries++;
                    getLogger().error("Hit an Exception " + e.getMessage() + e.getCause() + "Retrying {} of {}", numRetries, MAX_RETRIES);
                }
            }
        }
    }

    private static class LoadResult {
        private long totalSize;
        private long failedLines;
        private long successFulLines;

        public LoadResult() {
            this.failedLines = 0;
            this.successFulLines = 0;
        }

        public long getFailedLines() {
            return failedLines;
        }

        public long getSuccessFulLines() {
            return successFulLines;
        }

        public void incLinesProcessed() {
            this.failedLines++;
        }

        public void incFailedLines() {
            this.failedLines++;
        }

        public void incSuccessFulLines() {
            this.successFulLines++;
        }

        public long getTotalSize() {
            return totalSize;
        }

        public LoadResult setTotalSize(long totalSize) {
            this.totalSize = totalSize;
            return this;
        }
    }
}
