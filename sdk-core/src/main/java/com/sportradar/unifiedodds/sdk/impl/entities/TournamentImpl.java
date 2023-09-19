/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventCi;
import com.sportradar.unifiedodds.sdk.caching.TournamentCi;
import com.sportradar.unifiedodds.sdk.caching.ci.SeasonCi;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.StreamWrapperException;
import com.sportradar.utils.Urn;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a sport tournament
 */
@SuppressWarnings(
    {
        "ClassFanOutComplexity",
        "ConstantName",
        "LambdaBodyLength",
        "LineLength",
        "MultipleStringLiterals",
        "NPathComplexity",
        "ReturnCount",
    }
)
public class TournamentImpl extends SportEventImpl implements Tournament {

    private static final Logger logger = LoggerFactory.getLogger(TournamentImpl.class);

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
     * Initializes a new {@link TournamentImpl} instance
     *
     * @param id an {@link Urn} uniquely identifying the tournament associated with the current instance
     * @param sportId an {@link Urn} identifying the sport to which the tournament belongs
     * @param locales a {@link List} of all languages for this instance
     * @param sportEventCache the cache used to retrieve additional sport event data
     * @param sportEntityFactory a {@link SportEntityFactory} instance used to construct {@link Competition} instances
     * @param exceptionHandlingStrategy the desired exception handling strategy
     */
    public TournamentImpl(
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
        TournamentCi tournamentCi = loadTournamentCi();

        if (tournamentCi == null) {
            handleException("TournamentCI missing", null);
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
        TournamentCi tournamentCi = loadTournamentCi();

        if (tournamentCi == null) {
            handleException("TournamentCI missing", null);
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
        TournamentCi tournamentCi = loadTournamentCi();

        if (tournamentCi == null) {
            handleException("TournamentCI missing", null);
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
        TournamentCi tournamentCi = loadTournamentCi();

        if (tournamentCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        return tournamentCi.isStartTimeTbd().isPresent() ? tournamentCi.isStartTimeTbd().get() : null;
    }

    /**
     * Returns the {@link Urn} specifying the replacement sport event for the current instance
     *
     * @return if available, the {@link Urn} specifying the replacement sport event for the current instance
     */
    @Override
    public Urn getReplacedBy() {
        TournamentCi tournamentCi = loadTournamentCi();

        if (tournamentCi == null) {
            handleException("TournamentCI missing", null);
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
        TournamentCi tournamentCi = loadTournamentCi();

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
     * Returns a {@link CurrentSeasonInfo} which contains data about the season in which the current instance
     * tournament is happening
     *
     * @return - a {@link CurrentSeasonInfo} which provides data about the season in
     *           which the current instance tournament is happening
     */
    @Override
    public CurrentSeasonInfo getCurrentSeason() {
        TournamentCi tournamentCi = loadTournamentCi();

        if (tournamentCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        SeasonCi currentSeason = tournamentCi.getCurrentSeason(locales);
        if (currentSeason == null) {
            logger.debug("Tournament {} has no current season", id);
            return null;
        }

        TournamentCi seasonCi = null;
        try {
            SportEventCi eventCacheItem = sportEventCache.getEventCacheItem(currentSeason.getId());
            if (eventCacheItem instanceof TournamentCi) {
                seasonCi = (TournamentCi) eventCacheItem;
            } else {
                handleException("getCurrentSeason - invalid cache item type", null);
            }
        } catch (CacheItemNotFoundException e) {
            handleException("getCurrentSeason - error providing season cache item", e);
        }

        if (seasonCi == null) {
            return null;
        }

        return new CurrentSeasonInfoImpl(
            currentSeason,
            seasonCi,
            sportEventCache,
            sportEntityFactory,
            locales,
            exceptionHandlingStrategy
        );
    }

    /**
     * Returns a {@link SportSummary} instance representing the sport associated with the current instance
     *
     * @return a {@link SportSummary} instance representing the sport associated with the current instance
     */
    @Override
    public SportSummary getSport() {
        TournamentCi tournamentCi = loadTournamentCi();

        if (tournamentCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        if (tournamentCi.getCategoryId() == null) {
            handleException("getSport - missing category data", null);
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
        TournamentCi tournamentCi = loadTournamentCi();

        if (tournamentCi == null) {
            handleException("TournamentCI missing", null);
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

        TournamentCi tournamentCi = loadTournamentCi();

        if (tournamentCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        if (tournamentCi.getCategoryId() == null) {
            handleException("Category id missing", null);
            return null;
        }

        try {
            SportSummary sportSummary = sportEntityFactory.buildSportForCategory(
                tournamentCi.getCategoryId(),
                locales
            );
            return sportSummary.getId();
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
            handleException("getSportId", e);
            return null;
        }
    }

    /**
     * Returns a list of associated tournament seasons
     *
     * @return a list of associated tournament seasons
     */
    @Override
    public List<Season> getSeasons() {
        TournamentCi tournamentCi = loadTournamentCi();

        if (tournamentCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        List<Urn> seasonIds = tournamentCi.getSeasonIds();
        try {
            return seasonIds == null
                ? null
                : seasonIds
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
                        if (e instanceof Season) {
                            return true;
                        } else {
                            logger.warn(
                                "Tournament.getSeasons found a non-season object[{}], instance: {}",
                                e.getId(),
                                e.getClass()
                            );
                            return false;
                        }
                    })
                    .map(e -> (Season) e)
                    .collect(Collectors.toList());
        } catch (StreamWrapperException e) {
            handleException("getSeasons", e);
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
        TournamentCi tournamentCi = loadTournamentCi();

        if (tournamentCi == null) {
            handleException("TournamentCI missing", null);
            return null;
        }

        return tournamentCi.isExhibitionGames();
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
            CurrentSeasonInfo season = getCurrentSeason();
            if (season == null) {
                return null;
            }
            return season.getSchedule();
        }

        try {
            return sportEntityFactory.buildSportEvents(eventIds, locales);
        } catch (com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException e) {
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
        return "TournamentImpl{" + "id=" + id + ", locales=" + locales + "}";
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
    private TournamentCi loadTournamentCi() {
        try {
            SportEventCi eventCacheItem = sportEventCache.getEventCacheItem(id);
            if (eventCacheItem instanceof TournamentCi) {
                return (TournamentCi) eventCacheItem;
            }
            handleException("loadTournamentCI, CI type miss-match", null);
        } catch (CacheItemNotFoundException e) {
            handleException("loadTournamentCI, CI not found", e);
        }
        return null;
    }
}
