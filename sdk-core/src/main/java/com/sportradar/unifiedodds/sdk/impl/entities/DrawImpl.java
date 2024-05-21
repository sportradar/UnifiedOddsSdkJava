/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.DrawCi;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventCi;
import com.sportradar.unifiedodds.sdk.caching.ci.DrawResultCi;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.SportEntityFactoryImpl;
import com.sportradar.utils.Urn;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The basic implementation of a lottery
 */
@SuppressWarnings({ "ClassFanOutComplexity", "ConstantName", "LineLength", "MultipleStringLiterals" })
public class DrawImpl extends SportEventImpl implements Draw {

    private static final Logger logger = LoggerFactory.getLogger(DrawImpl.class);

    private final List<Locale> locales;
    private final SportEventCache sportEventCache;
    private final SportEntityFactory sportEntityFactory;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    public DrawImpl(
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
     * Returns the status of the draw
     *
     * @return the status of the draw
     */
    @Override
    public DrawStatus getStatus() {
        DrawCi drawCi = loadDrawCi();

        if (drawCi == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        return drawCi.getStatus();
    }

    /**
     * Returns a list of draw results
     *
     * @return a list of draw results
     */
    @Override
    public List<DrawResult> getResults() {
        DrawCi drawCi = loadDrawCi();

        if (drawCi == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        List<DrawResultCi> results = drawCi.getResults(locales);

        return results == null
            ? null
            : results
                .stream()
                .map(i -> new DrawResultImpl(i.getValue(), i.getName(locales)))
                .collect(Collectors.toList());
    }

    /**
     * Returns the associated lottery
     *
     * @return the associated lottery
     */
    @Override
    public Lottery getLottery() {
        DrawCi drawCi = loadDrawCi();

        if (drawCi == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        Urn lotteryId = drawCi.getLotteryId();
        if (lotteryId == null) {
            handleException("Lottery id missing", null);
            return null;
        }

        try {
            SportEvent sportEvent = sportEntityFactory.buildSportEvent(lotteryId, locales, false);
            if (sportEvent instanceof Lottery) {
                return (Lottery) sportEvent;
            }
            handleException(
                "getLottery - invalid type[" + sportEvent.getId() + "]: " + sportEvent.getClass(),
                null
            );
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
        DrawCi drawCi = loadDrawCi();

        if (drawCi == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        return drawCi.getNames(locales).get(locale);
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
        DrawCi drawCi = loadDrawCi();

        if (drawCi == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        return drawCi.getScheduled();
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
        DrawCi drawCi = loadDrawCi();

        if (drawCi == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        return drawCi.getScheduledEnd();
    }

    /**
     * Returns the {@link Boolean} specifying if the start time to be determined is set for the current instance
     *
     * @return if available, the {@link Boolean} specifying if the start time to be determined is set for the current instance
     */
    @Override
    public Boolean isStartTimeTbd() {
        DrawCi drawCi = loadDrawCi();

        if (drawCi == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        return drawCi.isStartTimeTbd().orElse(null);
    }

    /**
     * Returns the {@link Urn} specifying the replacement sport event for the current instance
     *
     * @return if available, the {@link Urn} specifying the replacement sport event for the current instance
     */
    @Override
    public Urn getReplacedBy() {
        DrawCi drawCi = loadDrawCi();

        if (drawCi == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        return drawCi.getReplacedBy();
    }

    /**
     * Returns the associated sport identifier
     * (This method its overridden because the superclass SportEvent does not contain the sportId in all cases)
     *
     * @return - the unique sport identifier to which this event is associated
     */
    @Override
    public Urn getSportId() {
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
    public Integer getDisplayId() {
        DrawCi drawCi = loadDrawCi();

        if (drawCi == null) {
            handleException("DrawCI missing", null);
            return null;
        }

        return drawCi.getDisplayId();
    }

    /**
     * Loads the associated entity cache item from the sport event cache
     *
     * @return the associated cache item
     */
    private DrawCi loadDrawCi() {
        try {
            SportEventCi eventCacheItem = sportEventCache.getEventCacheItem(id);
            if (eventCacheItem instanceof DrawCi) {
                return (DrawCi) eventCacheItem;
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
                logger.warn(
                    "Error executing {}[{}] request({}), returning null value",
                    this.getClass(),
                    id,
                    request
                );
            } else {
                logger.warn(
                    "Error executing {}[{}] request({}), returning null value",
                    this.getClass(),
                    id,
                    request,
                    e
                );
            }
        }
    }
}
