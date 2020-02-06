/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.SportEventStatusCIImpl;
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
    private final Cache<String, SportEventStatusCI> sportEventStatusCache;

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
    public SportEventStatusCacheImpl(Cache<String,
                                     SportEventStatusCI> sportEventStatusCache,
                                     SportEventCache sportEventCache) {
        Preconditions.checkNotNull(sportEventStatusCache);
        Preconditions.checkNotNull(sportEventCache);

        this.sportEventStatusCache = sportEventStatusCache;
        this.sportEventCache = sportEventCache;
    }

    /**
     * Returns the status of the event associated with the provided identifier. If the instance associated with
     * the specified event is not found, a {@link SportEventStatusCI} instance indicating a 'not started' event is returned.
     *
     * @param eventId the event identifier
     * @param makeApiCall should the API call be made if necessary
     * @return a {@link SportEventStatusCI} instance describing the last known event status
     */
    @Override
    public SportEventStatusCI getSportEventStatusCI(URN eventId, boolean makeApiCall) {
        Preconditions.checkNotNull(eventId);

        SportEventStatusCI statusCi = sportEventStatusCache.getIfPresent(eventId.toString());

        if (statusCi != null || !makeApiCall) {
            return statusCi;
        }

        tryFetchCacheSportEventStatus(eventId);
        statusCi = sportEventStatusCache.getIfPresent(eventId.toString());

        if (statusCi == null) {
            statusCi = new SportEventStatusCIImpl(null, SportEventStatusDTO.getNotStarted());
        }

        return statusCi;
    }

    /**
    * Adds a new {@link #sportEventStatusCache} entry
    *
    * @param id - the unique identifier of the sport event to which the status belongs to
    * @param data - a {@link SportEventStatusDTO} to store in the cache
    * @param statusOnEvent - a status obtained directly on the sport event
    * @param source - a source of the data
    */
    @Override
    public void onSportEventStatusFetched(URN id, SportEventStatusDTO data, String statusOnEvent, String source) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);

        // sportEventStatus from oddsChange message has priority
        if(source.equalsIgnoreCase("UFOddsChange") ||
                source.contains("Summary") ||
                sportEventStatusCache.getIfPresent(id.toString()) == null) {

            logger.debug(String.format("Received SES for %s from %s with EventStatus:%s", id, source, data.getStatus()));

            SportEventStatusCI cacheItem = sportEventStatusCache.getIfPresent(id.toString());
            SportEventStatusDTO feedDTO = (cacheItem != null) ? cacheItem.getFeedStatusDTO() : null;
            SportEventStatusDTO sapiDTO = (cacheItem != null) ? cacheItem.getSapiStatusDTO() : null;

            if (source.equalsIgnoreCase("UFOddsChange")) {
                feedDTO = data;
            }
            else {
                sapiDTO = data;
            }

            if (cacheItem == null) {
                cacheItem = new SportEventStatusCIImpl(feedDTO, sapiDTO);
            }
            else {
                cacheItem.setFeedStatus(feedDTO);
                cacheItem.setSapiStatus(sapiDTO);
            }
            sportEventStatusCache.put(id.toString(), cacheItem);
            return;
        }
        logger.debug(String.format("Received SES for %s from %s with EventStatus:%s (ignored)", id, source, data.getStatus()));
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
     */
    private void tryFetchCacheSportEventStatus(URN eventId) {
        Preconditions.checkNotNull(eventId);

        try {
            SportEventCI eventCacheItem = sportEventCache.getEventCacheItem(eventId);
            if (eventCacheItem instanceof CompetitionCI) {
                ((CompetitionCI) eventCacheItem).fetchSportEventStatus();
            } else {
                logger.warn("Received sport event[{}] status fetch-cache request for unsupported entity type: {}", eventId, eventCacheItem.getClass().getSimpleName());
            }
        } catch (CacheItemNotFoundException e) {
            logger.warn("Could not access a valid cache item for the requested event[{}] status, exc: ", eventId, e);
        }
    }
}
