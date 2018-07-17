/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.SAPIManager;
import com.sportradar.utils.URN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * A cache representation of a competitor manager
 */
public class ManagerCI {

    /**
     * The manager identifier
     */
    private final URN id;

    /**
     * A {@link Map} of translated manager names
     */
    private final Map<Locale, String> names = Maps.newConcurrentMap();

    /**
     * A {@link Map} of translated nationality names
     */
    private final Map<Locale, String> nationalities = Maps.newConcurrentMap();

    /**
     * The manager country code
     */
    private String countryCode;

    /**
     * A {@link List} of cached {@link Locale}s
     */
    private List<Locale> cachedLocales = Collections.synchronizedList(new ArrayList<>());


    /**
     * Initializes as new {@link ManagerCI} instance
     *
     * @param manager the API schema object from which the instance will be created
     * @param locale the {@link Locale} in which the API object is translated
     */
    public ManagerCI(SAPIManager manager, Locale locale) {
        Preconditions.checkNotNull(manager);
        Preconditions.checkNotNull(locale);

        id = URN.parse(manager.getId());

        merge(manager, locale);
    }


    /**
     * Returns the manager identifier
     *
     * @return the manager identifier
     */
    public URN getId() {
        return id;
    }

    /**
     * Returns the translated manager name
     *
     * @param locale the locale in which the name should be provided
     * @return the translated manager name
     */
    public String getName(Locale locale) {
        return names.get(locale);
    }

    /**
     * Returns the translated nationality
     *
     * @param locale the locale in which the nationality should be provided
     * @return the translated nationality
     */
    public String getNationality(Locale locale) {
        return names.get(locale);
    }

    /**
     * Returns the country code
     *
     * @return the country code
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Merges the provided data into the current instance
     *
     * @param manager the API object to be merged
     * @param locale the locale in which the provided object is translated
     */
    public void merge(SAPIManager manager, Locale locale) {
        Preconditions.checkNotNull(manager);
        Preconditions.checkNotNull(locale);

        Optional.ofNullable(manager.getName()).ifPresent(n -> names.put(locale, n));
        Optional.ofNullable(manager.getNationality()).ifPresent(n -> nationalities.put(locale, n));
        countryCode = manager.getCountryCode();

        cachedLocales.add(locale);
    }

    /**
     * Checks if the provided locales are internally cached
     *
     * @param locales the locales to be checked
     * @return <code>true</code> if the locales are cached, otherwise <code>false</code>
     */
    public boolean hasTranslationsFor(List<Locale> locales) {
        Preconditions.checkNotNull(locales);

        return cachedLocales.containsAll(locales);
    }
}
