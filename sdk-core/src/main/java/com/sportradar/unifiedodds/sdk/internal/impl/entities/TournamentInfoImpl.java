/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.CategorySummary;
import com.sportradar.unifiedodds.sdk.entities.CurrentSeasonInfo;
import com.sportradar.unifiedodds.sdk.entities.TournamentInfo;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventCi;
import com.sportradar.unifiedodds.sdk.internal.caching.TournamentCi;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.SeasonCi;
import com.sportradar.unifiedodds.sdk.internal.exceptions.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.impl.SportEntityFactory;
import com.sportradar.utils.Urn;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides methods used to access tournament properties
 */
@SuppressWarnings({ "ClassFanOutComplexity", "ConstantName" })
class TournamentInfoImpl implements TournamentInfo {

    private static final Logger logger = LoggerFactory.getLogger(TournamentInfoImpl.class);
    private final TournamentCi tournament;
    private final SportEventCache sportEventCache;
    private final SportEntityFactory sportEntityFactory;
    private final List<Locale> locales;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    TournamentInfoImpl(
        TournamentCi tournamentCi,
        SportEventCache sportEventCache,
        SportEntityFactory sportEntityFactory,
        List<Locale> locales,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        Preconditions.checkNotNull(tournamentCi);
        Preconditions.checkNotNull(sportEventCache);
        Preconditions.checkNotNull(sportEntityFactory);
        Preconditions.checkNotNull(locales);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.tournament = tournamentCi;
        this.sportEventCache = sportEventCache;
        this.sportEntityFactory = sportEntityFactory;
        this.locales = locales;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    /**
     * Returns the {@link Urn} uniquely identifying the current season
     *
     * @return - the {@link Urn} uniquely identifying the current season
     */
    @Override
    public Urn getId() {
        return tournament.getId();
    }

    /**
     * Returns the name of the season in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned name
     * @return - the name of the season in the specified language
     */
    @Override
    public String getName(Locale locale) {
        return tournament.getNames(locales) == null ? null : tournament.getNames(locales).get(locale);
    }

    @Override
    public Map<Locale, String> getNames() {
        return Optional.ofNullable(tournament.getNames(locales)).orElse(ImmutableMap.of());
    }

    /**
     * Returns a {@link CategorySummary} representing the category associated with the current instance
     *
     * @return - a {@link CategorySummary} representing the category associated with the current instance
     */
    @Override
    public CategorySummary getCategory() {
        if (tournament == null || tournament.getCategoryId() == null) {
            handleException("getCategory - missing category data", null);
            return null;
        }

        try {
            return sportEntityFactory.buildCategory(tournament.getCategoryId(), locales);
        } catch (com.sportradar.unifiedodds.sdk.internal.exceptions.ObjectNotFoundException e) {
            handleException("getCategory", null);
            return null;
        }
    }

    /**
     * Returns a {@link CurrentSeasonInfo} which contains data about the season in which the current instance
     * tournament is happening
     *
     * @return - a {@link CurrentSeasonInfo} which provides data about the season in
     * which the current instance tournament is happening
     */
    @Override
    public CurrentSeasonInfo getCurrentSeason() {
        SeasonCi currentSeason = tournament.getCurrentSeason(locales);
        if (currentSeason == null) {
            return null;
        }

        TournamentCi tournamentCi = null;
        try {
            SportEventCi eventCacheItem = sportEventCache.getEventCacheItem(currentSeason.getId());
            if (eventCacheItem instanceof TournamentCi) {
                tournamentCi = (TournamentCi) eventCacheItem;
            } else {
                handleException("tInfo.getCurrentSeason - invalid cache item type", null);
            }
        } catch (CacheItemNotFoundException e) {
            handleException("tInfo.getCurrentSeason - error providing season cache item", e);
        }

        if (tournamentCi == null) {
            return null;
        }

        return new CurrentSeasonInfoImpl(
            currentSeason,
            tournamentCi,
            sportEventCache,
            sportEntityFactory,
            locales,
            exceptionHandlingStrategy
        );
    }

    /**
     * Returns a {@link String} describing the current {@link TournamentInfoImpl} instance
     *
     * @return - a {@link String} describing the current {@link TournamentInfoImpl} instance
     */
    @Override
    public String toString() {
        return "TournamentInfoImpl{" + "tournamentId=" + tournament.getId() + ", locales=" + locales + '}';
    }

    /**
     * Method used to throw or return null value based on the SDK configuration
     *
     * @param request the requested object method
     * @param e the actual exception
     */
    private void handleException(String request, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (e == null) {
                throw new ObjectNotFoundException(
                    this.getClass() + "[" + tournament.getId() + "], request(" + request + ")"
                );
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn(
                    "tInfo - Error executing {}[{}] request({}), returning null",
                    this.getClass(),
                    tournament.getId(),
                    request
                );
            } else {
                logger.warn(
                    "tInfo - Error executing {}[{}] request({}), returning null",
                    this.getClass(),
                    tournament.getId(),
                    request,
                    e
                );
            }
        }
    }
}
