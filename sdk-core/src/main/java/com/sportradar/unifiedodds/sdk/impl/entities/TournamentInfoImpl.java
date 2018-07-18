/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.SportEventCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.TournamentCI;
import com.sportradar.unifiedodds.sdk.caching.ci.SeasonCI;
import com.sportradar.unifiedodds.sdk.entities.CategorySummary;
import com.sportradar.unifiedodds.sdk.entities.CurrentSeasonInfo;
import com.sportradar.unifiedodds.sdk.entities.TournamentInfo;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

/**
 * Provides methods used to access tournament properties
 */
class TournamentInfoImpl implements TournamentInfo {
    private final static Logger logger = LoggerFactory.getLogger(TournamentInfoImpl.class);
    private final TournamentCI tournament;
    private final SportEventCache sportEventCache;
    private final SportEntityFactory sportEntityFactory;
    private final List<Locale> locales;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;


    TournamentInfoImpl(TournamentCI tournamentCI, SportEventCache sportEventCache, SportEntityFactory sportEntityFactory, List<Locale> locales, ExceptionHandlingStrategy exceptionHandlingStrategy) {
        Preconditions.checkNotNull(tournamentCI);
        Preconditions.checkNotNull(sportEventCache);
        Preconditions.checkNotNull(sportEntityFactory);
        Preconditions.checkNotNull(locales);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.tournament = tournamentCI;
        this.sportEventCache = sportEventCache;
        this.sportEntityFactory = sportEntityFactory;
        this.locales = locales;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }


    /**
     * Returns the {@link URN} uniquely identifying the current season
     *
     * @return - the {@link URN} uniquely identifying the current season
     */
    @Override
    public URN getId() {
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
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
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
        SeasonCI currentSeason = tournament.getCurrentSeason(locales);
        if (currentSeason == null) {
            return null;
        }

        TournamentCI tournamentCI = null;
        try {
            SportEventCI eventCacheItem = sportEventCache.getEventCacheItem(currentSeason.getId());
            if (eventCacheItem instanceof TournamentCI) {
                tournamentCI = (TournamentCI) eventCacheItem;
            } else {
                handleException("tInfo.getCurrentSeason - invalid cache item type", null);
            }
        } catch (CacheItemNotFoundException e) {
            handleException("tInfo.getCurrentSeason - error providing season cache item", e);
        }

        if (tournamentCI == null) {
            return null;
        }

        return new CurrentSeasonInfoImpl(currentSeason, tournamentCI, sportEventCache, sportEntityFactory, locales, exceptionHandlingStrategy);
    }

    /**
     * Returns a {@link String} describing the current {@link TournamentInfoImpl} instance
     *
     * @return - a {@link String} describing the current {@link TournamentInfoImpl} instance
     */
    @Override
    public String toString() {
        return "TournamentInfoImpl{" +
                "tournamentId=" + tournament.getId() +
                ", locales=" + locales +
                '}';
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
                throw new ObjectNotFoundException(this.getClass() + "[" + tournament.getId() + "], request(" + request + ")");
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("tInfo - Error executing {}[{}] request({}), returning null", this.getClass(), tournament.getId(), request);
            } else {
                logger.warn("tInfo - Error executing {}[{}] request({}), returning null", this.getClass(), tournament.getId(), request, e);
            }
        }
    }
}
