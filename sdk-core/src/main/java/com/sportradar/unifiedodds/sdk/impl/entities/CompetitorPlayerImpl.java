/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.entities.CompetitorPlayer;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;

/**
 * Represents competitor's player profile information
 */
public class CompetitorPlayerImpl extends PlayerProfileImpl implements CompetitorPlayer {

    private final Integer jerseyNumber;

    /**
     * Initializes a new instance of {@link PlayerProfileImpl}
     *
     * @param playerId                        the associated player identifier
     * @param profileCache                    the cache used to provide the data
     * @param possibleAssociatedCompetitorIds a list of possible associated competitor ids (used to prefetch data)
     * @param locales                         the {@link Locale}s in which the data should be available
     * @param exceptionHandlingStrategy       the preferred exception handling strategy
     */
    public CompetitorPlayerImpl(
        Urn playerId,
        Integer jerseyNumber,
        ProfileCache profileCache,
        List<Urn> possibleAssociatedCompetitorIds,
        List<Locale> locales,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        super(playerId, profileCache, possibleAssociatedCompetitorIds, locales, exceptionHandlingStrategy);
        this.jerseyNumber = jerseyNumber;
    }

    @Override
    public Integer getJerseyNumber() {
        return jerseyNumber;
    }
}
