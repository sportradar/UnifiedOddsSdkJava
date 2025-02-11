/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.SapiManager;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableManagerCi;
import com.sportradar.utils.Urn;
import java.util.*;

/**
 * A cache representation of a competitor manager
 */
public class ManagerCi {

    /**
     * The manager identifier
     */
    private final Urn id;

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
     * Initializes as new {@link ManagerCi} instance
     *
     * @param manager the API schema object from which the instance will be created
     * @param locale the {@link Locale} in which the API object is translated
     */
    public ManagerCi(SapiManager manager, Locale locale) {
        Preconditions.checkNotNull(manager);
        Preconditions.checkNotNull(locale);

        id = Urn.parse(manager.getId());

        merge(manager, locale);
    }

    public ManagerCi(ExportableManagerCi exportable) {
        Preconditions.checkNotNull(exportable);

        id = Urn.parse(exportable.getId());
        names.putAll(exportable.getNames());
        nationalities.putAll(exportable.getNationalities());
        countryCode = exportable.getCountryCode();
        cachedLocales.addAll(exportable.getCachedLocales());
    }

    /**
     * Returns the manager identifier
     *
     * @return the manager identifier
     */
    public Urn getId() {
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

    public Map<Locale, String> getNames() {
        return ImmutableMap.copyOf(names);
    }

    /**
     * Returns the translated nationality
     *
     * @param locale the locale in which the nationality should be provided
     * @return the translated nationality
     */
    public String getNationality(Locale locale) {
        return nationalities.get(locale);
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
    public void merge(SapiManager manager, Locale locale) {
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

    public ExportableManagerCi export() {
        return new ExportableManagerCi(
            id.toString(),
            new HashMap<>(names),
            new HashMap<>(nationalities),
            countryCode,
            new ArrayList<>(cachedLocales)
        );
    }
}
