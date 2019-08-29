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
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.impl.MappingTypeProvider;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Implements methods used to access sport event data
 */
public class SportEventCacheImpl implements SportEventCache, DataRouterListener {
    /**
     * The {@link Logger} instance used to log {@link SportEventCacheImpl} events
     */
    private static final Logger logger = LoggerFactory.getLogger(SportEventCacheImpl.class);

    /**
     * A {@link Cache} instance used to cache sport events data
     */
    private final Cache<URN, SportEventCI> sportEventsCache;

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
    SportEventCacheImpl(CacheItemFactory cacheItemFactory,
                        MappingTypeProvider mappingTypeProvider,
                        DataRouterManager dataRouterManager,
                        SDKInternalConfiguration sdkInternalConfiguration,
                        Cache<URN, SportEventCI> sportEventsCache) {
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
     * Returns a {@link SportEventCI} instance representing a cached sport event data
     *
     * @param id an {@link URN} specifying the id of the sport event
     * @return a {@link SportEventCI} instance representing cached sport event data
     */
    @Override
    public SportEventCI getEventCacheItem(URN id) throws CacheItemNotFoundException {
        Preconditions.checkNotNull(id);

        try {
            return sportEventsCache.get(id, () -> {
                logger.info("Cache miss for[{}], providing CI", id);
                try {
                    return provideEventCI(id);
                } catch (IllegalCacheStateException e) {
                    throw new CacheItemNotFoundException(String.format("An error occurred while loading a new cache item '%s', ex: ", id), e);
                }
            });
        } catch (ExecutionException e) {
            throw new CacheItemNotFoundException(String.format("Cache item could not be loaded[%s], ex: ", id), e);
        }
    }

    /**
     * Returns a {@link List} containing id's of sport events, which belong to a specific tournament
     *
     * @param tournamentId an {@link URN} specifying the id of the tournament to which the events should relate
     * @param locale the locale to fetch the data
     * @return a {@link List} containing id's of sport events, which belong to the specified tournament
     */
    @Override
    public List<URN> getEventIds(URN tournamentId, Locale locale) throws IllegalCacheStateException {
        logger.debug("Providing tournament[{}] event IDs", tournamentId);
        try {
            if(locale == null) {
                return dataRouterManager.requestEventsFor(defaultLocale, tournamentId);
            }
            else{
                return dataRouterManager.requestEventsFor(locale, tournamentId);
            }
        } catch (CommunicationException e) {
            throw new IllegalCacheStateException("Error occurred while fetching tournament schedule[" + tournamentId + "]", e);
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
    public List<URN> getEventIds(Date date, Locale locale) throws IllegalCacheStateException {
        logger.debug("Providing event IDs for {}", date == null ? "live" : date);
        try {
            if(locale == null) {
                return dataRouterManager.requestEventsFor(defaultLocale, date);
            }
            else{
                return dataRouterManager.requestEventsFor(locale, date);
            }
        } catch (CommunicationException e) {
            throw new IllegalCacheStateException("Error occurred while fetching date schedule for " + (date == null ? "live" : date), e);
        }
    }

    /**
     * Purges an item from the {@link SportEventCache}
     *
     * @param id The {@link URN} specifying the event which should be purged
     */
    @Override
    public void purgeCacheItem(URN id) {
        if (id == null) {
            return;
        }

        logger.debug("Purging CI[{}]", id);
        sportEventsCache.invalidate(id);
    }

    @Override
    public void onSportEventFetched(URN id, SAPISportEvent data, Locale dataLocale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCI ifPresent = sportEventsCache.getIfPresent(id);
        if (ifPresent == null) {
            Class mappingType;
            try {
                mappingType = provideMappingType(id);
            } catch (IllegalCacheStateException e) {
                logger.warn("SportEventCache.onSportEventFetched -> Failed to provide valid mapping type for id [{}]", id);
                return;
            }

            if (mappingType.equals(Match.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildMatchCI(id, data, dataLocale));
            } else if (mappingType.equals(Stage.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildStageCI(id, data, dataLocale));
            }
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    public void onChildSportEventFetched(URN id, SAPISportEventChildren.SAPISportEvent data, Locale dataLocale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCI ifPresent = sportEventsCache.getIfPresent(id);
        if (ifPresent == null) {
            Class mappingType;
            try {
                mappingType = provideMappingType(id);
            } catch (IllegalCacheStateException e) {
                logger.warn("SportEventCache.onChildSportEventFetched -> Failed to provide valid mapping type for id [{}]", id);
                return;
            }

            if (mappingType.equals(Match.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildMatchCI(id, data, dataLocale));
            } else if (mappingType.equals(Stage.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildStageCI(id, data, dataLocale));
            }
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    public void onTournamentExtendedFetched(URN id, SAPITournamentExtended data, Locale dataLocale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCI ifPresent = sportEventsCache.getIfPresent(id);
        if (ifPresent == null) {
            Class mappingType;
            try {
                mappingType = provideMappingType(id);
            } catch (IllegalCacheStateException e) {
                logger.warn("SportEventCache.onTournamentExtendedFetched -> Failed to provide valid mapping type for id [{}]", id);
                return;
            }

            if (isTournamentCIType(mappingType)) {
                sportEventsCache.put(id, cacheItemFactory.buildTournamentCI(id, data, dataLocale));
            } else if (mappingType.equals(Stage.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildStageCI(id, data, dataLocale));
            } else {
                logger.warn("SportEventCache.onTournamentExtendedFetched -> discarding data, mapping type not supported. id:{}, type:{}", id, mappingType);
            }
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    public void onTournamentInfoEndpointFetched(URN requestedId, URN tournamentId, URN seasonId, SAPITournamentInfoEndpoint data, Locale dataLocale, CacheItem requester) {
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
    public void onStageSummaryEndpointFetched(URN id, SAPIStageSummaryEndpoint data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCI ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            sportEventsCache.put(id, cacheItemFactory.buildStageCI(id, data, dataLocale));
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    public void onMatchSummaryEndpointFetched(URN id, SAPIMatchSummaryEndpoint data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCI ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            sportEventsCache.put(id, cacheItemFactory.buildMatchCI(id, data, dataLocale));
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    public void onFixtureFetched(URN id, SAPIFixture data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCI ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            Class mappingType;
            try {
                mappingType = provideMappingType(id);
            } catch (IllegalCacheStateException e) {
                logger.warn("SportEventCache.onFixtureFetched -> Failed to provide valid mapping type for id [{}]", id);
                return;
            }

            if (mappingType.equals(Match.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildMatchCI(id, data, dataLocale));
            } else if (mappingType.equals(Stage.class)) {
                sportEventsCache.put(id, cacheItemFactory.buildStageCI(id, data, dataLocale));
            } else {
                logger.warn("SportEventCache.onFixtureFetched -> discarding data, mapping type not supported. id:{}, type:{}", id, mappingType);
            }
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    public void onTournamentFetched(URN id, SAPITournament data, Locale locale) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(locale);

        SportEventCI ifPresent = sportEventsCache.getIfPresent(id);
        if (ifPresent == null) {
            Class mappingType;
            try {
                mappingType = provideMappingType(id);
            } catch (IllegalCacheStateException e) {
                logger.warn("SportEventCache.onTournamentFetched -> Failed to provide valid mapping type for id [{}]", id);
                return;
            }

            if (isTournamentCIType(mappingType)) {
                sportEventsCache.put(id, cacheItemFactory.buildTournamentCI(id, data, locale));
            } else if (mappingType == Stage.class) {
                sportEventsCache.put(id, cacheItemFactory.buildStageCI(id, data, locale));
            } else {
                logger.warn("SportEventCache.onTournamentFetched -> discarding data, mapping type not supported. id:{}, type:{}", id, mappingType);
            }
        } else {
            ifPresent.merge(data, locale);
        }
    }

    @Override
    public void onMatchTimelineFetched(URN id, SAPIMatchTimelineEndpoint data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCI ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        // we only merge such data, this request wont be triggered before the cache item is created anyway
        if (ifPresent != null) {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    public void onLotteryFetched(URN id, SAPILottery data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCI ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            sportEventsCache.put(id, cacheItemFactory.buildLotteryCI(id, data, dataLocale));
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    @SuppressWarnings("Duplicates") // its not a duplicate, different CI factory method
    public void onDrawFetched(URN id, SAPIDrawEvent data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCI ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            sportEventsCache.put(id, cacheItemFactory.buildDrawCI(id, data, dataLocale));
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    @SuppressWarnings("Duplicates") // its not a duplicate, different CI factory method
    public void onDrawFixtureFetched(URN id, SAPIDrawFixture data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCI ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            sportEventsCache.put(id, cacheItemFactory.buildDrawCI(id, data, dataLocale));
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    @Override
    @SuppressWarnings("Duplicates") // its not a duplicate, different CI factory method
    public void onDrawSummaryEndpointFetched(URN id, SAPIDrawSummary data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCI ifPresent = sportEventsCache.getIfPresent(id);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            sportEventsCache.put(id, cacheItemFactory.buildDrawCI(id, data, dataLocale));
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    /**
     * Method that gets triggered when the associated event gets booked trough the {@link BookingManager}
     *
     * @param id the {@link URN} of the event that was successfully booked
     */
    @Override
    public void onEventBooked(URN id) {
        SportEventCI ifPresent = sportEventsCache.getIfPresent(id);
        if (ifPresent instanceof CompetitionCI) {
            ((CompetitionCI) ifPresent).onEventBooked();
        } else {
            logger.warn("Received onEventBooked event for an unsupported event type, id: {}", id);
        }
    }

    /**
     * Adds fixture timestamp to cache so that the next fixture calls for the event goes through non-cached fixture provider
     *
     * @param id the {@link URN} of the event
     */
    @Override
    public void addFixtureTimestamp(URN id) {
        Cache<URN, Date> cache = cacheItemFactory.getFixtureTimestampCache();
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
        for (SportEventCI ci : sportEventsCache.asMap().values()) {
            if(ci.getScheduledRaw() != null){
                if(ci.getScheduledRaw().before(before)){
                    sportEventsCache.invalidate(ci.getId());
                }
            }
            else if (ci.getScheduledEndRaw() != null){
                if(ci.getScheduledEndRaw().before(before)){
                    sportEventsCache.invalidate(ci.getId());
                }
            }
        }
        long endCount = sportEventsCache.size();
        long diff = startCount - endCount;
        logger.info("Deleted {} items from cache.", diff);
        return (int)diff;
    }

    private SportEventCI provideEventCI(URN id) throws CacheItemNotFoundException, IllegalCacheStateException {
        Preconditions.checkNotNull(id);

        Class mappedClazz = provideMappingType(id);
        if (isTournamentCIType(mappedClazz)) {
            return cacheItemFactory.buildTournamentCI(id);
        } else if (mappedClazz == Match.class) {
            return cacheItemFactory.buildMatchCI(id);
        } else if (mappedClazz == Stage.class) {
            return provideStageDerivedCI(id);
        } else if (mappedClazz == Lottery.class) {
            return cacheItemFactory.buildLotteryCI(id);
        } else if (mappedClazz == Draw.class) {
            return cacheItemFactory.buildDrawCI(id);
        }

        throw new CacheItemNotFoundException(String.format("Unsupported caching URN identifier[%s] with clazz[%s]", id, mappedClazz.getName()));
    }

    private boolean isTournamentCIType(Class clazz) {
        Preconditions.checkNotNull(clazz);

        return clazz == Tournament.class || clazz == BasicTournament.class || clazz == Season.class;
    }

    private SportEventCI provideStageDerivedCI(URN id) throws CacheItemNotFoundException, IllegalCacheStateException {
        Preconditions.checkNotNull(id);

        logger.debug("Pre-fetching summary endpoint(stage type detection)[{}]", id);
        try {
            dataRouterManager.requestSummaryEndpoint(defaultLocale, id, null);
        } catch (CommunicationException e) {
            throw new IllegalCacheStateException("An error occurred while performing StageCI summary request[" + id + "]", e);
        }

        SportEventCI ifPresent = sportEventsCache.getIfPresent(id);
        if (ifPresent != null) {
            return ifPresent;
        }

        throw new CacheItemNotFoundException("StageCI[" + id + "] data could not be found");
    }

    private void storeTournamentInfoEndpoint(URN tournamentId, SAPITournamentInfoEndpoint data, Locale dataLocale, CacheItem requester) {
        Preconditions.checkNotNull(tournamentId);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        SportEventCI ifPresent = sportEventsCache.getIfPresent(tournamentId);

        if (requester != null && !Equivalence.identity().equivalent(ifPresent, requester)) {
            requester.merge(data, dataLocale);
        }

        if (ifPresent == null) {
            Class mappingType;
            try {
                mappingType = provideMappingType(tournamentId);
            } catch (IllegalCacheStateException e) {
                logger.warn("SportEventCache.onTournamentInfoEndpointFetched -> Failed to provide valid mapping type for id [{}]", tournamentId);
                return;
            }

            if (isTournamentCIType(mappingType)) {
                sportEventsCache.put(tournamentId, cacheItemFactory.buildTournamentCI(tournamentId, data, dataLocale));
            } else if (mappingType.equals(Stage.class)) {
                sportEventsCache.put(tournamentId, cacheItemFactory.buildStageCI(tournamentId, data, dataLocale));
            } else {
                logger.warn("SportEventCache.onTournamentInfoEndpointFetched -> discarding data, mapping type not supported. id:{}, type:{}", tournamentId, mappingType);
            }
        } else {
            ifPresent.merge(data, dataLocale);
        }
    }

    private Class provideMappingType(URN id) throws IllegalCacheStateException {
        Preconditions.checkNotNull(id);

        return mappingTypeProvider.getMappingType(id)
                .orElseThrow(() -> new IllegalCacheStateException(String.format("Error providing mapping type for [%s]", id)));
    }
}
