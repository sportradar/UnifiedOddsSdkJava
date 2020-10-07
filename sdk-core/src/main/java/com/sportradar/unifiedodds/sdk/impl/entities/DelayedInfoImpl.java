/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.caching.ci.DelayedInfoCI;
import com.sportradar.unifiedodds.sdk.entities.DelayedInfo;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A basic event delay info implementation
 */
class DelayedInfoImpl implements DelayedInfo {
    private final int id;
    private final Map<Locale, String> descriptions;

    /**
     * Initializes a new {@link DelayedInfoImpl} instance
     *
     * @param delayedInfo the cache item used to build the instance
     * @param locales the locales in which the data is provided
     */
    DelayedInfoImpl(DelayedInfoCI delayedInfo, List<Locale> locales) {
        Preconditions.checkNotNull(delayedInfo);
        Preconditions.checkNotNull(locales);

        id = delayedInfo.getId();

        this.descriptions = locales.stream()
                .filter(l -> delayedInfo.getDescription(l) != null)
                .collect(ImmutableMap.toImmutableMap(k -> k, delayedInfo::getDescription));
    }

    /**
     * Returns the delay info identifier
     *
     * @return the delay info identifier
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Returns the delay info description in the specified {@link Locale}
     *
     * @param locale the {@link Locale} in which the data should be provided
     * @return the delay info description in the specified {@link Locale}
     */
    @Override
    public String getDescription(Locale locale) {
        Preconditions.checkNotNull(locale);

        return descriptions.get(locale);
    }
}
