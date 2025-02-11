/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.entities.Category;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.entities.Tournament;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a sport category
 */
@SuppressWarnings({ "LineLength" })
public class CategoryImpl extends CategorySummaryImpl implements Category {

    /**
     * An unmodifiable {@link List} of tournaments which belong to the category represented by the current instance
     */
    private final List<SportEvent> tournaments;

    /**
     * Initializes a new instance of the {@link CategoryImpl}
     *
     * @param id - an {@link Urn} uniquely identifying the current {@link Category} instance
     * @param names - a {@link Map} containing translated category name
     * @param tournaments - a {@link List} of tournaments which belong to the current {@link Category} instance
     * @param countryCode - a {@link String} representation of a country code
     */
    public CategoryImpl(Urn id, Map<Locale, String> names, List<SportEvent> tournaments, String countryCode) {
        super(id, names, countryCode);
        Preconditions.checkNotNull(tournaments);

        this.tournaments = ImmutableList.copyOf(tournaments);
    }

    /**
     * Returns an unmodifiable {@link List} of tournaments which belong to the category represented by the current instance
     * (possible types: {@link com.sportradar.unifiedodds.sdk.entities.BasicTournament}, {@link Tournament}, {@link com.sportradar.unifiedodds.sdk.entities.Stage})
     *
     * @return - an unmodifiable {@link List} of tournaments which belong to the category represented by the current instance
     */
    @Override
    public List<SportEvent> getTournaments() {
        return tournaments;
    }

    /**
     * Returns a {@link String} describing the current {@link Category} instance
     *
     * @return - a {@link String} describing the current {@link Category} instance
     */
    @Override
    public String toString() {
        return "CategoryImpl{" + "tournaments=" + tournaments + "} " + super.toString();
    }
}
