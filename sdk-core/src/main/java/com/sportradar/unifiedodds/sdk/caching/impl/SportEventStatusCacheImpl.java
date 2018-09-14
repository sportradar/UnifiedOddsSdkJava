/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cache storing sport event statuses
 */
public class SportEventStatusCacheImpl implements SportEventStatusCache, DataRouterListener {

    private static final Logger logger = LoggerFactory.getLogger(SportEventStatusCacheImpl.class);

    /**
     * A {@link Cache} instance used to store sport event statuses
     */
    private final Cache<String, SportEventStatusDTO> sportEventStatusCache;

    /**
     * The {@link SportEventCache} instance that stores sport events
     */
    private final SportEventCache sportEventCache;

    /**
     * Initializes a new {@link SportEventStatusCacheImpl} instance
     *
     * @param sportEventStatusCache - the {@link Cache} instance used to store sport event statuses
     * @param sportEventCache - the {@link SportEventCache} instance used to fetch sport event
     *                          statuses if they are not yet cached
     */
    public SportEventStatusCacheImpl(Cache<String, SportEventStatusDTO> sportEventStatusCache,
                                     SportEventCache sportEventCache) {
        Preconditions.checkNotNull(sportEventStatusCache);
        Preconditions.checkNotNull(sportEventCache);

        this.sportEventStatusCache = sportEventStatusCache;
        this.sportEventCache = sportEventCache;
    }

    /**
     * Returns the status of the event associated with the provided identifier. If the instance associated with
     * the specified event is not found, a {@link SportEventStatusDTO} instance indicating a 'not started' event is returned.
     *
     * @param eventId the event identifier
     * @return a {@link SportEventStatusDTO} instance describing the last known event status
     */
    @Override
    public SportEventStatusDTO getSportEventStatusDTO(URN eventId) {
        Preconditions.checkNotNull(eventId);

        SportEventStatusDTO statusDto = sportEventStatusCache.getIfPresent(eventId.toString());

        if (statusDto != null) {
            return statusDto;
        }

        statusDto = tryFetchCacheSportEventStatus(eventId);

        if (statusDto == null) {
            statusDto = SportEventStatusDTO.getNotStarted();
        }

        return statusDto;
    }

    /**
    * Adds a new {@link #sportEventStatusCache} entry
    *
    * @param id - the unique identifier of the sport event to which the status belongs to
    * @param data - a {@link SportEventStatusDTO} to store in the cache
    * @param source - a source of the data
    */
    @Override
    public void onSportEventStatusFetched(URN id, SportEventStatusDTO data, String source) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);

        logger.info(String.format("Received SES for %s from %s with EventStatus:%s", id, source, data.getStatus()));

        sportEventStatusCache.put(id.toString(), data);
    }

    /**
     * Purges the sport event status associated with the provided event id
     *
     * @param id the id of the event that you want to purge the sport event status
     */
    @Override
    public void purgeSportEventStatus(URN id) {
        sportEventStatusCache.invalidate(id.toString());
    }

    /**
     * Tries to load the sport event status from the associated event cache item, if the load was successful
     * the status gets cached
     *
     * @param eventId the {@link URN} of the event that the status should be fetched-cached
     * @return if the fetch was successful the status of the event, otherwise null
     */
    private SportEventStatusDTO tryFetchCacheSportEventStatus(URN eventId) {
        Preconditions.checkNotNull(eventId);

        SportEventStatusDTO statusDto = null;
        try {
            SportEventCI eventCacheItem = sportEventCache.getEventCacheItem(eventId);
            if (eventCacheItem instanceof CompetitionCI) {
                statusDto = ((CompetitionCI) eventCacheItem).getSportEventStatusDTO();
            } else {
                logger.warn("Received sport event[{}] status fetch-cache request for unsupported entity type: {}", eventId, eventCacheItem.getClass().getSimpleName());
            }
        } catch (CacheItemNotFoundException e) {
            logger.warn("Could not access a valid cache item for the requested event[{}] status, exc: ", eventId, e);
        }

        if (statusDto != null) {
            onSportEventStatusFetched(eventId, statusDto, "CompetitionCI");
        }

        return statusDto;
    }
}
