/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.impl;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.LocalizedNamedValue;
import com.sportradar.unifiedodds.sdk.internal.caching.LocalizedNamedValueCache;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.NamedValueCi;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkTaskScheduler;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.LocalizedNamedValueImpl;
import com.sportradar.utils.SdkHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link LocalizedNamedValueCache} used to cache {@link LocalizedNamedValue} items
 */
@SuppressWarnings({ "ConstantName", "IllegalCatch", "LineLength", "MagicNumber", "ParameterAssignment" })
public class LocalizedNamedValueCacheImpl implements LocalizedNamedValueCache {

    /**
     * A {@link Logger} instance used to log {@link LocalizedNamedValueCache} entries
     */
    private static final Logger cacheLog = LoggerFactory.getLogger(LocalizedNamedValueCacheImpl.class);

    /**
     * A {@link DataProvider} which is used to get new data
     */
    private final DataProvider dataProvider;

    /**
     * A {@link List} of all supported {@link Locale}
     */
    private final List<Locale> defaultLocales;

    /**
     * A {@link ConcurrentHashMap} storing the translated values
     */
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Locale, String>> namedValues;

    /**
     * A {@link List } of {@link Locale} that are already fetched
     */
    private final List<Locale> fetchedLocales;

    /**
     * The {@link Object} used to synchronize the access
     */
    private final Object lock = new Object();

    /**
     * Initializes a new instance of {@link LocalizedNamedValueCacheImpl}
     *
     * @param dataProvider - a {@link DataProvider} that will be used to get new data
     * @param scheduler - the {@link SdkTaskScheduler} used to perform repeating cache tasks
     * @param defaultLocales - a {@link List} of all supported {@link Locale}
     */
    public LocalizedNamedValueCacheImpl(
        DataProvider dataProvider,
        SdkTaskScheduler scheduler,
        List<Locale> defaultLocales
    ) {
        Preconditions.checkNotNull(dataProvider);
        Preconditions.checkNotNull(defaultLocales);

        this.dataProvider = dataProvider;
        this.defaultLocales = defaultLocales;

        this.namedValues = new ConcurrentHashMap<>();
        this.fetchedLocales = Collections.synchronizedList(new ArrayList<>());

        scheduler.scheduleAtFixedRate(
            "LocalizedNamedValueRefreshTask",
            this::onTimerElapsed,
            24,
            24,
            TimeUnit.HOURS
        );
    }

    /**
     * Gets a {@link LocalizedNamedValue} with the specified translations
     *
     * @param id - the identifier of the localized value
     * @param locales - a {@link List} of {@link Locale} in which the data is required
     * @return - a {@link LocalizedNamedValue} with the specified translations
     */
    @Override
    public LocalizedNamedValue get(int id, List<Locale> locales) {
        Preconditions.checkArgument(id >= 0);

        if (locales == null || locales.size() == 0) {
            locales = defaultLocales;
        }

        ConcurrentHashMap<Locale, String> cachedTranslations;
        synchronized (lock) {
            List<Locale> missingLocales = SdkHelper.findMissingLocales(fetchedLocales, locales);

            if (!missingLocales.isEmpty()) {
                getInternal(missingLocales);
            }

            cachedTranslations = namedValues.get(id);
        }

        if (cachedTranslations == null) {
            return new LocalizedNamedValueImpl(id, null, null);
        }

        return new LocalizedNamedValueImpl(
            id,
            cachedTranslations,
            locales.stream().findFirst().orElse(defaultLocales.get(0))
        );
    }

    /**
     * Determines if the specified identifier exists in the current instance
     *
     * @param id - the identifier to check
     * @return - <code>true</code> if the value exists; otherwise <code>false</code>
     */
    @Override
    public boolean isValueDefined(int id) {
        boolean exists;
        synchronized (lock) {
            if (fetchedLocales.isEmpty()) {
                getInternal(
                    Collections.singletonList(defaultLocales.stream().findFirst().orElse(Locale.ENGLISH))
                );
            }
            exists = namedValues.containsKey(id);
        }
        return exists;
    }

    /**
     * Performs several calls of the {@link this#fetchAndMerge(Locale)}
     *
     * @param locales - a {@link List} of {@link Locale} in which the data should be retrieved
     */
    private void getInternal(List<Locale> locales) {
        try {
            locales.forEach(this::fetchAndMerge);
        } catch (Exception ex) {
            cacheLog.warn(
                "An exception occurred while attempting to retrieve named values. [{}] Exception:",
                dataProvider,
                ex
            );
        }
    }

    /**
     * Fetches localized values using the provided {@link DataProvider}, the fetched data is
     * than merged in the local cache
     *
     * @param locale - a {@link Locale} specifying the language in which the data should be fetched
     */
    private void fetchAndMerge(Locale locale) {
        Preconditions.checkNotNull(locale);

        Object fetch;
        try {
            fetch = dataProvider.getData(locale);
        } catch (DataProviderException e) {
            cacheLog.warn("Error fetching Localized named values [{}] Exception:", dataProvider, e);
            return;
        }

        List<NamedValueCi> namedValueCis = NamedValueCi.mapToNamedValuesCi(fetch);
        namedValueCis.forEach(fetchedVal -> {
            ConcurrentHashMap<Locale, String> storedData = namedValues.computeIfAbsent(
                fetchedVal.getId(),
                k -> new ConcurrentHashMap<>()
            );

            storedData.put(locale, fetchedVal.getDescription());
        });

        fetchedLocales.add(locale);

        cacheLog.info(
            "{} {} retrieved for locale {}",
            namedValueCis.size(),
            fetch.getClass().getName(),
            locale
        );
    }

    /**
     * Timer scheduled for every 24h to refresh named values
     */
    private synchronized void onTimerElapsed() {
        try {
            fetchedLocales.clear();
            namedValues.clear();
            defaultLocales.forEach(this::fetchAndMerge);
        } catch (Exception ex) {
            cacheLog.warn(
                "An exception occurred while attempting to retrieve localized named values with the scheduled timer. [{}] Exception:",
                dataProvider,
                ex
            );
        }
    }
}
