/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.ScheduledStartTimeChange;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableScheduledStartTimeChangeCi;
import java.util.Date;

/**
 * Represents a start time change
 */
@SuppressWarnings({ "UnnecessaryParentheses" })
public class ScheduledStartTimeChangeImpl implements ScheduledStartTimeChange {

    private final Date oldTime;
    private final Date newTime;
    private final Date changedAt;

    /**
     *Initializes a new instance of the {@link ScheduledStartTimeChangeImpl} class
     */
    ScheduledStartTimeChangeImpl(Date oldTime, Date newTime, Date changedAt) {
        this.oldTime = oldTime;
        this.newTime = newTime;
        this.changedAt = changedAt;
    }

    ScheduledStartTimeChangeImpl(ExportableScheduledStartTimeChangeCi exportable) {
        Preconditions.checkNotNull(exportable);
        this.oldTime = exportable.getOldTime();
        this.newTime = exportable.getNewTime();
        this.changedAt = exportable.getChangedAt();
    }

    /**
     * Returns a {@link String} describing the current {@link ScheduledStartTimeChange} instance
     *
     * @return - a {@link String} describing the current {@link ScheduledStartTimeChange} instance
     */
    @Override
    public String toString() {
        return (
            "ScheduledStartTimeChangeImpl{" +
            "oldTime='" +
            oldTime +
            '\'' +
            ", newTime=" +
            newTime +
            '\'' +
            ", changedAt=" +
            changedAt +
            '}'
        );
    }

    /**
     * Returns a {@link Date} specifying the old date
     *
     * @return - a {@link Date} specifying the old date
     */
    @Override
    public Date getOldTime() {
        return oldTime;
    }

    /**
     * Returns a {@link Date} specifying the new date
     *
     * @return - a {@link Date} specifying the new date
     */
    @Override
    public Date getNewTime() {
        return newTime;
    }

    /**
     * Returns a {@link Date} specifying when was start time changed
     *
     * @return - a {@link Date} specifying when was start time changed
     */
    @Override
    public Date getChangedAt() {
        return changedAt;
    }

    public ExportableScheduledStartTimeChangeCi export() {
        return new ExportableScheduledStartTimeChangeCi(oldTime, newTime, changedAt);
    }
}
