/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.Urn;
import java.util.Date;
import java.util.Locale;

/**
 * Defines methods implemented by classes representing the target(tournament, match, race) of feed messages
 */
@SuppressWarnings({ "LineLength" })
public interface SportEvent {
    /**
     * Returns an {@link Urn} uniquely identifying the tournament associated with the current instance
     *
     * @return - an {@link Urn} uniquely identifying the tournament associated with the current instance
     */
    Urn getId();

    /**
     * Returns the sport event name
     *
     * @param locale the {@link Locale} in which the name should be provided
     * @return the sport event name if available; otherwise null
     */
    String getName(Locale locale);

    /**
     * Returns the unique sport identifier to which this event is associated
     *
     * @return - the unique sport identifier to which this event is associated
     */
    Urn getSportId();

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled
     *
     * @return - a {@link Date} instance specifying when the sport event associated with the current
     * instance was scheduled
     */
    Date getScheduledTime();

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled to end
     *
     * @return - a {@link Date} instance specifying when the sport event associated with the current
     * instance was scheduled to end
     */
    Date getScheduledEndTime();

    /**
     * Returns the {@link Boolean} specifying if the start time to be determined is set for the current instance
     *
     * @return if available, the {@link Boolean} specifying if the start time to be determined is set for the current instance
     */
    @SuppressWarnings("java:S2447") // Null should not be returned from a "Boolean" method
    default Boolean isStartTimeTbd() {
        return null;
    }

    /**
     * Returns the {@link Urn} specifying the replacement sport event for the current instance
     *
     * @return if available, the {@link Urn} specifying the replacement sport event for the current instance
     */
    default Urn getReplacedBy() {
        return null;
    }
}
