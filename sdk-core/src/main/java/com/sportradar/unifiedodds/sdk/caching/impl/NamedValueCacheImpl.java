/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.NamedValueCache;
import com.sportradar.unifiedodds.sdk.caching.ci.NamedValueCi;
import com.sportradar.unifiedodds.sdk.entities.NamedValue;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.SdkTaskScheduler;
import com.sportradar.unifiedodds.sdk.impl.entities.NamedValueImpl;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of the {@link NamedValueCache} used to cache {@link NamedValue} objects
 */
@SuppressWarnings({ "ConstantName", "ExplicitInitialization", "IllegalCatch", "LineLength", "MagicNumber" })
public class NamedValueCacheImpl implements NamedValueCache {

    /**
     * The {@link Logger} instance used to log {@link NamedValueCache} events
     */
    private static final Logger cacheLog = LoggerFactory.getLogger(NamedValueCacheImpl.class);

    /**
     * The {@link DataProvider} used to retrieve {@link NamedValue} objects
     */
    private final DataProvider dataProvider;

    /**
     * A {@link ConcurrentHashMap} that contains the retrieved {@link NamedValue} instances
     */
    private final ConcurrentHashMap<Integer, String> namedValues;

    /**
     * A value indicating whether the data was already fetched
     */
    private boolean dataFetched = false;

    /**
     * Initializes a new instance of {@link NamedValueCacheImpl}
     *
     * @param dataProvider - the {@link DataProvider} which is used to retrieve {@link NamedValue}
     * @param scheduler - the {@link SdkTaskScheduler} used to perform repeating cache tasks
     */
    public NamedValueCacheImpl(DataProvider dataProvider, SdkTaskScheduler scheduler) {
        Preconditions.checkNotNull(dataProvider);

        this.dataProvider = dataProvider;
        this.namedValues = new ConcurrentHashMap<>();

        scheduler.scheduleAtFixedRate("NamedValueRefreshTask", this::onTimerElapsed, 24, 24, TimeUnit.HOURS);
    }

    /**
     * Gets the {@link NamedValue} specified by the provided <code>id</code>
     *
     * @param id - the <code>id</code> of the {@link NamedValue} to retrieve.
     * @return - the {@link NamedValue} specified by the provided <code>id</code>
     */
    @Override
    public NamedValue getNamedValue(int id) {
        if (!dataFetched) {
            dataFetched = fetchAndMerge();
        }

        String description = namedValues.get(id);
        if (description == null) {
            return new NamedValueImpl(id);
        }

        return new NamedValueImpl(id, description);
    }

    /**
     * Determines if the specified <code>id</code> exists in the current cache instance
     *
     * @param id - the <code>id</code> that should be checked
     * @return <code>true</code> if the value is defined; otherwise <code>false</code>
     */
    @Override
    public boolean isValueDefined(int id) {
        if (!dataFetched) {
            dataFetched = fetchAndMerge();
        }

        return namedValues.containsKey(id);
    }

    /**
     * Fetches and merges {@link NamedValue} provided by the {@link this#dataProvider}
     *
     * @return - <code>true</code> if the operation was successful; otherwise false;
     */
    private synchronized boolean fetchAndMerge() {
        Object fetch;
        try {
            fetch = dataProvider.getData();
        } catch (DataProviderException e) {
            cacheLog.warn("There was an error while fetching the namedValue cache list, ex:", e);
            return false;
        }

        List<NamedValueCi> namedValueCis = NamedValueCi.mapToNamedValuesCi(fetch);
        namedValueCis.forEach(fetchedVal -> namedValues.put(fetchedVal.getId(), fetchedVal.getDescription()));

        cacheLog.info("{} {} retrieved", namedValueCis.size(), fetch.getClass().getName());
        return true;
    }

    /**
     * Timer scheduled for every 24h to refresh named values
     */
    private synchronized void onTimerElapsed() {
        try {
            namedValues.clear();
            fetchAndMerge();
        } catch (Exception ex) { // so timer does not die
            cacheLog.warn(
                "An exception occurred while attempting to retrieve named values with the scheduled timer. [{}] Exception was: {}",
                dataProvider,
                ex
            );
        }
    }
}
