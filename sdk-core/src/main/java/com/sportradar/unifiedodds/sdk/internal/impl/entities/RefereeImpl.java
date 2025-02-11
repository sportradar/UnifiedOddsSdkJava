/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.entities.Referee;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.RefereeCi;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a sport event referee
 */
@SuppressWarnings({ "UnnecessaryParentheses" })
public class RefereeImpl implements Referee {

    /**
     *  A value used to uniquely identify the current {@link Referee} instance
     */
    private final Urn id;

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
     * @param refereeCi - a {@link RefereeCi} used to create a new instance
     * @param locales - a {@link List} of locales supported by the new instance
     */
    RefereeImpl(RefereeCi refereeCi, List<Locale> locales) {
        this.id = refereeCi.getId();
        this.name = refereeCi.getName();
        this.nationalities =
            locales
                .stream()
                .filter(l -> refereeCi.getNationality(l) != null)
                .collect(ImmutableMap.toImmutableMap(k -> k, refereeCi::getNationality));
    }

    /**
     * Returns the unique identifier of the current {@link Referee} instance
     *
     * @return - the unique identifier of the current {@link Referee} instance
     */
    @Override
    public Urn getId() {
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
        return (
            "RefereeImpl{" + "id=" + id + ", name='" + name + '\'' + ", nationalities=" + nationalities + '}'
        );
    }
}
