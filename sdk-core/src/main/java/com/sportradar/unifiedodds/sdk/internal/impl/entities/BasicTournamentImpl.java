/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import static java.util.Optional.ofNullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.caching.RequestOptions;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventCi;
import com.sportradar.unifiedodds.sdk.internal.caching.TournamentCi;
import com.sportradar.unifiedodds.sdk.internal.exceptions.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.exceptions.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.internal.exceptions.StreamWrapperException;
import com.sportradar.unifiedodds.sdk.internal.impl.SportEntityFactory;
import com.sportradar.utils.Urn;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a sport tournament
 */
@SuppressWarnings({ "ClassFanOutComplexity", "ConstantName", "LineLength", "MultipleStringLiterals" })
public class BasicTournamentImpl extends SportEventImpl implements BasicTournament, PreloadableEntity {

    private static final Logger logger = LoggerFactory.getLogger(BasicTournamentImpl.class);

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
     * Initializes a new {@link BasicTournamentImpl} instance
     *
     * @param id an {@link Urn} uniquely identifying the tournament associated with the current instance
     * @param sportId an {@link Urn} identifying the sport to which the tournament belongs
     * @param locales a {@link List} of all languages for this instance
     * @param sportEntityFactory a {@link SportEntityFactory} instance used to construct {@link Competition} instances
     * @param exceptionHandlingStrategy the desired exception handling strategy
     */
    public BasicTournamentImpl(
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
     * Returns the name of the current long term event translated to the specified language
     *
     * @param locale - a {@link Locale} specifying in which language the name should be returned
     * @return - the name of the current long term event translated to the specified language
     */
    @Override
    public String getName(Locale locale) {
        TournamentCi tournamentCi = loadBasicTournamentCi();

        if (tournamentCi == null) {
            handleException("tournamentCI missing", null);
            return null;
        }

        return tournamentCi.getNames(locales).get(locale);
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
        TournamentCi tournamentCi = loadBasicTournamentCi();

        if (tournamentCi == null) {
            handleException("tournamentCI missing", null);
            return null;
        }

        return tournamentCi.getScheduled();
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
        TournamentCi tournamentCi = loadBasicTournamentCi();

        if (tournamentCi == null) {
            handleException("tournamentCI missing", null);
            return null;
        }

        return tournamentCi.getScheduledEnd();
    }

    /**
     * Returns the {@link Boolean} specifying if the start time to be determined is set for the current instance
     *
     * @return if available, the {@link Boolean} specifying if the start time to be determined is set for the current instance
     */
    @SuppressWarnings("java:S2447") // Null should not be returned from a "Boolean" method
    @Override
    public Boolean isStartTimeTbd() {
        TournamentCi tournamentCi = loadBasicTournamentCi();

        if (tournamentCi == null) {
            handleException("tournamentCI missing", null);
            return null;
        }

        return tournamentCi.isStartTimeTbd().orElse(null);
    }

    /**
     * Returns the {@link Urn} specifying the replacement sport event for the current instance
     *
     * @return if available, the {@link Urn} specifying the replacement sport event for the current instance
     */
    @Override
    public Urn getReplacedBy() {
        TournamentCi tournamentCi = loadBasicTournamentCi();

        if (tournamentCi == null) {
            handleException("tournamentCI missing", null);
            return null;
        }

        return tournamentCi.getReplacedBy();
    }

    /**
     * Returns a {@link CategorySummary} representing the category associated with the current instance
     *
     * @return - a {@link CategorySummary} representing the category associated with the current instance
     */
    @Override
    public CategorySummary getCategory() {
        TournamentCi tournamentCi = loadBasicTournamentCi();

        if (tournamentCi == null || tournamentCi.getCategoryId() == null) {
            handleException("getCategory - missing category data", null);
            return null;
        }

        try {
            return sportEntityFactory.buildCategory(tournamentCi.getCategoryId(), locales);
        } catch (com.sportradar.unifiedodds.sdk.internal.exceptions.ObjectNotFoundException e) {
            handleException("getCategory", e);
        }

        return null;
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
        TournamentCi tournamentCi = loadBasicTournamentCi();

        if (tournamentCi == null) {
            handleException("tournamentCI missing", null);
            return null;
        }

        try {
            return tournamentCi.getCompetitorIds(locales) == null
                ? null
                : sportEntityFactory.buildStreamCompetitors(
                    tournamentCi.getCompetitorIds(locales),
                    tournamentCi,
                    locales
                );
        } catch (StreamWrapperException e) {
            handleException("getCompetitors failure", e);
            return null;
        }
    }

    /**
     * Returns the {@link Boolean} specifying if the tournament is exhibition game
     *
     * @return if available, the {@link Boolean} specifying if the tournament is exhibition game
     */
    @Override
    public Boolean isExhibitionGames() {
        TournamentCi tournamentCi = loadBasicTournamentCi();

        if (tournamentCi == null) {
            handleException("tournamentCI missing", null);
            return null;
        }

        return tournamentCi.isExhibitionGames();
    }

    /**
     * Returns a {@link SportSummary} instance representing the sport associated with the current instance
     *
     * @return a {@link SportSummary} instance representing the sport associated with the current instance
     */
    @Override
    public SportSummary getSport() {
        TournamentCi tournamentCi = loadBasicTournamentCi();

        if (tournamentCi == null) {
            handleException("tournamentCI missing", null);
            return null;
        }

        if (tournamentCi.getCategoryId() == null) {
            handleException("missing category data", null);
            return null;
        }

        try {
            return sportEntityFactory.buildSportForCategory(tournamentCi.getCategoryId(), locales);
        } catch (com.sportradar.unifiedodds.sdk.internal.exceptions.ObjectNotFoundException e) {
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
        TournamentCi tournamentCi = loadBasicTournamentCi();

        if (tournamentCi == null) {
            handleException("tournamentCI missing", null);
            return null;
        }

        return tournamentCi.getTournamentCoverage() == null
            ? null
            : new TournamentCoverageImpl(tournamentCi.getTournamentCoverage());
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

        // wrapper for the sport summary
        SportSummary sport = getSport();
        if (sport != null) {
            return sport.getId();
        }

        return null;
    }

    /**
     * Returns a {@link List} of events that belong to the associated tournament
     *
     * @return - a {@link List} of events that belong to the associated tournament
     */
    @Override
    public List<Competition> getSchedule() {
        List<Urn> eventIds = Lists.newArrayList();
        try {
            for (Locale l : locales) {
                eventIds = sportEventCache.getEventIds(id, l);
            }
        } catch (IllegalCacheStateException e) {
            handleException("getSchedule failure", e);
            return null;
        }

        if (eventIds == null || eventIds.size() == 0) {
            return null;
        }

        try {
            return sportEntityFactory.buildSportEvents(eventIds, locales);
        } catch (com.sportradar.unifiedodds.sdk.internal.exceptions.ObjectNotFoundException e) {
            handleException(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Returns a {@link String} describing the current {@link Tournament} instance
     *
     * @return - a {@link String} describing the current {@link Tournament} instance
     */
    @Override
    public String toString() {
        return "BasicTournamentImpl{" + "id=" + id + ", locales=" + locales + "}";
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
                throw new ObjectNotFoundException(this.getClass() + "[" + id + "], request(" + request + ")");
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn(
                    "Tournament - Error executing {}[{}] request({}), returning null",
                    this.getClass(),
                    id,
                    request
                );
            } else {
                logger.warn(
                    "Tournament - Error executing {}[{}] request({}), returning null",
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
    private TournamentCi loadBasicTournamentCi() {
        try {
            SportEventCi eventCacheItem = sportEventCache.getEventCacheItem(id);
            if (eventCacheItem instanceof TournamentCi) {
                return (TournamentCi) eventCacheItem;
            }
            handleException("loadBasicTournamentCI, CI type miss-match", null);
        } catch (CacheItemNotFoundException e) {
            handleException("loadBasicTournamentCI, CI not found", e);
        }
        return null;
    }

    @Override
    public void ensureSummaryIsFetchedForLanguages(List<Locale> languages, RequestOptions requestOptions) {
        TournamentCi tournamentCi = loadBasicTournamentCi();
        ofNullable(tournamentCi)
            .ifPresent(ci -> ci.requestMissingSummaryData(languages, false, requestOptions));
    }
}
