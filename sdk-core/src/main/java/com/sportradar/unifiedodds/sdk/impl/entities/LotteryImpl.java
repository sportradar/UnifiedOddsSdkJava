/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.LotteryCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventCI;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.StreamWrapperException;
import com.sportradar.unifiedodds.sdk.impl.SportEntityFactoryImpl;
import com.sportradar.utils.URN;
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
        "AbbreviationAsWordInName",
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
        URN id,
        URN sportId,
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
        LotteryCI lotteryCi = loadLotteryCI();

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
        LotteryCI lotteryCI = loadLotteryCI();

        if (lotteryCI == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        return lotteryCI.getBonusInfo() == null ? null : new BonusInfoImpl(lotteryCI.getBonusInfo());
    }

    /**
     * Returns the associated draw info
     *
     * @return the associated draw info
     */
    @Override
    public DrawInfo getDrawInfo() {
        LotteryCI lotteryCI = loadLotteryCI();

        if (lotteryCI == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        return lotteryCI.getDrawInfo() == null ? null : new DrawInfoImpl(lotteryCI.getDrawInfo());
    }

    /**
     * Returns the lottery draws
     *
     * @return the lottery draw
     */
    @Override
    public List<Draw> getScheduledDraws() {
        LotteryCI lotteryCI = loadLotteryCI();

        if (lotteryCI == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        List<URN> scheduledDraws = lotteryCI.getScheduledDraws();
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
        LotteryCI lotteryCi = loadLotteryCI();

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
        LotteryCI lotteryCi = loadLotteryCI();

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
    public URN getSportId() {
        if (super.getSportId() != null) {
            return super.getSportId();
        }

        LotteryCI lotteryCi = loadLotteryCI();

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
        LotteryCI lotteryCI = loadLotteryCI();

        if (lotteryCI == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        return lotteryCI.getScheduled();
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
        LotteryCI lotteryCI = loadLotteryCI();

        if (lotteryCI == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        return lotteryCI.getScheduledEnd();
    }

    /**
     * Returns the {@link Boolean} specifying if the start time to be determined is set for the current instance
     *
     * @return if available, the {@link Boolean} specifying if the start time to be determined is set for the current instance
     */
    @Override
    public Boolean isStartTimeTbd() {
        LotteryCI lotteryCI = loadLotteryCI();

        if (lotteryCI == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        return lotteryCI.isStartTimeTbd().isPresent() ? lotteryCI.isStartTimeTbd().get() : null;
    }

    /**
     * Returns the {@link URN} specifying the replacement sport event for the current instance
     *
     * @return if available, the {@link URN} specifying the replacement sport event for the current instance
     */
    @Override
    public URN getReplacedBy() {
        LotteryCI lotteryCI = loadLotteryCI();

        if (lotteryCI == null) {
            handleException("LotteryCI missing", null);
            return null;
        }

        return lotteryCI.getReplacedBy();
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
    private LotteryCI loadLotteryCI() {
        try {
            SportEventCI eventCacheItem = sportEventCache.getEventCacheItem(id);
            if (eventCacheItem instanceof LotteryCI) {
                return (LotteryCI) eventCacheItem;
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
