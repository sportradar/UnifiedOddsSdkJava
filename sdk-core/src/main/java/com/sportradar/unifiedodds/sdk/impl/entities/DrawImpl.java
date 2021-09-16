/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.DrawCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.ci.DrawResultCI;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.SportEntityFactoryImpl;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * The basic implementation of a lottery
 */
public class DrawImpl extends SportEventImpl implements Draw {
    private static final Logger logger = LoggerFactory.getLogger(DrawImpl.class);

    private final List<Locale> locales;
    private final SportEventCache sportEventCache;
    private final SportEntityFactoryImpl sportEntityFactory;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    public DrawImpl(URN id, URN sportId, List<Locale> locales, SportEventCache sportEventCache, SportEntityFactoryImpl sportEntityFactory, ExceptionHandlingStrategy exceptionHandlingStrategy) {
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
     * Returns the status of the draw
     *
     * @return the status of the draw
     */
    @Override
    public DrawStatus getStatus() {
        DrawCI drawCI = loadDrawCI();

        if (drawCI == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        return drawCI.getStatus();
    }

    /**
     * Returns a list of draw results
     *
     * @return a list of draw results
     */
    @Override
    public List<DrawResult> getResults() {
        DrawCI drawCI = loadDrawCI();

        if (drawCI == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        List<DrawResultCI> results = drawCI.getResults(locales);

        return results == null ? null : results.stream()
                .map(i -> new DrawResultImpl(
                        i.getValue(),
                        i.getName(locales)
                ))
                .collect(Collectors.toList());
    }

    /**
     * Returns the associated lottery
     *
     * @return the associated lottery
     */
    @Override
    public Lottery getLottery() {
        DrawCI drawCI = loadDrawCI();

        if (drawCI == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        URN lotteryId = drawCI.getLotteryId();
        if (lotteryId == null) {
            handleException("Lottery id missing", null);
            return null;
        }

        try {
            SportEvent sportEvent = sportEntityFactory.buildSportEvent(lotteryId, locales, false);
            if (sportEvent instanceof Lottery) {
                return (Lottery) sportEvent;
            }
            handleException("getLottery - invalid type[" + sportEvent.getId() + "]: " + sportEvent.getClass(), null);
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
            handleException("getLottery - not found", e);
        }

        return null;
    }

    /**
     * Returns the sport event name
     *
     * @param locale the {@link Locale} in which the name should be provided
     * @return the sport event name if available; otherwise null
     */
    @Override
    public String getName(Locale locale) {
        DrawCI drawCI = loadDrawCI();

        if (drawCI == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        return drawCI.getNames(locales).get(locale);
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
        DrawCI drawCI = loadDrawCI();

        if (drawCI == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        return drawCI.getScheduled();
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
        DrawCI drawCI = loadDrawCI();

        if (drawCI == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        return drawCI.getScheduledEnd();
    }

    /**
     * Returns the {@link Boolean} specifying if the start time to be determined is set for the current instance
     *
     * @return if available, the {@link Boolean} specifying if the start time to be determined is set for the current instance
     */
    @Override
    public Boolean isStartTimeTbd() {
        DrawCI drawCI = loadDrawCI();

        if (drawCI == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        return drawCI.isStartTimeTbd().isPresent() ? drawCI.isStartTimeTbd().get() : null;
    }

    /**
     * Returns the {@link URN} specifying the replacement sport event for the current instance
     *
     * @return if available, the {@link URN} specifying the replacement sport event for the current instance
     */
    @Override
    public URN getReplacedBy() {
        DrawCI drawCI = loadDrawCI();

        if (drawCI == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        return drawCI.getReplacedBy();
    }

    /**
     * Returns the associated sport identifier
     * (This method its overridden because the superclass SportEvent does not contain the sportId in all cases)
     *
     * @return - the unique sport identifier to which this event is associated
     */
    @Override
    public URN getSportId() {
        if (super.getSportId() != null) {
            return super.getSportId();
        }

        // try to get the sport id from the tournament instance
        Lottery lottery = getLottery();
        if (lottery == null) {
            return null;
        }

        SportSummary sport = lottery.getSport();
        if (sport != null) {
            return sport.getId();
        }

        return null;
    }

    /**
     * Returns the display id
     * @return the display id
     */
    @Override
    public Integer getDisplayId(){
        DrawCI drawCI = loadDrawCI();

        if (drawCI == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        return drawCI.getDisplayId();
    }

    /**
     * Loads the associated entity cache item from the sport event cache
     *
     * @return the associated cache item
     */
    private DrawCI loadDrawCI() {
        try {
            SportEventCI eventCacheItem = sportEventCache.getEventCacheItem(id);
            if (eventCacheItem instanceof DrawCI) {
                return (DrawCI) eventCacheItem;
            }
            handleException("loadDrawCI, CI type miss-match", null);
        } catch (CacheItemNotFoundException e) {
            handleException("loadDrawCI, CI not found", e);
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
                throw new ObjectNotFoundException("DrawImpl[" + id + "], request(" + request + ")");
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error executing {}[{}] request({}), returning null value", this.getClass(), id, request);
            } else {
                logger.warn("Error executing {}[{}] request({}), returning null value", this.getClass(), id, request, e);
            }
        }
    }
}
