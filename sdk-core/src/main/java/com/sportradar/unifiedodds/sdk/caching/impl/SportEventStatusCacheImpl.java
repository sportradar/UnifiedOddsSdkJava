/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.sportradar.unifiedodds.sdk.RuntimeConfiguration;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.SportEventStatusCiImpl;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDto;
import com.sportradar.utils.Urn;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cache storing sport event statuses
 */
@SuppressWarnings(
    {
        "ConstantName",
        "CyclomaticComplexity",
        "EqualsAvoidNull",
        "LineLength",
        "MagicNumber",
        "MethodLength",
        "ReturnCount",
    }
)
public class SportEventStatusCacheImpl implements SportEventStatusCache, DataRouterListener {

    private static final Logger logger = LoggerFactory.getLogger(SportEventStatusCacheImpl.class);

    /**
     * A {@link Cache} instance used to store sport event statuses
     */
    private final Cache<String, SportEventStatusCi> sportEventStatusCache;

    /**
     * The {@link SportEventCache} instance that stores sport events
     */
    private final SportEventCache sportEventCache;

    /**
     * A {@link Cache} instance used to store event ids for which timeline SES should be ignored
     */
    private final Cache<String, Date> ignoreEventsTimelineCache;

    /**
     * Initializes a new {@link SportEventStatusCacheImpl} instance
     *
     * @param sportEventStatusCache - the {@link Cache} instance used to store sport event statuses
     * @param sportEventCache - the {@link SportEventCache} instance used to fetch sport event
     *                          statuses if they are not yet cached
     * @param ignoreEventsTimelineCache - the {@link Cache} instance used to store event ids for which timeline SES should be ignored
     */
    public SportEventStatusCacheImpl(
        Cache<String, SportEventStatusCi> sportEventStatusCache,
        SportEventCache sportEventCache,
        Cache<String, Date> ignoreEventsTimelineCache
    ) {
        Preconditions.checkNotNull(sportEventStatusCache);
        Preconditions.checkNotNull(sportEventCache);
        Preconditions.checkNotNull(ignoreEventsTimelineCache);

        this.sportEventStatusCache = sportEventStatusCache;
        this.sportEventCache = sportEventCache;
        this.ignoreEventsTimelineCache = ignoreEventsTimelineCache;
    }

    /**
     * Returns the status of the event associated with the provided identifier. If the instance associated with
     * the specified event is not found, a {@link SportEventStatusCi} instance indicating a 'not started' event is returned.
     *
     * @param eventId the event identifier
     * @param makeApiCall should the API call be made if necessary
     * @return a {@link SportEventStatusCi} instance describing the last known event status
     */
    @Override
    public SportEventStatusCi getSportEventStatusCi(Urn eventId, boolean makeApiCall) {
        Preconditions.checkNotNull(eventId);

        SportEventStatusCi statusCi = sportEventStatusCache.getIfPresent(eventId.toString());

        if (statusCi != null || !makeApiCall) {
            return statusCi;
        }

        tryFetchCacheSportEventStatus(eventId);
        statusCi = sportEventStatusCache.getIfPresent(eventId.toString());

        if (statusCi == null) {
            statusCi = new SportEventStatusCiImpl(null, SportEventStatusDto.getNotStarted());
        }

        return statusCi;
    }

    /**
     * Adds a new {@link #sportEventStatusCache} entry
     *
     * @param id - the unique identifier of the sport event to which the status belongs to
     * @param data - a {@link SportEventStatusDto} to store in the cache
     * @param statusOnEvent - a status obtained directly on the sport event
     * @param source - a source of the data
     */
    @Override
    public void onSportEventStatusFetched(
        Urn id,
        SportEventStatusDto data,
        String statusOnEvent,
        String source
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(data);

        // sportEventStatus from oddsChange message has priority
        if (
            source.equalsIgnoreCase("UfOddsChange") ||
            source.contains("Summary") ||
            sportEventStatusCache.getIfPresent(id.toString()) == null
        ) {
            Date d = ignoreEventsTimelineCache.getIfPresent(id.toString());
            if (
                RuntimeConfiguration.getIgnoreBetPalTimelineSportEventStatus() &&
                source.contains("Timeline") &&
                d != null
            ) {
                logger.debug(
                    String.format(
                        "Received SES for %s from %s with EventStatus:%s (timeline ignored)",
                        id,
                        source,
                        data.getStatus()
                    )
                );
                return;
            }

            logger.debug(
                String.format("Received SES for %s from %s with EventStatus:%s", id, source, data.getStatus())
            );

            SportEventStatusCi cacheItem = sportEventStatusCache.getIfPresent(id.toString());
            SportEventStatusDto feedDto = (cacheItem != null) ? cacheItem.getFeedStatusDto() : null;
            SportEventStatusDto sapiDto = (cacheItem != null) ? cacheItem.getSapiStatusDto() : null;

            if (source.equalsIgnoreCase("UfOddsChange")) {
                feedDto = data;
            } else {
                sapiDto = data;
            }

            if (cacheItem == null) {
                cacheItem = new SportEventStatusCiImpl(feedDto, sapiDto);
            } else {
                cacheItem.setFeedStatus(feedDto);
                cacheItem.setSapiStatus(sapiDto);
            }
            sportEventStatusCache.put(id.toString(), cacheItem);
            return;
        }
        logger.debug(
            String.format(
                "Received SES for %s from %s with EventStatus:%s (ignored)",
                id,
                source,
                data.getStatus()
            )
        );
    }

    /**
     * Purges the sport event status associated with the provided event id
     *
     * @param id the id of the event that you want to purge the sport event status
     */
    @Override
    public void purgeSportEventStatus(Urn id) {
        sportEventStatusCache.invalidate(id.toString());
    }

    /**
     * Adds the event identifier for timeline ignore
     * Used for BetPal events to have ignored timeline event status cache
     *
     * @param eventId     the event identifier
     * @param producerId  the producer identifier
     * @param messageType type of the feed message
     */
    @Override
    public void addEventIdForTimelineIgnore(Urn eventId, int producerId, String messageType) {
        if (producerId == 4) { // BetPal
            Date d = ignoreEventsTimelineCache.getIfPresent(eventId.toString());
            if (d == null) {
                String msg = String.format(
                    "Received %s - added %s to the ignore timeline list",
                    messageType,
                    eventId
                );
                logger.debug(msg);
                ignoreEventsTimelineCache.put(eventId.toString(), new Date());
            }
        }
    }

    /**
     * Tries to load the sport event status from the associated event cache item, if the load was successful
     * the status gets cached
     *
     * @param eventId the {@link Urn} of the event that the status should be fetched-cached
     */
    private void tryFetchCacheSportEventStatus(Urn eventId) {
        Preconditions.checkNotNull(eventId);

        try {
            SportEventCi eventCacheItem = sportEventCache.getEventCacheItem(eventId);
            if (eventCacheItem instanceof CompetitionCi) {
                ((CompetitionCi) eventCacheItem).fetchSportEventStatus();
            } else {
                logger.warn(
                    "Received sport event[{}] status fetch-cache request for unsupported entity type: {}",
                    eventId,
                    eventCacheItem.getClass().getSimpleName()
                );
            }
        } catch (CacheItemNotFoundException e) {
            logger.warn(
                "Could not access a valid cache item for the requested event[{}] status, exc: ",
                eventId,
                e
            );
        }
    }
}
