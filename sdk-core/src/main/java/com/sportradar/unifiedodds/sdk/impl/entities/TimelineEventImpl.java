/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.TimelineEventCI;
import com.sportradar.unifiedodds.sdk.entities.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

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

    /**
     * Returns the period to which the timeline event belongs to
     *
     * @return the period
     */
    @Override
    public String getPeriod() {
        return cacheItem.getPeriod();
    }

    /**
     * Returns the points
     *
     * @return the points
     */
    @Override
    public String getPoints() {
        return cacheItem.getPoints();
    }

    /**
     * Returns the stoppage time
     *
     * @return the stoppage time
     */
    @Override
    public String getStoppageTime() {
        return cacheItem.getStoppageTime();
    }

    /**
     * Returns the value
     *
     * @return the value
     */
    @Override
    public String getValue() {
        return cacheItem.getValue();
    }

    /**
     * Returns the X value
     *
     * @return the X value
     */
    @Override
    public Integer getX() {
        return cacheItem.getX();
    }

    /**
     * Returns the Y value
     *
     * @return the Y value
     */
    @Override
    public Integer getY() {
        return cacheItem.getY();
    }

    /**
     * Returns the match status code
     *
     * @return the match status code
     */
    @Override
    public Integer getMatchStatusCode() {
        return cacheItem.getMatchStatusCode();
    }

    /**
     * Returns the match clock
     *
     * @return the match clock
     */
    @Override
    public String getMatchClock() {
        return cacheItem.getMatchClock();
    }

    /**
     * Returns the period to which the timeline event belongs to
     *
     * @return the period
     */
    @Override
    public GoalScorer getGoalScorer() {
        if(cacheItem.getGoalScorer() == null)
            return null;

        return new GoalScorerImpl(cacheItem.getGoalScorer(), dataLocale);
    }

    /**
     * Returns the period to which the timeline event belongs to
     *
     * @return the period
     */
    @Override
    public EventPlayer getPlayer() {
        if(cacheItem.getPlayer() == null)
            return null;

        return new EventPlayerImpl(cacheItem.getPlayer(), dataLocale);
    }

    /**
     * Returns the period to which the timeline event belongs to
     *
     * @return the period
     */
    @Override
    public List<Assist> getAssists() {
        if(cacheItem.getAssists() == null || cacheItem.getAssists().isEmpty())
            return null;

        return cacheItem.getAssists().stream().map(m-> {
            HashMap<Locale, String> names = new HashMap<>();
            names.put(dataLocale, m.getName());
            return new AssistImpl(m.getId(), names, m.getType());
        }).collect(Collectors.toList());
    }

    /**
     * Returns a {@link String} describing the current {@link TimelineEvent} instance
     *
     * @return - a {@link String} describing the current {@link TimelineEvent} instance
     */
    @Override
    public String toString() {
        int assistsCount = getAssists() == null ? 0 : getAssists().size();

        return "TimelineEventImpl{" +
                "id=" + getId() +
                ", homeScore=" + getHomeScore() +
                ", awayScore=" + getAwayScore() +
                ", matchTime=" + getMatchTime() +
                ", period=" + getPeriod() +
                ", periodName=" + getPeriodName() +
                ", point=" + getPoints() +
                ", x=" + getX() +
                ", y=" + getY() +
                ", type=" + getType() +
                ", team=" + getTeam() +
                ", matchStatusCode=" + getMatchStatusCode() +
                ", matchClock=" + getMatchClock() +
                ", goalScorer=" + getGoalScorer() +
                ", player=" + getPlayer() +
                ", assists=" + assistsCount +
                ", value=" + getValue() +
                '}';
    }
}
