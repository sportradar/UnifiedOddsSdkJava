/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCI;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventCI;
import com.sportradar.unifiedodds.sdk.entities.TeamCompetitor;
import com.sportradar.utils.URN;

import java.util.List;
import java.util.Locale;

/**
 * Represents a competing team
 */
public class TeamCompetitorImpl extends CompetitorImpl implements TeamCompetitor {
//    /**
//     * A qualifier additionally describing the competitor (e.g. home, away, ...)
//     */
//    private final String qualifier;

    /**
     * Initializes a new instance of the {@link TeamCompetitorImpl} class
     *
     * @param competitorId the associated competitor id
     * @param profileCache the cache instance used to retrieve the cached data
     * @param qualifier the associated team qualifier
     * @param parentSportEventCI the {@link SportEventCI} this {@link CompetitorCI} belongs to
     * @param locales a {@link List} in which is provided the {@link CompetitorCI}
     * @param sportEntityFactory the factory used to create additional entities
     * @param exceptionHandlingStrategy the exception handling strategy
     */
    public TeamCompetitorImpl(URN competitorId,
                              ProfileCache profileCache,
                              String qualifier,
                              SportEventCI parentSportEventCI,
                              List<Locale> locales,
                              SportEntityFactory sportEntityFactory,
                              ExceptionHandlingStrategy exceptionHandlingStrategy) {
        super(competitorId, profileCache, parentSportEventCI, locales, sportEntityFactory, exceptionHandlingStrategy);

        TeamQualifier = qualifier;
    }

    /**
     * Returns the qualifier additionally describing the competitor (e.g. home, away, ...)
     *
     * @return - the qualifier additionally describing the competitor (e.g. home, away, ...)
     */
    @Override
    public String getQualifier() {
        FetchEventCompetitorsQualifiers();
        return TeamQualifier;
    }

    /**
     * Returns a {@link String} describing the current {@link TeamCompetitor} instance
     *
     * @return - a {@link String} describing the current {@link TeamCompetitor} instance
     */
    @Override
    public String toString() {
        return "TeamCompetitorImpl{" +
                "qualifier='" + TeamQualifier + '\'' +
                "} " + super.toString();
    }
}
