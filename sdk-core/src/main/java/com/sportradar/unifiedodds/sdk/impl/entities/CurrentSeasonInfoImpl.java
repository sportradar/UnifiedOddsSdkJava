/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.TournamentCI;
import com.sportradar.unifiedodds.sdk.caching.ci.GroupCI;
import com.sportradar.unifiedodds.sdk.caching.ci.SeasonCI;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.StreamWrapperException;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides information about a tournament season
 */
public class CurrentSeasonInfoImpl implements CurrentSeasonInfo {
    private final static Logger logger = LoggerFactory.getLogger(CurrentSeasonInfoImpl.class);

    /**
     * An {@link URN} uniquely identifying the current season
     */
    private final URN id;

    /**
     * An unmodifiable {@link Map} containing names of the season in different languages
     * @see com.google.common.collect.ImmutableMap
     */
    private final Map<Locale, String> names;

    /**
     * A representation of the current season year
     */
    private final String year;

    /**
     * A {@link Date} specifying the start date of the season
     */
    private final Date startDate;

    /**
     * The {@link Date} specifying the end date of the season
     */
    private final Date endDate;

    /**
     * A cache item representing the season endpoint
     */
    private final TournamentCI seasonEndpointCI;

    /**
     * The cache used to retrieve related event data
     */
    private final SportEventCache sportEventCache;

    /**
     * A {@link SportEntityFactory} instance used to build additional entities
     */
    private final SportEntityFactory sportEntityFactory;

    /**
     * The locales in which the data is available
     */
    private final List<Locale> locales;

    /**
     * The exception handling policy
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * Initializes a new intance of the {@link CurrentSeasonInfoImpl}
     *
     * @param currentSeasonCi a {@link TournamentCI} used to build the instance
     * @param seasonEndpointCI the associated season endpoint cache representation
     * @param sportEventCache the cache used to retrieve related event data
     * @param sportEntityFactory a {@link SportEntityFactory} instance used to build additional entities
     * @param locales a {@link List} of supported locales
     * @param exceptionHandlingStrategy - the exception handling policy
     */
    CurrentSeasonInfoImpl(SeasonCI currentSeasonCi,
                          TournamentCI seasonEndpointCI,
                          SportEventCache sportEventCache,
                          SportEntityFactory sportEntityFactory,
                          List<Locale> locales,
                          ExceptionHandlingStrategy exceptionHandlingStrategy) {
        Preconditions.checkNotNull(currentSeasonCi);
        Preconditions.checkNotNull(seasonEndpointCI);
        Preconditions.checkNotNull(sportEventCache);
        Preconditions.checkNotNull(sportEntityFactory);
        Preconditions.checkNotNull(locales);
        Preconditions.checkNotNull(exceptionHandlingStrategy);
        Preconditions.checkArgument(!locales.isEmpty());

        this.id = currentSeasonCi.getId();
        this.year = currentSeasonCi.getYear();
        this.startDate = currentSeasonCi.getStartDate();
        this.endDate = currentSeasonCi.getEndDate();
        this.names = ImmutableMap.copyOf(locales.stream()
                .filter(l -> currentSeasonCi.getName(l) != null)
                .collect(Collectors.toMap(k -> k, currentSeasonCi::getName)));

        this.seasonEndpointCI = seasonEndpointCI;

        this.locales = locales;
        this.sportEventCache = sportEventCache;
        this.sportEntityFactory = sportEntityFactory;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    /**
     * Returns the {@link URN} uniquely identifying the current season
     *
     * @return - the {@link URN} uniquely identifying the current season
     */
    @Override
    public URN getId() {
        return id;
    }

    /**
     * Returns the name of the season in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned name
     * @return - the name of the season in the specified language
     */
    @Override
    public String getName(Locale locale) {
        return names.get(locale);
    }

    /**
     * Returns the {@link String} representation the year of the season
     *
     * @return - the {@link String} representation the year of the season
     */
    @Override
    public String getYear() {
        return year;
    }

    /**
     * Returns the {@link Date} specifying the start date of the season
     *
     * @return - the {@link Date} specifying the start date of the season
     */
    @Override
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Returns the {@link Date} specifying the end date of the season
     *
     * @return - the {@link Date} specifying the end date of the season
     */
    @Override
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Returns a {@link SeasonCoverage} instance containing information about the available
     * coverage for the associated season
     *
     * @return - a {@link SeasonCoverage} instance containing information about the available coverage
     */
    @Override
    public SeasonCoverage getCoverage() {
        return seasonEndpointCI.getSeasonCoverage() == null ? null : new SeasonCoverageImpl(seasonEndpointCI.getSeasonCoverage());
    }

    /**
     * Returns a {@link List} of groups associated with the associated season
     *
     * @return - a {@link List} of groups associated with the associated season
     */
    @Override
    public List<Group> getGroups() {
        List<GroupCI> groups = seasonEndpointCI.getGroups(locales);

        return groups == null ? null :
                groups.stream().map(g -> new GroupImpl(g, locales, sportEntityFactory, exceptionHandlingStrategy)).collect(Collectors.toList());
    }

    /**
     * Returns a {@link Round} instance specifying the associated season round
     *
     * @return - a {@link Round} instance specifying the associated season round
     */
    @Override
    public Round getCurrentRound() {
        return seasonEndpointCI.getRound(locales) == null ? null : new RoundImpl(seasonEndpointCI.getRound(locales), locales);
    }

    /**
     * Returns a {@link List} of competitors that participate in the sport event
     * associated with the current instance
     *
     * @return - a {@link List} of competitors that participate in the sport event
     * associated with the current instance
     */
    @Override
    public List<Competitor> getCompetitors() {
        try {
            return seasonEndpointCI.getCompetitorIds(locales) == null ? null :
                    sportEntityFactory.buildStreamCompetitors(seasonEndpointCI.getCompetitorIds(locales), seasonEndpointCI.getCompetitorsReferences(), locales);
        } catch (StreamWrapperException e) {
            handleException("getCompetitors failure", e);
            return null;
        }
    }

    /**
     * Returns a {@link List} of events that belong to the associated season
     *
     * @return - a {@link List} of events that belong to the associated season
     */
    @Override
    public List<Competition> getSchedule() {
        List<URN> eventIds;
        try {
            eventIds = sportEventCache.getEventIds(id);
        } catch (IllegalCacheStateException e) {
            return handleException("getSchedule failure", e);
        }

        if (eventIds == null || eventIds.size() == 0) {
            return null;
        }

        try {
            return sportEntityFactory.buildSportEvents(eventIds, locales);
        } catch (ObjectNotFoundException e) {
            return handleException(e.getMessage(), e);
        }
    }

    /**
     * Method used to throw or return null value based on the SDK configuration
     *
     * @param request the requested object method
     * @param e the actual exception
     * @param <T> the expected return type - always null, if at all
     * @return if the SDK is set to the return null values instead of throwing errors, null
     */
    private <T> T handleException(String request, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (e == null) {
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException(this.getClass() + "[" + id + "], request(" + request + ")");
            } else {
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error executing {}[{}] request({}), returning null", this.getClass(), id, request);
            } else {
                logger.warn("Error executing {}[{}] request({}), returning null", this.getClass(), id, request, e);
            }
            return null;
        }
    }

    /**
     * Returns a {@link String} describing the current {@link CurrentSeasonInfo} instance
     *
     * @return - a {@link String} describing the current {@link CurrentSeasonInfo} instance
     */
    @Override
    public String toString() {
        return "CurrentSeasonInfoImpl{" +
                "id=" + id +
                ", names=" + names +
                ", year='" + year + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
