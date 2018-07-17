/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.URN;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Defines methods used to access data of a fixture
 *
 * <i>A Fixture is a sport event that has been arranged for a particular time and place</i>
 */
public interface Fixture {

    /**
     * Returns the {@link Date} instance specifying when the fixture is scheduled to start
     *
     * @return - the {@link Date} instance specifying when the fixture is scheduled to start
     */
    Date getStartTime();

    /**
     * Returns the value indicating whether the start time of the fixture has been confirmed
     *
     * @return - the value indicating whether the start time of the fixture has been confirmed
     */
    boolean isStartTimeConfirmed();

    /**
     * An indication if the start tam is yet to be defined
     *
     * @return an indication if the start tam is yet to be defined
     */
    Boolean getStartTimeTbd();

    /**
     * Returns the {@link Date} instance specifying the live time in case the fixture was re-schedule,
     * or a null reference if the fixture was not re-scheduled
     *
     * @return - the {@link Date} instance specifying the live time in case the fixture was re-schedule,
     * or a null reference if the fixture was not re-scheduled
     */
    Date getNextLiveTime();

    /**
     * Returns an unmodifiable {@link Map} containing additional information about the fixture
     *
     * @return - an unmodifiable {@link Map} containing additional information about the fixture
     */
    Map<String, String> getExtraInfo();

    /**
     * Returns an unmodifiable {@link List} representing TV channels covering the sport event
     *
     * @return - an unmodifiable {@link List} representing TV channels covering the sport event
     */
    List<TvChannel> getTvChannels();

    /**
     * Returns the {@link CoverageInfo} instance specifying what coverage is available for the sport event
     *
     * @return - the {@link CoverageInfo} instance specifying what coverage is available for the sport event
     */
    CoverageInfo getCoverageInfo();

    /**
     * Returns the {@link ProducerInfo} instance providing sportradar related information about the sport event associated
     *
     * @return - the {@link ProducerInfo} instance providing sportradar related information about the sport event associated
     */
    ProducerInfo getProducerInfo();

    /**
     * Returns the reference ids
     *
     * @return - the reference ids
     */
    Reference getReferences();

    /**
     * Returns the {@link URN} identifier of the replacement event
     *
     * @return the {@link URN} identifier of the replacement event
     */
    URN getReplacedBy();

    /**
     * Returns the list of all {@link ScheduledStartTimeChange} to start time
     * @return the list of all {@link ScheduledStartTimeChange} to start time
     */
    List<ScheduledStartTimeChange> getScheduledStartTimeChanges();
}
