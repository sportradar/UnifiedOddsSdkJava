/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * An interface providing methods to access {@link EventClock} implementation values
 */
public interface EventClock {
    /**
     * Returns the event time of the sport event associated with the current instance
     *
     * @return - the event time of the sport event associated with the current instance
     */
    String getEventTime();

    /**
     * Returns the time at which the event associated with the current instance has been stopped
     *
     * @return - the time at which the event associated with the current instance has been stopped
     */
    String getStoppageTime();

    /**
     * Returns the time at which the stoppage time has been announced
     *
     * @return - the time at which the stoppage time has been announced
     */
    String getStoppageTimeAnnounced();

    /**
     * Returns the remaining event time
     *
     * @return - the remaining event time if available; otherwise null
     */
    String getRemainingTime();

    /**
     * Returns the remaining period time
     *
     * @return - the remaining period time if available; otherwise null
     */
    String getRemainingTimeInPeriod();

    /**
     * Returns an indication if the event clock is currently stopped
     *
     * @return - an indication if the event clock is currently stopped if available; otherwise null
     */
    Boolean getStopped();
}
