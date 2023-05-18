/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.utils.URN;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Contains basic information about a sport tournament
 */
public class CategoryData extends SportEntityData {

    /**
     * A {@link List} representing the tournaments, which belong to category
     * represented by the current instance
     */
    private final List<URN> tournaments;

    /**
     * A {@link String} representation of a country code
     */
    private final String countryCode;

    /**
     * Initializes a new instance of the {@link CategoryData} class
     *
     * @param id - a {@link URN} specifying the id of the associated category
     * @param names - a {@link Map} containing translated entity name
     * @param tournaments - a {@link List} representing the tournaments, which belong to the category
     * @param countryCode - a {@link String} representation of a country code
     */
    CategoryData(URN id, Map<Locale, String> names, List<URN> tournaments, String countryCode) {
        super(id, names);
        Preconditions.checkNotNull(tournaments);

        this.tournaments = ImmutableList.copyOf(tournaments);
        this.countryCode = countryCode;
    }

    /**
     * Returns a {@link List} representing the tournaments, which belong to category
     *
     * @return - a {@link List} representing the tournaments, which belong to category
     */
    public List<URN> getTournaments() {
        return tournaments;
    }

    /**
     * Returns a {@link String} representation of a country code
     *
     * @return - a {@link String} representation of a country code
     */
    public String getCountryCode() {
        return countryCode;
    }
}
