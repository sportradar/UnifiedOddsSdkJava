/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.URN;

import java.util.Date;

/**
 * An interface providing methods to access stage details of a multi-staged race
 */
public interface ChildRace {
    /**
     * Returns a {@link URN} instance representing the id uniquely identifying the race represented by the current instance
     *
     * @return - a {@link URN} instance representing the id uniquely identifying the race represented by the current instance
     */
    URN getId();

    /**
     * Returns the name of the race represented by the current instance
     *
     * @return - the name of the race represented by the current instance
     */
    String getName();

    /**
     * Returns a {@link Date} specifying the scheduled start time of the race represented by the current instance
     *
     * @return - a {@link Date} specifying the scheduled start time; or null if the schedule start time is unknown
     */
    Date getScheduleTime();

    /**
     * Returns a {@link Date} specifying the scheduled end time of the race represented by the current instance
     *
     * @return - a {@link Date} specifying the scheduled end time; or null if the schedule end time is unknown
     */
    Date getScheduleEndTime();
}
