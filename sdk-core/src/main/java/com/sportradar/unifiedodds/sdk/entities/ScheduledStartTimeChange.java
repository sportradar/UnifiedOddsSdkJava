/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.Date;

/**
 * An interface providing methods to access {@link ScheduledStartTimeChange} implementations properties
 */
public interface ScheduledStartTimeChange {
    /**
     * Returns a {@link Date} specifying the old date
     *
     * @return - a {@link Date} specifying the old date
     */
    Date getOldTime();

    /**
     * Returns a {@link Date} specifying the new date
     *
     * @return - a {@link Date} specifying the new date
     */
    Date getNewTime();

    /**
     * Returns a {@link Date} specifying when was start time changed
     *
     * @return - a {@link Date} specifying when was start time changed
     */
    Date getChangedAt();
}
