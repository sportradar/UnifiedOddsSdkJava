/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sportradar.uf.sportsapi.datamodel.SAPIDelayedInfo;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Event delay info cache representation
 */
public class DelayedInfoCI {

    /**
     * The identifier of the delayed info
     */
    private final int id;

    /**
     * The cached delay descriptions in various locales
     */
    private final Map<Locale, String> descriptions;

    /**
     * A {@link Set} of cached {@link Locale}s
     */
    private final Set<Locale> cachedLocales;


    /**
     * Initializes a new delayed info CI
     *
     * @param delayedInfo the data from which the CI will be built
     * @param locale the {@link Locale} in which the data is provided
     */
    public DelayedInfoCI(SAPIDelayedInfo delayedInfo, Locale locale) {
        Preconditions.checkNotNull(delayedInfo);
        Preconditions.checkNotNull(locale);

        id = delayedInfo.getId();

        descriptions = Maps.newConcurrentMap();
        cachedLocales = Sets.newConcurrentHashSet();

        merge(delayedInfo, locale);
    }


    /**
     * Returns the delay info identifier
     *
     * @return the delay info identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the delay info description in the specified {@link Locale}
     *
     * @param locale the {@link Locale} in which the data should be provided
     * @return the delay info description in the specified {@link Locale}
     */
    public String getDescription(Locale locale) {
        return descriptions.get(locale);
    }

    /**
     * Check if the cache item contains the provided {@link Locale}s
     *
     * @param locales the {@link Locale}s which should be checked
     * @return <code>true</code> if all the provided {@link Locale}s are cached; otherwise <code>false</code>
     */
    public boolean hasTranslationsFor(List<Locale> locales) {
        Preconditions.checkNotNull(locales);

        return cachedLocales.containsAll(locales);
    }

    /**
     * Merges the provided data in the associated cache item
     *
     * @param delayedInfo the data which should be merged
     * @param locale the {@link Locale} in which the data is provided
     */
    public void merge(SAPIDelayedInfo delayedInfo, Locale locale) {
        Preconditions.checkNotNull(delayedInfo);
        Preconditions.checkNotNull(locale);

        if (delayedInfo.getDescription() != null) {
            descriptions.put(locale, delayedInfo.getDescription());
        }

        cachedLocales.add(locale);
    }
}
