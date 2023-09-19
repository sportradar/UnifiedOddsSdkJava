/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.sportradar.unifiedodds.sdk.entities.EventClock;

/**
 * Represents an event clock
 */
@SuppressWarnings({ "UnnecessaryParentheses" })
public class EventClockImpl implements EventClock {

    /**
     * The current event time
     */
    private final String eventTime;

    /**
     * The event stoppage time
     */
    private final String stoppageTime;

    /**
     * The time in which the event stoppage time was announced
     */
    private final String stoppageTimeAnnounced;

    /**
     * The remaining event time
     */
    private final String remainingTime;

    /**
     * The remaining period time
     */
    private final String remainingTimeInPeriod;

    /**
     * Indicates if the event clock is currently stopped
     */
    private final Boolean stopped;

    /**
     * Initializes a new instance of {@link EventClockImpl}
     *
     * @param eventTime - a {@link String} description of the current event time
     * @param stoppageTime - a {@link String} description of the stoppage time
     * @param stoppageTimeAnnounced - a {@link String}  description of when the stoppage time was announced
     * @param remainingTime - a {@link String} description of the remaining time
     * @param remainingTimeInPeriod - a {@link String} description of the remaining period time
     * @param stopped - an indication if the event clock is currently stopped
     */
    public EventClockImpl(
        String eventTime,
        String stoppageTime,
        String stoppageTimeAnnounced,
        String remainingTime,
        String remainingTimeInPeriod,
        Boolean stopped
    ) {
        this.eventTime = eventTime;
        this.stoppageTime = stoppageTime;
        this.stoppageTimeAnnounced = stoppageTimeAnnounced;
        this.remainingTime = remainingTime;
        this.remainingTimeInPeriod = remainingTimeInPeriod;
        this.stopped = stopped;
    }

    /**
     * Returns the current event time
     *
     * @return - the current event time
     */
    @Override
    public String getEventTime() {
        return eventTime;
    }

    /**
     * Returns te event stoppage time
     *
     * @return - te event stoppage time
     */
    @Override
    public String getStoppageTime() {
        return stoppageTime;
    }

    /**
     * Returns the time in which the event stoppage time was announced
     *
     * @return - the time in which the event stoppage time was announced
     */
    @Override
    public String getStoppageTimeAnnounced() {
        return stoppageTimeAnnounced;
    }

    /**
     * Returns the remaining event time
     *
     * @return - the remaining event time if available; otherwise null
     */
    @Override
    public String getRemainingTime() {
        return remainingTime;
    }

    /**
     * Returns the remaining period time
     *
     * @return - the remaining period time if available; otherwise null
     */
    @Override
    public String getRemainingTimeInPeriod() {
        return remainingTimeInPeriod;
    }

    /**
     * Returns an indication if the event clock is currently stopped
     *
     * @return - an indication if the event clock is currently stopped if available; otherwise null
     */
    @Override
    public Boolean getStopped() {
        return stopped;
    }

    /**
     * Returns a {@link String} describing the current {@link EventClock} instance
     *
     * @return - a {@link String} describing the current {@link EventClock} instance
     */
    @Override
    public String toString() {
        return (
            "EventClockImpl{" +
            "eventTime=" +
            eventTime +
            ", stoppageTime=" +
            stoppageTime +
            ", stoppageTimeAnnounced=" +
            stoppageTimeAnnounced +
            ", remainingTime=" +
            remainingTime +
            ", remainingTimeInPeriod=" +
            remainingTimeInPeriod +
            ", stopped=" +
            stopped +
            '}'
        );
    }
}
