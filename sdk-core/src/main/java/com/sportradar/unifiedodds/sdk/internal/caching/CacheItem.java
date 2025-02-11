/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching;

import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Base interface of cached entities
 */
@SuppressWarnings({ "LineLength" })
public interface CacheItem {
    /**
     * Returns the {@link Urn} representing id of the related entity
     *
     * @return the {@link Urn} representing id of the related entity
     */
    Urn getId();

    /**
     * Returns the {@link Map} containing translated names of the item
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated names of the item
     */
    Map<Locale, String> getNames(List<Locale> locales);

    /**
     * Determines whether the current instance has translations for the specified languages
     *
     * @param localeList a {@link List} specifying the required languages
     * @return <code>true</code> if the current instance contains data in the required locals, otherwise <code>false</code>.
     */
    boolean hasTranslationsLoadedFor(List<Locale> localeList);

    <T> void merge(T endpointData, Locale dataLocale);
}
