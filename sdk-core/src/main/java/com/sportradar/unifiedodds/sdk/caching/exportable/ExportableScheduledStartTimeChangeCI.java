package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.util.Date;

public class ExportableScheduledStartTimeChangeCI {
    private Date oldTime;
    private Date newTime;
    private Date changedAt;

    ExportableScheduledStartTimeChangeCI(Date oldTime, Date newTime, Date changedAt) {
        this.oldTime = oldTime;
        this.newTime = newTime;
        this.changedAt = changedAt;
    }

    public Date getOldTime() {
        return oldTime;
    }

    public void setOldTime(Date oldTime) {
        this.oldTime = oldTime;
    }

    public Date getNewTime() {
        return newTime;
    }

    public void setNewTime(Date newTime) {
        this.newTime = newTime;
    }

    public Date getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Date changedAt) {
        this.changedAt = changedAt;
    }

    public ExportableScheduledStartTimeChangeCI export() {
        return new ExportableScheduledStartTimeChangeCI(
                oldTime,
                newTime,
                changedAt
        );
    }
}
