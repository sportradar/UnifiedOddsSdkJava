/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.utils.URN;

import java.util.Date;

/**
 * Defines methods used to access sport event type properties
 */
public interface SportEventCI extends CacheItem {
    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled
     *
     * @return if available, the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled; otherwise null;
     */
    Date getScheduled();

    /**
     * Returns the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled to end
     *
     * @return if available, the {@link Date} specifying when the sport event associated with the current
     * instance was scheduled to end; otherwise null;
     */
    Date getScheduledEnd();

    /**
     * Returns the {@link Boolean} specifying if the start time to be determined is set for the current instance
     *
     * @return if available, the {@link Boolean} specifying if the start time to be determined is set for the current instance
     */
    Boolean isStartTimeTbd();

    /**
     * Returns the {@link URN} specifying the replacement sport event for the current instance
     *
     * @return if available, the {@link URN} specifying the replacement sport event for the current instance
     */
    URN getReplacedBy();
}
