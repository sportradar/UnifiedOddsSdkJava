package com.sportradar.unifiedodds.sdk.oddsentities.exportable;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings({ "HiddenField" })
public class ExportableScheduledStartTimeChangeCi implements Serializable {

    private Date oldTime;
    private Date newTime;
    private Date changedAt;

    public ExportableScheduledStartTimeChangeCi(Date oldTime, Date newTime, Date changedAt) {
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
}
