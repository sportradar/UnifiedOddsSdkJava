/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.SportEventCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.StageCI;
import com.sportradar.unifiedodds.sdk.caching.ci.SportEventConditionsCI;
import com.sportradar.unifiedodds.sdk.caching.ci.VenueCI;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.entities.status.CompetitionStatus;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.StreamWrapperException;
import com.sportradar.unifiedodds.sdk.impl.SportEventStatusFactory;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Represents a race type of sport event (more than two competitors)
 */
public class StageImpl extends SportEventImpl implements Stage {
    private static final Logger logger = LoggerFactory.getLogger(StageImpl.class);

    /**
     * The cache used to access associated cache items
     */
    private final SportEventCache sportEventCache;

    /**
     * The locales with which the object is built
     */
    private final List<Locale> locales;

    /**
     * A {@link SportEventStatusFactory} instance used to build event status entities
     */
    private final SportEventStatusFactory sportEventStatusFactory;

    /**
     * A {@link SportEntityFactory} instance used to construct {@link Tournament} instances
     */
    private final SportEntityFactory sportEntityFactory;

    /**
     * A {@link CompetitionStatus} containing information about the progress of the event associated with the current instance
     */
    private CompetitionStatus status;

    /**
     * The exception strategy that should be used within the instance
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * Initializes a new instance of the {@link StageImpl}
     *
     * @param id A {@link URN} uniquely identifying the sport event associated with the current instance
     * @param sportId A {@link URN} uniquely identifying the sport to which the tournament is related
     * @param sportEventCache A {@link SportEventCache} instance used to access the associated cache items
     * @param statusFactory A {@link SportEventStatusFactory} instance used to build status entities
     * @param sportEntityFactory A {@link SportEntityFactory} instance used to construct other associated sport entities
     * @param locales A {@link List} specifying languages the current instance supports
     * @param exceptionHandlingStrategy The exception handling strategy that should be followed by the instance
     */
    public StageImpl(URN id, URN sportId, SportEventCache sportEventCache, SportEventStatusFactory statusFactory,
                     SportEntityFactory sportEntityFactory, List<Locale> locales, ExceptionHandlingStrategy exceptionHandlingStrategy) {
        super(id, sportId);

        Preconditions.checkNotNull(sportEventCache);
        Preconditions.checkNotNull(statusFactory);
        Preconditions.checkNotNull(sportEntityFactory);
        Preconditions.checkNotNull(locales);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.sportEventCache = sportEventCache;
        this.locales = locales;
        this.sportEventStatusFactory = statusFactory;
        this.sportEntityFactory = sportEntityFactory;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    /**
     * Returns a {@link SportSummary} instance representing the sport associated with the current instance
     *
     * @return a {@link SportSummary} instance representing the sport associated with the current instance
     */
    @Override
    public SportSummary getSport() {
        StageCI cacheItem = loadStageCI();

        if (cacheItem == null || cacheItem.getCategoryId() == null) {
                handleException("getSport - missing category data", null);
            return null;
        }

        try {
            return sportEntityFactory.buildSportForCategory(cacheItem.getCategoryId(), locales);
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
            handleException("getSport", e);
            return null;
        }
    }

    /**
     * Returns a {@link CategorySummary} representing the category associated with the current instance
     *
     * @return a {@link CategorySummary} representing the category associated with the current instance
     */
    @Override
    public CategorySummary getCategory() {
        StageCI cacheItem = loadStageCI();

        if (cacheItem == null || cacheItem.getCategoryId() == null) {
            handleException("getCategory - missing category data", null);
            return null;
        }

        try {
            return sportEntityFactory.buildCategory(cacheItem.getCategoryId(), locales);
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
            handleException("getCategory", e);
        }

        return null;
    }

    /**
     * Returns a {@link ParentRace} representing the parent race of the race represented by the current instance
     *
     * @return - a {@link ParentRace} representing the parent race of the race represented by the current instance or a null reference
     * if the represented race does not have the parent race
     */
    @Override
    public Stage getParentStage() {
        StageCI cacheItem = loadStageCI();

        if (cacheItem == null) {
            handleException("StageCI missing", null);
            return null;
        }

        URN id = cacheItem.getParentStageId();

        if (id == null) {
            return null;
        }

        try {
            SportEvent sportEvent = sportEntityFactory.buildSportEvent(id, locales, false);
            if (sportEvent instanceof Stage) {
                return (Stage) sportEvent;
            }
            handleException("getParentStage - built type is not a stage: " + sportEvent.getClass(), null);
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
            handleException("getParentStage", e);
        }

        return null;
    }

    /**
     * Returns a {@link List} of {@link ChildRace} instances representing stages of the multi-stage race
     *
     * @return - a {@link List} of {@link ChildRace} instances representing stages of the multi-stage race, if available
     */
    @Override
    public List<Stage> getStages() {
        StageCI cacheItem = loadStageCI();

        if (cacheItem == null) {
            handleException("StageCI missing", null);
            return null;
        }

        List<URN> childRaceIds = cacheItem.getStagesIds();

        if (childRaceIds == null) {
            return null;
        }

        List<Stage> result = new ArrayList<>();
        try {
            for (URN childRaceId : childRaceIds) {
                SportEvent sportEvent = sportEntityFactory.buildSportEvent(childRaceId, locales, false);
                if (sportEvent instanceof Stage) {
                    result.add((Stage) sportEvent);
                } else {
                    handleException("getStages - built type is not a stage[" + sportEvent.getId() + "]: " + sportEvent.getClass(), null);
                }
            }
            return result;
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
            handleException("getStages", e);
        }

        return null;
    }

    /**
     * Returns a {@link StageType} indicating the type of the associated stage
     *
     * @return a {@link StageType} indicating the type of the associated stage
     */
    @Override
    public StageType getStageType() {
        StageCI cacheItem = loadStageCI();

        if (cacheItem == null) {
            handleException("StageCI missing", null);
            return null;
        }

        return cacheItem.getStageType();
    }

    /**
     * Returns a {@link CompetitionStatus} containing information about the progress of the sport event
     * associated with the current instance
     *
     * @return - a {@link CompetitionStatus} containing information about the progress of the sport event
     * associated with the current instance
     */
    @Override
    public CompetitionStatus getStatus() {
        StageCI cacheItem = loadStageCI();

        if (cacheItem == null) {
            handleException("StageCI missing", null);
            return null;
        }

        if (status == null) {
            status = sportEventStatusFactory.buildSportEventStatus(id, CompetitionStatus.class, true);
        }

        return status;
    }

    /**
     * Returns a {@link CompetitionStatus} containing information about the progress of the sport event
     * associated with the current instance if already cached (does not make API call)
     *
     * @return - a {@link CompetitionStatus} containing information about the progress of the sport event
     * associated with the current instance if already cached (does not make API call)
     */
    @Override
    public Optional<CompetitionStatus> getStatusIfPresent()  {
        if (status == null) {
            status = sportEventStatusFactory.buildSportEventStatus(id, CompetitionStatus.class, false);
        }
        if(status == null) {
            return Optional.empty();
        }
        return Optional.of(status);
    }

    /**
     * Returns a {@link BookingStatus} enum member providing booking status of the current instance
     *
     * @return - a {@link BookingStatus} enum member providing booking status of the current instance
     */
    @Override
    public BookingStatus getBookingStatus() {
        StageCI cacheItem = loadStageCI();

        if (cacheItem == null) {
            handleException("StageCI missing", null);
            return null;
        }

        return cacheItem.getBookingStatus();
    }

    /**
     * Returns a {@link EventStatus}
     *
     * @return a {@link EventStatus}
     */
    @Override
    public EventStatus getEventStatus() {
        return getStatus().getStatus();
    }

    /**
     * Returns the venue where the sport event associated with the current instance will take place
     *
     * @return - the {@link Venue} where the sport event associated with the current instance will take place
     */
    @Override
    public Venue getVenue() {
        StageCI cacheItem = loadStageCI();

        if (cacheItem == null) {
            handleException("StageCI missing", null);
            return null;
        }

        VenueCI venue = cacheItem.getVenue(locales);

        return venue == null ? null : new VenueImpl(venue, locales);
    }

    /**
     * Returns a {@link SportEventConditions} representing live conditions of the sport event associated
     * with the current instance
     *
     * @return - the {@link SportEventConditions} representing live conditions of the sport event associated
     * with the current instance
     */
    @Override
    public SportEventConditions getConditions() {
        StageCI cacheItem = loadStageCI();

        if (cacheItem == null) {
            handleException("StageCI missing", null);
            return null;
        }

        SportEventConditionsCI conditions = cacheItem.getConditions(locales);

        return conditions == null ? null :
                new SportEventConditionsImpl(conditions, locales);
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
        StageCI cacheItem = loadStageCI();

        if (cacheItem == null) {
            handleException("StageCI missing", null);
            return null;
        }

        List<URN> competitors = cacheItem.getCompetitorIds(locales);

        if (competitors == null) {
            return null;
        }

        try {
            return sportEntityFactory.buildStreamCompetitors(competitors, cacheItem, locales);
        } catch (StreamWrapperException e) {
            handleException("getCompetitors failure", e);
            return null;
        }
    }

    /**
     * Returns the sport event name
     *
     * @param locale the {@link Locale} in which the name should be provided
     * @return the sport event name if available; otherwise null
     */
    @Override
    public String getName(Locale locale) {
        StageCI cacheItem = loadStageCI();

        if (cacheItem == null) {
            handleException("StageCI missing", null);
            return null;
        }

        return cacheItem.getNames(locales).get(locale);
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
        StageCI cacheItem = loadStageCI();

        if (cacheItem == null) {
            handleException("StageCI missing", null);
            return null;
        }

        return cacheItem.getScheduled();
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
        StageCI cacheItem = loadStageCI();

        if (cacheItem == null) {
            handleException("StageCI missing", null);
            return null;
        }

        return cacheItem.getScheduledEnd();
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
     * Returns a {@link String} describing the current {@link Stage} instance
     *
     * @return - a {@link String} describing the current {@link Stage} instance
     */
    @Override
    public String toString() {
        return "StageImpl{" +
                "id=" + id +
                ", locales=" + locales +
                "} ";
    }

    /**
     * Loads the associated entity cache item from the sport event cache
     *
     * @return the associated cache item
     */
    private StageCI loadStageCI() {
        try {
            SportEventCI eventCacheItem = sportEventCache.getEventCacheItem(id);
            if (eventCacheItem instanceof StageCI) {
                return (StageCI) eventCacheItem;
            }
            handleException("loadStageCI, CI type miss-match", null);
        } catch (CacheItemNotFoundException e) {
            handleException("loadStageCI, CI not found", e);
        }
        return null;
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
                throw new ObjectNotFoundException("StageImpl[" + id + "], request(" + request + ")");
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error providing StageImpl[{}] request({})", id, request);
            } else {
                logger.warn("Error providing StageImpl[{}] request({}), ex:", id, request, e);
            }
        }
    }
}
