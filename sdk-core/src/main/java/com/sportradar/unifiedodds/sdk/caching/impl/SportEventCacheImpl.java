/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.inject.Inject;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.BookingManager;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCacheItem;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCi;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableSdkCache;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableSportEventCi;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.impl.MappingTypeProvider;
import com.sportradar.utils.Urn;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements methods used to access sport event data
 */
@SuppressWarnings(
    {
        "ClassFanOutComplexity",
        "ConstantName",
        "CyclomaticComplexity",
        "LambdaBodyLength",
        "LineLength",
        "MethodLength",
        "NPathComplexity",
        "NeedBraces",
        "OneStatementPerLine",
        "ReturnCount",
    }
)
public class SportEventCacheImpl implements SportEventCache, DataRouterListener, ExportableSdkCache {

    /**
     * The {@link Logger} instance used to log {@link SportEventCacheImpl} events
     */
    private static final Logger logger = LoggerFactory.getLogger(SportEventCacheImpl.class);

    /**
     * A {@link Cache} instance used to cache sport events data
     */
    private final Cache<Urn, SportEventCi> sportEventsCache;

    /**
     * A factory used to build specific sport event cache items
     */
    private final CacheItemFactory cacheItemFactory;

    /**
     * The {@link MappingTypeProvider} instance used to detect different CI types
     */
    private final MappingTypeProvider mappingTypeProvider;

    /**
     * The {@link DataRouterManager} instance used to initiate data requests
     */
    private final DataRouterManager dataRouterManager;

    /**
     * The default {@link Locale}
     */
    private final Locale defaultLocale;

    @Inject
    SportEventCacheImpl(
        CacheItemFactory cacheItemFactory,
        MappingTypeProvider mappingTypeProvider,
        DataRouterManager dataRouterManager,
        SdkInternalConfiguration sdkInternalConfiguration,
        Cache<Urn, SportEventCi> sportEventsCache
    ) {
        Preconditions.checkNotNull(cacheItemFactory);
        Preconditions.checkNotNull(mappingTypeProvider);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(sdkInternalConfiguration);
        Preconditions.checkNotNull(sportEventsCache);

        this.cacheItemFactory = cacheItemFactory;
        this.mappingTypeProvider = mappingTypeProvider;
        this.dataRouterManager = dataRouterManager;
        this.defaultLocale = sdkInternalConfiguration.getDefaultLocale();
        this.sportEventsCache = sportEventsCache;
    }

    /**
     * Returns a {@link SportEventCi} instance representing a cached sport event data
     *
     * @param id an {@link Urn} specifying the id of the sport event
     * @return a {@link SportEventCi} instance representing cached sport event data
     */
    @Override
    public SportEventCi getEventCacheItem(Urn id) throws CacheItemNotFoundException {
        Preconditions.checkNotNull(id);

        try {
            return sportEventsCache.get(
                id,
                () -> {
                    logger.info("Cache miss for[{}], providing CI", id);
                    try {
                        return provideEventCi(id);
                    } catch (IllegalCacheStateException e) {
                        throw new CacheItemNotFoundException(
                            String.format("An error occurred while loading a new cache item '%s', ex: ", id),
                            e
                        );
                    }
                }
            );
        } catch (ExecutionException e) {
            throw new CacheItemNotFoundException(
                String.format("Cache item could not be loaded[%s], ex: ", id),
                e
            );
        }
    }

    /**
     * Returns a {@link List} containing id's of sport events, which belong to a specific tournament
     *
     * @param tournamentId an {@link Urn} specifying the id of the tournament to which the events should relate
     * @param locale the locale to fetch the data
     * @return a {@link List} containing id's of sport events, which belong to the specified tournament
     */
    @Override
    public List<Urn> getEventIds(Urn tournamentId, Locale locale) throws IllegalCacheStateException {
        logger.debug("Providing tournament[{}] event IDs", tournamentId);
        try {
            if (locale == null) {
                return dataRouterManager.requestEventsFor(defaultLocale, tournamentId);
            } else {
                return dataRouterManager.requestEventsFor(locale, tournamentId);
            }
        } catch (CommunicationException e) {
            throw new IllegalCacheStateException(
                "Error occurred while fetching tournament schedule[" + tournamentId + "]",
                e
            );
        }
    }

    /**
     * Returns a {@link List} containing id's of sport events, which are scheduled for a specific date - if provided;
     * otherwise a {@link List} of currently live events is returned
     *
     * @param date an optional {@link Date} for which the data is provided
     * @param locale the locale to fetch the data
     * @return a {@link List} of events that are happening on the specified {@link Date};
     * or a {@link List} of currently live events
     */
    @Override
    public List<Urn> getEventIds(Date date, Locale locale) throws IllegalCacheStateException {
        logger.debug("Providing event IDs for {}", date == null ? "live" : date);
        try {
            if (locale == null) {
                return dataRouterManager.requestEventsFor(defaultLocale, date);
            } else {
                return dataRouterManager.requestEventsFor(locale, date);
            }
        } catch (CommunicationException e) {
            throw new IllegalCacheStateException(
                "Error occurred while fetching date schedule for " + (date == null ? "live" : date),
                e
            );
        }
    }

    /**
     * Purges an item from the {@link SportEventCache}
     *
     * @param id The {@link Urn} specifying the event which should be purged
     */
    @Override
    public void purgeCacheItem(Urn id) {
        if (id == null) {
            return;
        }

        logger.debug("Purging CI[{}]", id);
        sportEventsCache.invalidate(id);
    }

    @Override
    public void onSportEventFetched(Urn id, SapiSportEvent data, Locale dataLocale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCi ifPresent = sportEventsCache.getIfPresent(id);
        if (ifPresent == null) {
            Class mappingType;
            try {
                mappingType = provideMappingType(id);
            } catch (IllegalCacheStateException e) {
                logger.warn(
                    "SportEventCache.onSportEventFetched -> Failed to provide valid mapping type for id [{}]",
                    id
                );
                return;
            }

            if (mappingType.equals(Match.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildMatchCi(id, data, dataLocale));
            } else if (mappingType.equals(Stage.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildStageCi(id, data, dataLocale));
            }
        } else {
            ifPresent.merge(data, dataLocale);
        }
        if (data.getParent() != null) {
            Urn parentId = Urn.parse(data.getParent().getId());
            saveParentStage(parentId, data.getParent(), data.getTournament(), dataLocale);
        }
        if (data.getAdditionalParents() != null && !data.getAdditionalParents().getParent().isEmpty()) {
            for (SapiParentStage parentStage : data.getAdditionalParents().getParent()) {
                saveParentStage(
                    Urn.parse(parentStage.getId()),
                    parentStage,
                    data.getTournament(),
                    dataLocale
                );
            }
        }
    }

    /**
     * We save the parent stage, to save Type and StageType since for tournament if provided
     * @param parentId
     * @param parentStage
     * @param tournament
     * @param dataLocale
     */
    private void saveParentStage(
        Urn parentId,
        SapiParentStage parentStage,
        SapiTournament tournament,
        Locale dataLocale
    ) {
        if (parentId == null || parentStage == null) {
            return;
        }
        SportEventCi stagePresent = sportEventsCache.getIfPresent(parentId);
        if (stagePresent == null) {
            Urn tournamentId = tournament == null ? null : Urn.parse(tournament.getId());
            if (parentId.equals(tournamentId)) {
                StageCi ci = cacheItemFactory.buildStageCi(parentId, tournament, dataLocale);
                ci.merge(parentStage, dataLocale);
                sportEventsCache.put(parentId, ci);
            } else {
                sportEventsCache.put(
                    parentId,
                    cacheItemFactory.buildStageCi(parentId, parentStage, dataLocale)
                );
            }
        } else {
            stagePresent.merge(parentStage, dataLocale);
        }
    }

    @Override
    public void onChildSportEventFetched(
        Urn id,
        SapiSportEventChildren.SapiSportEvent data,
        Locale dataLocale
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCi ifPresent = sportEventsCache.getIfPresent(id);
        if (ifPresent == null) {
            Class mappingType;
            try {
                mappingType = provideMappingType(id);
            } catch (IllegalCacheStateException e) {
                logger.warn(
                    "SportEventCache.onChildSportEventFetched -> Failed to provide valid mapping type for id [{}]",
                    id
                );
                return;
            }

            if (mappingType.equals(Match.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildMatchCi(id, data, dataLocale));
            } else if (mappingType.equals(Stage.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildStageCi(id, data, dataLocale));
            }
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    public void onTournamentExtendedFetched(Urn id, SapiTournamentExtended data, Locale dataLocale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCi ifPresent = sportEventsCache.getIfPresent(id);
        if (ifPresent == null) {
            Class mappingType;
            try {
                mappingType = provideMappingType(id);
            } catch (IllegalCacheStateException e) {
                logger.warn(
                    "SportEventCache.onTournamentExtendedFetched -> Failed to provide valid mapping type for id [{}]",
                    id
                );
                return;
            }

            if (isTournamentCiType(mappingType)) {
                sportEventsCache.put(id, cacheItemFactory.buildTournamentCi(id, data, dataLocale));
            } else if (mappingType.equals(Stage.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildStageCi(id, data, dataLocale));
            } else {
                logger.warn(
                    "SportEventCache.onTournamentExtendedFetched -> discarding data, mapping type not supported. id:{}, type:{}",
                    id,
                    mappingType
                );
            }
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    public void onTournamentInfoEndpointFetched(
        Urn requestedId,
        Urn tournamentId,
        Urn seasonId,
        SapiTournamentInfoEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(tournamentId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        if (requestedId.equals(tournamentId)) {
            // if the requested id is the same as the tournament id, we can store as the "full current season response" and "full latest tournament response"
            storeTournamentInfoEndpoint(tournamentId, data, dataLocale, requester);

            if (seasonId != null) {
                storeTournamentInfoEndpoint(seasonId, data, dataLocale, requester);
            }
        } else {
            // if we didn't request the "outer" tournament, we can not store it as such, because the data might be different - ex: previous or next season request
            storeTournamentInfoEndpoint(seasonId, data, dataLocale, requester);
        }
    }

    @Override
    public void onStageSummaryEndpointFetched(
        Urn id,
        SapiStageSummaryEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCi ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            sportEventsCache.put(id, cacheItemFactory.buildStageCi(id, data, dataLocale));
        } else {
            ifPresent.merge(data, dataLocale);
        }
        if (data.getSportEvent().getParent() != null) {
            Urn parentId = Urn.parse(data.getSportEvent().getParent().getId());
            saveParentStage(
                parentId,
                data.getSportEvent().getParent(),
                data.getSportEvent().getTournament(),
                dataLocale
            );
        }
        if (
            data.getSportEvent().getAdditionalParents() != null &&
            !data.getSportEvent().getAdditionalParents().getParent().isEmpty()
        ) {
            for (SapiParentStage parentStage : data.getSportEvent().getAdditionalParents().getParent()) {
                saveParentStage(
                    Urn.parse(parentStage.getId()),
                    parentStage,
                    data.getSportEvent().getTournament(),
                    dataLocale
                );
            }
        }
    }

    @Override
    public void onMatchSummaryEndpointFetched(
        Urn id,
        SapiMatchSummaryEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCi ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            sportEventsCache.put(id, cacheItemFactory.buildMatchCi(id, data, dataLocale));
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    public void onFixtureFetched(Urn id, SapiFixture data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCi ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            Class mappingType;
            try {
                mappingType = provideMappingType(id);
            } catch (IllegalCacheStateException e) {
                logger.warn(
                    "SportEventCache.onFixtureFetched -> Failed to provide valid mapping type for id [{}]",
                    id
                );
                return;
            }

            if (mappingType.equals(Match.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildMatchCi(id, data, dataLocale));
            } else if (mappingType.equals(Stage.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildStageCi(id, data, dataLocale));
            } else {
                logger.warn(
                    "SportEventCache.onFixtureFetched -> discarding data, mapping type not supported. id:{}, type:{}",
                    id,
                    mappingType
                );
            }
        } else {
            ifPresent.merge(data, dataLocale);
        }
        if (data.getParent() != null) {
            Urn parentId = Urn.parse(data.getParent().getId());
            saveParentStage(parentId, data.getParent(), data.getTournament(), dataLocale);
        }
        if (data.getAdditionalParents() != null && !data.getAdditionalParents().getParent().isEmpty()) {
            for (SapiParentStage parentStage : data.getAdditionalParents().getParent()) {
                saveParentStage(
                    Urn.parse(parentStage.getId()),
                    parentStage,
                    data.getTournament(),
                    dataLocale
                );
            }
        }
    }

    @Override
    public void onTournamentFetched(Urn id, SapiTournament data, Locale locale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        SportEventCi ifPresent = sportEventsCache.getIfPresent(id);
        if (ifPresent == null) {
            Class mappingType;
            try {
                mappingType = provideMappingType(id);
            } catch (IllegalCacheStateException e) {
                logger.warn(
                    "SportEventCache.onTournamentFetched -> Failed to provide valid mapping type for id [{}]",
                    id
                );
                return;
            }

            if (isTournamentCiType(mappingType)) {
                sportEventsCache.put(id, cacheItemFactory.buildTournamentCi(id, data, locale));
            } else if (mappingType == Stage.class) {
                sportEventsCache.put(id, cacheItemFactory.buildStageCi(id, data, locale));
            } else {
                logger.warn(
                    "SportEventCache.onTournamentFetched -> discarding data, mapping type not supported. id:{}, type:{}",
                    id,
                    mappingType
                );
            }
        } else {
            ifPresent.merge(data, locale);
        }
    }

    @Override
    public void onMatchTimelineFetched(
        Urn id,
        SapiMatchTimelineEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCi ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            Class mappingType;
            try {
                mappingType = provideMappingType(id);
            } catch (IllegalCacheStateException e) {
                logger.warn(
                    "SportEventCache.onMatchTimelineFetched -> Failed to provide valid mapping type for id [{}]",
                    id
                );
                return;
            }

            if (mappingType.equals(Match.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildMatchCi(id));
                //            } else if (mappingType.equals(Stage.class)) {
                //                sportEventsCache.put(id, cacheItemFactory.buildStageCI(id, data, dataLocale));
            } else {
                logger.warn(
                    "SportEventCache.onMatchTimelineFetched -> discarding data, mapping type not supported. id:{}, type:{}",
                    id,
                    mappingType
                );
            }
            ifPresent = sportEventsCache.getIfPresent(id);
        }
        if (ifPresent != null) {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    public void onLotteryFetched(Urn id, SapiLottery data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCi ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            sportEventsCache.put(id, cacheItemFactory.buildLotteryCi(id, data, dataLocale));
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    @SuppressWarnings("Duplicates") // it is not a duplicate, different CI factory method
    public void onDrawFetched(Urn id, SapiDrawEvent data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCi ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            sportEventsCache.put(id, cacheItemFactory.buildDrawCi(id, data, dataLocale));
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    @SuppressWarnings("Duplicates") // it is not a duplicate, different CI factory method
    public void onDrawFixtureFetched(Urn id, SapiDrawFixture data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCi ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            sportEventsCache.put(id, cacheItemFactory.buildDrawCi(id, data, dataLocale));
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    @SuppressWarnings("Duplicates") // it is not a duplicate, different CI factory method
    public void onDrawSummaryEndpointFetched(
        Urn id,
        SapiDrawSummary data,
        Locale dataLocale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCi ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            sportEventsCache.put(id, cacheItemFactory.buildDrawCi(id, data, dataLocale));
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    /**
     * Method that gets triggered when the associated event gets booked trough the {@link BookingManager}
     *
     * @param id the {@link Urn} of the event that was successfully booked
     */
    @Override
    public void onEventBooked(Urn id) {
        SportEventCi ifPresent = sportEventsCache.getIfPresent(id);
        if (ifPresent instanceof CompetitionCi) {
            ((CompetitionCi) ifPresent).onEventBooked();
        } else {
            logger.warn("Received onEventBooked event for an unsupported event type, id: {}", id);
        }
    }

    /**
     * Adds fixture timestamp to cache so that the next fixture calls for the event goes through non-cached fixture provider
     *
     * @param id the {@link Urn} of the event
     */
    @Override
    public void addFixtureTimestamp(Urn id) {
        Cache<Urn, Date> cache = cacheItemFactory.getFixtureTimestampCache();
        cache.put(id, new Date());
    }

    /**
     * Deletes the sport events from cache which are scheduled before specified date
     *
     * @param before the scheduled Date used to delete sport events from cache
     * @return number of deleted items
     */
    @Override
    public Integer deleteSportEventsFromCache(Date before) {
        Preconditions.checkNotNull(before);

        long startCount = sportEventsCache.size();
        List<SportEventCi> cacheItems = sportEventsCache
            .asMap()
            .values()
            .stream()
            .collect(Collectors.toList());
        for (SportEventCi ci : cacheItems) {
            if (ci.getScheduledRaw() != null) {
                if (ci.getScheduledRaw().before(before)) {
                    sportEventsCache.invalidate(ci.getId());
                }
            } else if (ci.getScheduledEndRaw() != null) {
                if (ci.getScheduledEndRaw().before(before)) {
                    sportEventsCache.invalidate(ci.getId());
                }
            }
        }
        long endCount = sportEventsCache.size();
        long diff = startCount - endCount;
        logger.info("Deleted {} items from cache [before={}].", diff, before);
        return (int) diff;
    }

    private SportEventCi provideEventCi(Urn id)
        throws CacheItemNotFoundException, IllegalCacheStateException {
        Preconditions.checkNotNull(id);

        Class mappedClazz = provideMappingType(id);
        if (isTournamentCiType(mappedClazz)) {
            return cacheItemFactory.buildTournamentCi(id);
        } else if (mappedClazz == Match.class) {
            return cacheItemFactory.buildMatchCi(id);
        } else if (mappedClazz == Stage.class) {
            return provideStageDerivedCi(id);
        } else if (mappedClazz == Lottery.class) {
            return cacheItemFactory.buildLotteryCi(id);
        } else if (mappedClazz == Draw.class) {
            return cacheItemFactory.buildDrawCi(id);
        }

        throw new CacheItemNotFoundException(
            String.format("Unsupported caching URN identifier[%s] with clazz[%s]", id, mappedClazz.getName())
        );
    }

    private boolean isTournamentCiType(Class clazz) {
        Preconditions.checkNotNull(clazz);

        return clazz == Tournament.class || clazz == BasicTournament.class || clazz == Season.class;
    }

    private SportEventCi provideStageDerivedCi(Urn id)
        throws CacheItemNotFoundException, IllegalCacheStateException {
        Preconditions.checkNotNull(id);

        logger.debug("Pre-fetching summary endpoint(stage type detection)[{}]", id);
        try {
            dataRouterManager.requestSummaryEndpoint(defaultLocale, id, null);
        } catch (CommunicationException e) {
            throw new IllegalCacheStateException(
                "An error occurred while performing StageCI summary request[" + id + "]",
                e
            );
        }

        SportEventCi ifPresent = sportEventsCache.getIfPresent(id);
        if (ifPresent != null) {
            return ifPresent;
        }

        throw new CacheItemNotFoundException("StageCI[" + id + "] data could not be found");
    }

    private void storeTournamentInfoEndpoint(
        Urn tournamentId,
        SapiTournamentInfoEndpoint data,
        Locale dataLocale,
        CacheItem requester
    ) {
        Preconditions.checkNotNull(tournamentId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCi ifPresent = sportEventsCache.getIfPresent(tournamentId);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            Class mappingType;
            try {
                mappingType = provideMappingType(tournamentId);
            } catch (IllegalCacheStateException e) {
                logger.warn(
                    "SportEventCache.onTournamentInfoEndpointFetched -> Failed to provide valid mapping type for id [{}]",
                    tournamentId
                );
                return;
            }

            if (isTournamentCiType(mappingType)) {
                sportEventsCache.put(
                    tournamentId,
                    cacheItemFactory.buildTournamentCi(tournamentId, data, dataLocale)
                );
            } else if (mappingType.equals(Stage.class)) {
                sportEventsCache.put(
                    tournamentId,
                    cacheItemFactory.buildStageCi(tournamentId, data, dataLocale)
                );
            } else {
                logger.warn(
                    "SportEventCache.onTournamentInfoEndpointFetched -> discarding data, mapping type not supported. id:{}, type:{}",
                    tournamentId,
                    mappingType
                );
            }
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    private Class provideMappingType(Urn id) throws IllegalCacheStateException {
        Preconditions.checkNotNull(id);

        return mappingTypeProvider
            .getMappingType(id)
            .orElseThrow(() ->
                new IllegalCacheStateException(String.format("Error providing mapping type for [%s]", id))
            );
    }

    /**
     * Exports current items in the cache
     *
     * @return List of {@link ExportableCi} containing all the items currently in the cache
     */
    @Override
    public List<ExportableCi> exportItems() {
        return sportEventsCache
            .asMap()
            .values()
            .stream()
            .map(i -> (ExportableCacheItem) i)
            .map(ExportableCacheItem::export)
            .collect(Collectors.toList());
    }

    /**
     * Imports provided items into the cache
     *
     * @param items List of {@link ExportableCi} to be inserted into the cache
     */
    @Override
    public void importItems(List<ExportableCi> items) {
        Preconditions.checkNotNull(items);
        items.forEach(exportable -> {
            if (!(exportable instanceof ExportableSportEventCi)) {
                return;
            }

            Urn id = Urn.parse(exportable.getId());
            SportEventCi sportEvent = cacheItemFactory.buildSportEventCi(exportable);
            SportEventCi ifPresent = sportEventsCache.getIfPresent(id);
            if (ifPresent == null) sportEventsCache.put(id, sportEvent); else ifPresent.merge(
                exportable,
                null
            );
        });
    }

    /**
     * Returns current cache status
     *
     * @return A map containing all cache item types in the cache and their counts
     */
    @Override
    public Map<String, Long> cacheStatus() {
        Map<String, Long> status = new HashMap<>(
            sportEventsCache
                .asMap()
                .values()
                .stream()
                .map(c -> c.getClass().getSimpleName())
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
        );
        String[] classes = {
            "MatchCIImpl",
            "RaceStageCIImpl",
            "TournamentStageCIImpl",
            "TournamentCIImpl",
            "LotteryCIImpl",
            "DrawCIImpl",
        };
        for (String clazz : classes) {
            if (!status.containsKey(clazz)) status.put(clazz, 0L);
        }
        return status;
    }
}
