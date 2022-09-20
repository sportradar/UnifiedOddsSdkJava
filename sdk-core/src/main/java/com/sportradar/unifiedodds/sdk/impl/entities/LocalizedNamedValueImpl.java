/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.LocalizedNamedValue;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of the {@link LocalizedNamedValue}
 */
public class LocalizedNamedValueImpl extends NamedValueImpl implements LocalizedNamedValue {
    /**
     * A {@link ConcurrentHashMap} containing the various description translations
     */
    private final ConcurrentHashMap<Locale, String> descriptions;

    /**
     * The default locale of the current instance
     */
    private final Locale defaultLocale;

    /**
     * Initializes a new instance of {@link LocalizedNamedValueImpl}
     *
     * @param id - the identifier
     * @param descriptions - a {@link ConcurrentHashMap} containing various description translations
     * @param defaultLocale - the {@link Locale} which is used as default
     */
    public LocalizedNamedValueImpl(int id, ConcurrentHashMap<Locale, String> descriptions, Locale defaultLocale) {
        super(id);

        Preconditions.checkArgument(id >= 0);

        this.descriptions = descriptions;
        this.defaultLocale = defaultLocale;
    }

    /**
     * Returns the current instance description in the default {@link Locale}
     *
     * @return - the current instance description if available; otherwise null
     */
    @Override
    public String getDescription() {
        return descriptions == null ? null : descriptions.getOrDefault(defaultLocale, null);
    }

    /**
     * Returns the current instance description in the requested {@link Locale}
     *
     * @param locale - the {@link Locale} in which the description should be provided
     * @return - the current instance description in the requested {@link Locale} if available;
     *           otherwise null
     */
    @Override
    public String getDescription(Locale locale) { return descriptions == null ? null : descriptions.getOrDefault(locale, null); }
}
