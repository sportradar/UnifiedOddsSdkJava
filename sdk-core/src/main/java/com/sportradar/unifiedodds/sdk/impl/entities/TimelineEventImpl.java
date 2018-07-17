/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.TimelineEventCI;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.unifiedodds.sdk.entities.TimelineEvent;

import java.util.Date;
import java.util.Locale;

/**
 * An implementation of the {@link TimelineEvent} which uses a cache item for data access
 */
class TimelineEventImpl implements TimelineEvent {

    private TimelineEventCI cacheItem;
    private Locale dataLocale;


    TimelineEventImpl(TimelineEventCI cacheItem, Locale dataLocale) {
        Preconditions.checkNotNull(cacheItem);
        Preconditions.checkNotNull(dataLocale);

        this.cacheItem = cacheItem;
        this.dataLocale = dataLocale;
    }


    /**
     * Returns the timeline event identifier
     *
     * @return the timeline event identifier
     */
    @Override
    public int getId() {
        return cacheItem.getId();
    }

    /**
     * Returns the timeline event away score
     *
     * @return the away score
     */
    @Override
    public Double getAwayScore() {
        return cacheItem.getAwayScore();
    }

    /**
     * Returns the timeline event home score
     *
     * @return the home score
     */
    @Override
    public Double getHomeScore() {
        return cacheItem.getHomeScore();
    }

    /**
     * Returns the timeline event match time
     *
     * @return the match time
     */
    @Override
    public Integer getMatchTime() {
        return cacheItem.getMatchTime();
    }

    /**
     * Returns the name of the period to which the timeline event belongs to
     *
     * @return the period name
     */
    @Override
    public String getPeriodName() {
        return cacheItem.getPeriodName();
    }

    /**
     * Returns an indication of which team was the cause for the timeline event
     *
     * @return an indication of which team was the cause for the timeline event
     */
    @Override
    public HomeAway getTeam() {
        return cacheItem.getTeam();
    }

    /**
     * Returns the timeline event type
     *
     * @return the timeline event type
     */
    @Override
    public String getType() {
        return cacheItem.getType();
    }

    /**
     * Returns the timeline event time
     *
     * @return the timeline event time
     */
    @Override
    public Date getTime() {
        return cacheItem.getTime();
    }
}
