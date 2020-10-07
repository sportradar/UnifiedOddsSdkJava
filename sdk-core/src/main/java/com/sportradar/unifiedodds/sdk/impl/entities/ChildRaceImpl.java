/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.ChildRaceCI;
import com.sportradar.unifiedodds.sdk.entities.ChildRace;
import com.sportradar.utils.URN;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Represents a stage in a multi-staged race
 */
public class ChildRaceImpl implements ChildRace {
    /**
     * A {@link URN} representing the id uniquely identifying the race represented by the current instance
     */
    private final URN id;

    /**
     * The name of the race represented by the current instance
     */
    private final String name;

    /**
     * A {@link Date} specifying the scheduled start time of the race represented by the current instance
     */
    private final Date scheduleTime;

    /**
     * A {@link Date} specifying the scheduled end time of the race represented by the current instance
     */
    private final Date scheduleEndTime;


    /**
     * Initializes a new instance of the {@link ChildRaceImpl}
     *
     * @param childRaceCI - a {@link ChildRaceCI} containing information about the race
     * @param locales - a {@link List} of locales specifying the languages in which the translatable race info must be available
     */
    public ChildRaceImpl(ChildRaceCI childRaceCI, List<Locale> locales) {
        Preconditions.checkNotNull(childRaceCI);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        this.id = childRaceCI.getId();
        this.name = childRaceCI.getName();
        this.scheduleTime = childRaceCI.getSchedule();
        this.scheduleEndTime = childRaceCI.getScheduleEnd();
    }

    /**
     * Returns a {@link URN} instance representing the id uniquely identifying the race represented by the current instance
     *
     * @return - a {@link URN} instance representing the id uniquely identifying the race represented by the current instance
     */
    @Override
    public URN getId() {
        return id;
    }

    /**
     * Returns the name of the race represented by the current instance
     *
     * @return - the name of the race represented by the current instance
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns a {@link Date} specifying the scheduled start time of the race represented by the current instance
     *
     * @return - a {@link Date} specifying the scheduled start time; or null if the schedule start time is unknown
     */
    @Override
    public Date getScheduleTime() {
        return scheduleTime;
    }

    /**
     * Returns a {@link Date} specifying the scheduled end time of the race represented by the current instance
     *
     * @return - a {@link Date} specifying the scheduled end time; or null if the schedule end time is unknown
     */
    @Override
    public Date getScheduleEndTime() {
        return scheduleEndTime;
    }
}
