/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.TeamCompetitor;
import com.sportradar.unifiedodds.sdk.internal.caching.CompetitorCi;
import com.sportradar.unifiedodds.sdk.internal.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventCi;
import com.sportradar.unifiedodds.sdk.internal.impl.SportEntityFactory;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;

/**
 * Represents a competing team
 */
@SuppressWarnings({ "ParameterNumber", "UnnecessaryParentheses" })
public class TeamCompetitorImpl extends CompetitorImpl implements TeamCompetitor {

    /**
     * Initializes a new instance of the {@link TeamCompetitorImpl} class
     *
     * @param competitorId the associated competitor id
     * @param profileCache the cache instance used to retrieve the cached data
     * @param qualifier the associated team qualifier
     * @param isVirtual indication if the competitor is marked as virtual
     * @param parentSportEventCi the {@link SportEventCi} this {@link CompetitorCi} belongs to
     * @param locales a {@link List} in which is provided the {@link CompetitorCi}
     * @param sportEntityFactory the factory used to create additional entities
     * @param exceptionHandlingStrategy the exception handling strategy
     */
    public TeamCompetitorImpl(
        Urn competitorId,
        ProfileCache profileCache,
        String qualifier,
        Integer division,
        Boolean isVirtual,
        SportEventCi parentSportEventCi,
        List<Locale> locales,
        SportEntityFactory sportEntityFactory,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        super(
            competitorId,
            profileCache,
            parentSportEventCi,
            locales,
            sportEntityFactory,
            exceptionHandlingStrategy,
            isVirtual
        );
        TeamQualifier = qualifier;
        TeamDivision = division;
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
        return (
            "TeamCompetitorImpl{" +
            "qualifier='" +
            TeamQualifier +
            '\'' +
            "division='" +
            TeamDivision +
            '\'' +
            "} " +
            super.toString()
        );
    }
}
