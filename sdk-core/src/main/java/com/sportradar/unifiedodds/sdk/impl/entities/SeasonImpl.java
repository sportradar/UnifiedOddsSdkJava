/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventCi;
import com.sportradar.unifiedodds.sdk.caching.TournamentCi;
import com.sportradar.unifiedodds.sdk.caching.ci.SeasonCi;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.StreamWrapperException;
import com.sportradar.utils.Urn;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides methods used to access data of long term events of type season
 */
@SuppressWarnings(
    {
        "ClassFanOutComplexity",
        "ConstantName",
        "LineLength",
        "MultipleStringLiterals",
        "NPathComplexity",
        "ReturnCount",
    }
)
public class SeasonImpl extends SportEventImpl implements Season {

    private static final Logger logger = LoggerFactory.getLogger(SeasonImpl.class);

    /**
     * An indication on how should be the SDK exceptions handled
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * A {@link SportEventCache} instance used to retrieve sport events
     */
    private final SportEventCache sportEventCache;

    /**
     * A {@link SportEntityFactory} instance used to construct {@link Competition} and {@link Tournament} instances
     */
    private final SportEntityFactory sportEntityFactory;

    /**
     * A {@link List} of locales for this issue
     */
    private final List<Locale> locales;

    /**
     * Initializes a new {@link SeasonImpl} instance
     *
     * @param id an {@link Urn} uniquely identifying the season associated with the current instance
     * @param sportId an {@link Urn} identifying the sport to which the season belongs
     * @param locales a {@link List} of all languages for this instance
     * @param sportEventCache a {@link SportEventCache} instance used to retrieve sport events
     * @param sportEntityFactory a {@link SportEntityFactory} instance used to construct {@link Competition} instances
     * @param exceptionHandlingStrategy the desired exception handling strategy
     */
    public SeasonImpl(
        Urn id,
        Urn sportId,
        List<Locale> locales,
        SportEventCache sportEventCache,
        SportEntityFactory sportEntityFactory,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        super(id, sportId);
        Preconditions.checkNotNull(locales);
        Preconditions.checkNotNull(sportEventCache);
        Preconditions.checkNotNull(sportEntityFactory);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.locales = locales;
        this.sportEventCache = sportEventCache;
        this.sportEntityFactory = sportEntityFactory;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    /**
     * Returns the sport event name
     *
     * @param locale the {@link Locale} in which the name should be provided
     * @return the sport event name if available; otherwise null
     */
    @Override
    public String getName(Locale locale) {
        SeasonCi seasonCi = provideSeasonCi();

        return seasonCi != null ? seasonCi.getName(locale) : null;
    }

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled
     *
     * @return - a {@link Date} instance specifying when the sport event associated with the current
     * instance was scheduled
     */
    @Override
    public Date getScheduledTime() {
        SeasonCi seasonCi = provideSeasonCi();

        return seasonCi != null ? seasonCi.getStartDate() : null;
    }

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled to end
     *
     * @return - a {@link Date} instance specifying when the sport event associated with the current
     * instance was scheduled to end
     */
    @Override
    public Date getScheduledEndTime() {
        SeasonCi seasonCi = provideSeasonCi();

        return seasonCi != null ? seasonCi.getEndDate() : null;
    }

    /**
     * Returns the {@link Boolean} specifying if the start time to be determined is set for the current instance
     *
     * @return if available, the {@link Boolean} specifying if the start time to be determined is set for the current instance
     */
    @Override
    public Boolean isStartTimeTbd() {
        return null;
    }

    /**
     * Returns the {@link Urn} specifying the replacement sport event for the current instance
     *
     * @return if available, the {@link Urn} specifying the replacement sport event for the current instance
     */
    @Override
    public Urn getReplacedBy() {
        return null;
    }

    /**
     * Returns a {@link SeasonCoverage} instance containing information about the available
     * coverage for the associated season
     *
     * @return - a {@link SeasonCoverage} instance containing information about the available coverage
     */
    @Override
    public SeasonCoverage getSeasonCoverage() {
        TournamentCi seasonEndpointCi = loadSeasonEndpointCi();

        if (seasonEndpointCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        return seasonEndpointCi.getSeasonCoverage() == null
            ? null
            : new SeasonCoverageImpl(seasonEndpointCi.getSeasonCoverage());
    }

    /**
     * Returns a {@link List} of groups associated with the associated season
     *
     * @return - a {@link List} of groups associated with the associated season
     */
    @Override
    public List<Group> getGroups() {
        TournamentCi seasonEndpointCi = loadSeasonEndpointCi();

        if (seasonEndpointCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        return seasonEndpointCi.getGroups(locales) == null
            ? null
            : seasonEndpointCi
                .getGroups(locales)
                .stream()
                .map(g -> new GroupImpl(g, locales, sportEntityFactory, exceptionHandlingStrategy))
                .collect(Collectors.toList());
    }

    /**
     * Returns a {@link List} of events that belong to the associated season
     *
     * @return - a {@link List} of events that belong to the associated season
     */
    @Override
    public List<Competition> getSchedule() {
        List<Urn> eventIds = null;
        try {
            for (Locale l : locales) {
                eventIds = sportEventCache.getEventIds(id, l);
            }
        } catch (IllegalCacheStateException e) {
            handleException("getSchedule failure", e);
        }

        if (eventIds == null || eventIds.size() == 0) {
            return null;
        }

        try {
            return sportEntityFactory.buildSportEvents(eventIds, locales);
        } catch (ObjectNotFoundException e) {
            handleException(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Returns a {@link Round} instance specifying the current season round
     *
     * @return - a {@link Round} instance specifying the current season round
     */
    @Override
    public Round getCurrentRound() {
        TournamentCi seasonEndpointCi = loadSeasonEndpointCi();

        if (seasonEndpointCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        return seasonEndpointCi.getRound(locales) == null
            ? null
            : new RoundImpl(seasonEndpointCi.getRound(locales), locales);
    }

    /**
     * Returns the {@link String} representation the year of the season
     *
     * @return - the {@link String} representation the year of the season
     */
    @Override
    public String getYear() {
        SeasonCi seasonCi = provideSeasonCi();

        return seasonCi == null ? null : seasonCi.getYear();
    }

    /**
     * Returns a {@link TournamentInfo} which contains data of the associated tournament
     *
     * @return a {@link TournamentInfo} which contains data of the associated season
     */
    @Override
    public TournamentInfo getTournamentInfo() {
        TournamentCi seasonEndpointCi = loadSeasonEndpointCi();

        if (seasonEndpointCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        SeasonCi season = seasonEndpointCi.getSeason(locales);
        if (season == null) {
            handleException("TournamentCI.getSeason missing", null);
            return null;
        }

        if (season.getTournamentId() == null) {
            handleException("TournamentCI.getSeason.getTournamentId missing", null);
            return null;
        }

        TournamentCi tournamentCi = null;
        try {
            SportEventCi eventCacheItem = sportEventCache.getEventCacheItem(season.getTournamentId());
            if (eventCacheItem instanceof TournamentCi) {
                tournamentCi = (TournamentCi) eventCacheItem;
            } else {
                handleException("getTournamentInfo - Invalid cache item type", null);
            }
        } catch (CacheItemNotFoundException e) {
            handleException("getTournamentInfo - Error providing tournament endpoint cache item", null);
            return null;
        }

        if (tournamentCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        return new TournamentInfoImpl(
            tournamentCi,
            sportEventCache,
            sportEntityFactory,
            locales,
            exceptionHandlingStrategy
        );
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
        TournamentCi seasonEndpointCi = loadSeasonEndpointCi();

        if (seasonEndpointCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        try {
            return seasonEndpointCi.getCompetitorIds(locales) == null
                ? null
                : sportEntityFactory.buildStreamCompetitors(
                    seasonEndpointCi.getCompetitorIds(locales),
                    seasonEndpointCi,
                    locales
                );
        } catch (StreamWrapperException e) {
            handleException("getCompetitors failure", e);
            return null;
        }
    }

    /**
     * Returns a {@link SportSummary} instance representing the sport associated with the current instance
     *
     * @return a {@link SportSummary} instance representing the sport associated with the current instance
     */
    @Override
    public SportSummary getSport() {
        TournamentCi seasonEndpointCi = loadSeasonEndpointCi();

        if (seasonEndpointCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        if (seasonEndpointCi.getCategoryId() == null) {
            handleException("getSport - missing category data", null);
            return null;
        }

        try {
            return sportEntityFactory.buildSportForCategory(seasonEndpointCi.getCategoryId(), locales);
        } catch (ObjectNotFoundException e) {
            handleException("getSport", e);
            return null;
        }
    }

    /**
     * Returns a {@link TournamentCoverage} instance which describes the associated tournament coverage information
     *
     * @return a {@link TournamentCoverage} instance describing the tournament coverage information
     */
    @Override
    public TournamentCoverage getTournamentCoverage() {
        TournamentCi seasonEndpointCi = loadSeasonEndpointCi();

        if (seasonEndpointCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        return seasonEndpointCi.getTournamentCoverage() == null
            ? null
            : new TournamentCoverageImpl(seasonEndpointCi.getTournamentCoverage());
    }

    /**
     * Returns the associated sport identifier
     * (This method its overridden because the superclass SportEvent does not contain the sportId in all cases)
     *
     * @return the unique sport identifier to which this event is associated
     */
    @Override
    public Urn getSportId() {
        if (super.getSportId() != null) {
            return super.getSportId();
        }

        TournamentCi tournamentCi = loadSeasonEndpointCi();

        if (tournamentCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        if (tournamentCi.getCategoryId() == null) {
            handleException("missing category data", null);
            return null;
        }

        try {
            SportSummary sportSummary = sportEntityFactory.buildSportForCategory(
                tournamentCi.getCategoryId(),
                locales
            );
            return sportSummary.getId();
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
            logger.info("Could not provide sport for category[{}], ex:", tournamentCi.getCategoryId(), e);
        }

        return null;
    }

    /**
     * Returns a {@link String} describing the current {@link Season} instance
     *
     * @return - a {@link String} describing the current {@link Season} instance
     */
    @Override
    public String toString() {
        return "SeasonImpl{" + "id=" + id + ", sportId=" + sportId + ", locales=" + locales + "}";
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
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException(
                    this.getClass() + "[" + id + "], request(" + request + ")"
                );
            } else {
                throw new com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn(
                    "Error executing {}[{}] request({}), returning null",
                    this.getClass(),
                    id,
                    request
                );
            } else {
                logger.warn(
                    "Error executing {}[{}] request({}), returning null",
                    this.getClass(),
                    id,
                    request,
                    e
                );
            }
        }
    }

    /**
     * Loads the associated entity cache item from the sport event cache
     *
     * @return the associated cache item
     */
    private TournamentCi loadSeasonEndpointCi() {
        try {
            SportEventCi eventCacheItem = sportEventCache.getEventCacheItem(id);
            if (eventCacheItem instanceof TournamentCi) {
                return (TournamentCi) eventCacheItem;
            }
            handleException("loadSeasonEndpointCI, CI type miss-match", null);
        } catch (CacheItemNotFoundException e) {
            handleException("loadSeasonEndpointCI, CI not found", e);
        }
        return null;
    }

    /**
     * Provides the associated {@link SeasonCi} item
     *
     * @return the associated {@link SeasonCi} item
     */
    private SeasonCi provideSeasonCi() {
        TournamentCi seasonEndpointCi = loadSeasonEndpointCi();

        if (seasonEndpointCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        return seasonEndpointCi.getSeason(locales);
    }
}
