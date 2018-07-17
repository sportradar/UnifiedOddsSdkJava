/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.caching.ci.RefereeCI;
import com.sportradar.unifiedodds.sdk.entities.Referee;
import com.sportradar.utils.URN;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a sport event referee
 */
public class RefereeImpl implements Referee {
    /**
     *  A value used to uniquely identify the current {@link Referee} instance
     */
    private final URN id;

    /**
     * The name of the referee represented by the current {@link Referee} instance
     */
    private final String name;

    /**
     * An unmodifiable {@link Map} containing referee nationality in different languages
     * @see com.google.common.collect.ImmutableMap
     */
    private final Map<Locale, String> nationalities;


    /**
     * Initializes a new instance of {@link RefereeImpl} class
     *
     * @param refereeCI - a {@link RefereeCI} used to create a new instance
     * @param locales - a {@link List} of locales supported by the new instance
     */
    RefereeImpl(RefereeCI refereeCI, List<Locale> locales) {

        this.id = refereeCI.getId();
        this.name = refereeCI.getName();
        this.nationalities = ImmutableMap.copyOf(locales.stream()
                .filter(l -> refereeCI.getNationality(l) != null)
                .collect(Collectors.toMap(k -> k, refereeCI::getNationality)));
    }


    /**
     * Returns the unique identifier of the current {@link Referee} instance
     *
     * @return - the unique identifier of the current {@link Referee} instance
     */
    @Override
    public URN getId() {
        return id;
    }

    /**
     * Returns the name of the referee represented by the current {@link Referee} instance
     *
     * @return - the name of the referee represented by the current {@link Referee} instance
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the nationality in the requested locale
     *
     * @param locale - a {@link Locale} in which the nationality is requested
     * @return - the nationality in the requested locale
     */
    @Override
    public String getNationality(Locale locale) {
        return nationalities.get(locale);
    }

    /**
     * Returns an unmodifiable {@link Map} containing referee nationality in different languages
     * @see com.google.common.collect.ImmutableMap
     *
     * @return - an unmodifiable {@link Map} containing referee nationality in different languages
     */
    @Override
    public Map<Locale, String> getNationalities() {
        return nationalities;
    }

    /**
     * Returns a {@link String} describing the current {@link Referee} instance
     *
     * @return - a {@link String} describing the current {@link Referee} instance
     */
    @Override
    public String toString() {
        return "RefereeImpl{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nationalities=" + nationalities +
                '}';
    }
}
