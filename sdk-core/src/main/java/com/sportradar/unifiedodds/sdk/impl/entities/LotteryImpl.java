/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.LotteryCi;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventCi;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.StreamWrapperException;
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
@SuppressWarnings(
    {
        "ClassFanOutComplexity",
        "ConstantName",
        "LambdaBodyLength",
        "LineLength",
        "MultipleStringLiterals",
        "ReturnCount",
        "UnnecessaryParentheses",
    }
)
public class LotteryImpl extends SportEventImpl implements Lottery {

    private static final Logger logger = LoggerFactory.getLogger(LotteryImpl.class);

    private final List<Locale> locales;
    private final SportEventCache sportEventCache;
    private final SportEntityFactoryImpl sportEntityFactory;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    public LotteryImpl(
        Urn id,
        Urn sportId,
        List<Locale> locales,
        SportEventCache sportEventCache,
        SportEntityFactoryImpl sportEntityFactory,
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
     * Returns a {@link CategorySummary} representing the category associated with the current instance
     *
     * @return a {@link CategorySummary} representing the category associated with the current instance
     */
    @Override
    public CategorySummary getCategory() {
        LotteryCi lotteryCi = loadLotteryCi();

        if (lotteryCi == null || lotteryCi.getCategoryId() == null) {
            handleException("getCategory - missing category data", null);
            return null;
        }

        try {
            return sportEntityFactory.buildCategory(lotteryCi.getCategoryId(), locales);
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
            handleException("getCategory", e);
        }

        return null;
    }

    /**
     * Returns the associated bonus info
     *
     * @return the associated bonus info
     */
    @Override
    public BonusInfo getBonusInfo() {
        LotteryCi lotteryCi = loadLotteryCi();

        if (lotteryCi == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        return lotteryCi.getBonusInfo() == null ? null : new BonusInfoImpl(lotteryCi.getBonusInfo());
    }

    /**
     * Returns the associated draw info
     *
     * @return the associated draw info
     */
    @Override
    public DrawInfo getDrawInfo() {
        LotteryCi lotteryCi = loadLotteryCi();

        if (lotteryCi == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        return lotteryCi.getDrawInfo() == null ? null : new DrawInfoImpl(lotteryCi.getDrawInfo());
    }

    /**
     * Returns the lottery draws
     *
     * @return the lottery draw
     */
    @Override
    public List<Draw> getScheduledDraws() {
        LotteryCi lotteryCi = loadLotteryCi();

        if (lotteryCi == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        List<Urn> scheduledDraws = lotteryCi.getScheduledDraws();
        try {
            return scheduledDraws == null
                ? null
                : scheduledDraws
                    .stream()
                    .map(sId -> {
                        try {
                            return sportEntityFactory.buildSportEvent(sId, locales, false);
                        } catch (
                            com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e
                        ) {
                            throw new StreamWrapperException(e.getMessage(), e);
                        }
                    })
                    .filter(e -> {
                        if (e instanceof Draw) {
                            return true;
                        } else {
                            logger.warn(
                                "Lottery.getScheduledDraws found a non-draw object[{}], instance: {}",
                                e.getId(),
                                e.getClass()
                            );
                            return false;
                        }
                    })
                    .map(e -> (Draw) e)
                    .collect(Collectors.toList());
        } catch (StreamWrapperException e) {
            handleException("getScheduledDraws", e);
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
        LotteryCi lotteryCi = loadLotteryCi();

        if (lotteryCi == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        if (lotteryCi.getCategoryId() == null) {
            handleException("getSport - missing category data", null);
            return null;
        }

        try {
            return sportEntityFactory.buildSportForCategory(lotteryCi.getCategoryId(), locales);
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
            handleException("getSport", e);
            return null;
        }
    }

    /**
     * Returns a {@link TournamentCoverage} instance which describes the associated tournament coverage information
     * (no coverage info available for lotteries)
     *
     * @return a {@link TournamentCoverage} instance describing the tournament coverage information
     */
    @Override
    public TournamentCoverage getTournamentCoverage() {
        return null; // no coverage for lotteries
    }

    /**
     * Returns the sport event name
     *
     * @param locale the {@link Locale} in which the name should be provided
     * @return the sport event name if available; otherwise null
     */
    @Override
    public String getName(Locale locale) {
        LotteryCi lotteryCi = loadLotteryCi();

        if (lotteryCi == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        return lotteryCi.getNames(locales).get(locale);
    }

    /**
     * Returns the unique sport identifier to which this event is associated
     *
     * @return - the unique sport identifier to which this event is associated
     */
    @Override
    public Urn getSportId() {
        if (super.getSportId() != null) {
            return super.getSportId();
        }

        LotteryCi lotteryCi = loadLotteryCi();

        if (lotteryCi == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        if (lotteryCi.getCategoryId() == null) {
            handleException("Category id missing", null);
            return null;
        }

        try {
            SportSummary sportSummary = sportEntityFactory.buildSportForCategory(
                lotteryCi.getCategoryId(),
                locales
            );
            return sportSummary.getId();
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
            handleException("getSportId failed", e);
            return null;
        }
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
        LotteryCi lotteryCi = loadLotteryCi();

        if (lotteryCi == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        return lotteryCi.getScheduled();
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
        LotteryCi lotteryCi = loadLotteryCi();

        if (lotteryCi == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        return lotteryCi.getScheduledEnd();
    }

    /**
     * Returns the {@link Boolean} specifying if the start time to be determined is set for the current instance
     *
     * @return if available, the {@link Boolean} specifying if the start time to be determined is set for the current instance
     */
    @Override
    public Boolean isStartTimeTbd() {
        LotteryCi lotteryCi = loadLotteryCi();

        if (lotteryCi == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        return lotteryCi.isStartTimeTbd().isPresent() ? lotteryCi.isStartTimeTbd().get() : null;
    }

    /**
     * Returns the {@link Urn} specifying the replacement sport event for the current instance
     *
     * @return if available, the {@link Urn} specifying the replacement sport event for the current instance
     */
    @Override
    public Urn getReplacedBy() {
        LotteryCi lotteryCi = loadLotteryCi();

        if (lotteryCi == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        return lotteryCi.getReplacedBy();
    }

    @Override
    public String toString() {
        return (
            "LotteryImpl{" +
            "id=" +
            id +
            ", sportId=" +
            sportId +
            ", locales=" +
            locales +
            ", exceptionHandlingStrategy=" +
            exceptionHandlingStrategy +
            "} "
        );
    }

    /**
     * Loads the associated entity cache item from the sport event cache
     *
     * @return the associated cache item
     */
    private LotteryCi loadLotteryCi() {
        try {
            SportEventCi eventCacheItem = sportEventCache.getEventCacheItem(id);
            if (eventCacheItem instanceof LotteryCi) {
                return (LotteryCi) eventCacheItem;
            }
            handleException("loadLotteryCI, CI type miss-match", null);
        } catch (CacheItemNotFoundException e) {
            handleException("loadLotteryCI, CI not found", e);
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
                throw new ObjectNotFoundException(this.getClass() + "[" + id + "], request(" + request + ")");
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
