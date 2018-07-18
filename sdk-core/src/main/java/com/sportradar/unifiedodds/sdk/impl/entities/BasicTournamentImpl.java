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
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.StreamWrapperException;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Represents a sport tournament
 */
public class BasicTournamentImpl extends SportEventImpl implements BasicTournament {
    private final static Logger logger = LoggerFactory.getLogger(BasicTournamentImpl.class);

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
     * @param id an {@link URN} uniquely identifying the tournament associated with the current instance
     * @param sportId an {@link URN} identifying the sport to which the tournament belongs
     * @param locales a {@link List} of all languages for this instance
     * @param sportEntityFactory a {@link SportEntityFactory} instance used to construct {@link Competition} instances
     * @param exceptionHandlingStrategy the desired exception handling strategy
     */
    public BasicTournamentImpl(URN id,
                               URN sportId, List<Locale> locales,
                               SportEventCache sportEventCache,
                               SportEntityFactory sportEntityFactory,
                               ExceptionHandlingStrategy exceptionHandlingStrategy) {
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
        TournamentCI tournamentCi = loadBasicTournamentCI();

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
        TournamentCI tournamentCi = loadBasicTournamentCI();

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
        TournamentCI tournamentCi = loadBasicTournamentCI();

        if (tournamentCi == null) {
            handleException("tournamentCI missing", null);
            return null;
        }

        return tournamentCi.getScheduledEnd();
    }

    /**
     * Returns a {@link CategorySummary} representing the category associated with the current instance
     *
     * @return - a {@link CategorySummary} representing the category associated with the current instance
     */
    @Override
    public CategorySummary getCategory() {
        TournamentCI tournamentCi = loadBasicTournamentCI();

        if (tournamentCi == null || tournamentCi.getCategoryId() == null) {
            handleException("getCategory - missing category data", null);
            return null;
        }

        try {
            return sportEntityFactory.buildCategory(tournamentCi.getCategoryId(), locales);
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
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
        TournamentCI tournamentCi = loadBasicTournamentCI();

        if (tournamentCi == null) {
            handleException("tournamentCI missing", null);
            return null;
        }

        try {
            return tournamentCi.getCompetitorIds(locales) == null ? null :
                    sportEntityFactory.buildStreamCompetitors(tournamentCi.getCompetitorIds(locales), locales);
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
        TournamentCI tournamentCi = loadBasicTournamentCI();

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
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
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
        TournamentCI tournamentCi = loadBasicTournamentCI();

        if (tournamentCi == null) {
            handleException("tournamentCI missing", null);
            return null;
        }

        return tournamentCi.getTournamentCoverage() == null ? null :
                new TournamentCoverageImpl(tournamentCi.getTournamentCoverage());
    }

    /**
     * Returns the associated sport identifier
     * (This method its overridden because the superclass SportEvent does not contain the sportId in all cases)
     *
     * @return the unique sport identifier to which this event is associated
     */
    @Override
    public URN getSportId() {
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
     * Returns a {@link String} describing the current {@link Tournament} instance
     *
     * @return - a {@link String} describing the current {@link Tournament} instance
     */
    @Override
    public String toString() {
        return "BasicTournamentImpl{" +
                "id=" + id +
                ", locales=" + locales +
                "}";
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
                logger.warn("Tournament - Error executing {}[{}] request({}), returning null", this.getClass(), id, request);
            } else {
                logger.warn("Tournament - Error executing {}[{}] request({}), returning null", this.getClass(), id, request, e);
            }
        }
    }

    /**
     * Loads the associated entity cache item from the sport event cache
     *
     * @return the associated cache item
     */
    private TournamentCI loadBasicTournamentCI() {
        try {
            SportEventCI eventCacheItem = sportEventCache.getEventCacheItem(id);
            if (eventCacheItem instanceof TournamentCI) {
                return (TournamentCI) eventCacheItem;
            }
            handleException("loadBasicTournamentCI, CI type miss-match", null);
        } catch (CacheItemNotFoundException e) {
            handleException("loadBasicTournamentCI, CI not found", e);
        }
        return null;
    }
}
