/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.entities.FixtureChange;
import com.sportradar.unifiedodds.sdk.entities.ResultChange;

import java.time.Duration;
import java.util.Date;

/**
 * Defines methods used for getting list of {@link FixtureChange} and/or {@link ResultChange}
 */
public interface EventChangeManager {

    /**
     * Sets the {@link EventChangeListener}
     * @param listener the event change listener
     */
    void setListener(EventChangeListener listener);

    /**
     * Returns the timestamp of last processed fixture change
     * @return the timestamp of last processed fixture change
     */
    Date getLastFixtureChange();

    /**
     * Returns the timestamp of last processed result change
     * @return the timestamp of last processed result change
     */
    Date getLastResultChange();

    /**
     * Returns the interval for getting new list of fixture changes
     * @return the interval for getting new list of fixture changes
     */
    Duration getFixtureChangeInterval();

    /**
     * Returns the interval for getting new list of result changes
     * @return the interval for getting new list of result changes
     */
    Duration getResultChangeInterval();

    /**
     * Returns a value indicating whether this instance is running
     * @return a value indicating whether this instance is running
     */
    boolean isRunning();

    /**
     * Sets the fixture change interval between two Sports API requests. Must be between 1 min and 12 hours.
     * @param fixtureChangeInterval the fixture change interval between two Sports API requests
     */
    void setFixtureChangeInterval(Duration fixtureChangeInterval);

    /**
     * Sets the result change interval between two Sports API requests. Must be between 1 min and 12 hours.
     * @param resultChangeInterval the result change interval between two Sports API requests
     */
    void setResultChangeInterval(Duration resultChangeInterval);

    /**
     * Sets the last processed fixture change timestamp
     * It can be only set when it is stopped
     * @param fixtureChangeTimestamp the last processed fixture change timestamp
     */
    void setFixtureChangeTimestamp(Date fixtureChangeTimestamp);

    /**
     * Sets the last processed result change timestamp
     * It can be only set when it is stopped
     * @param resultChangeTimestamp the last processed result change timestamp
     */
    void setResultChangeTimestamp(Date resultChangeTimestamp);

    /**
     * Starts scheduled job for fetching fixture and result changes
     */
    void start();

    /**
     * Stops scheduled job for fetching fixture and result changes
     */
    void stop();
}
