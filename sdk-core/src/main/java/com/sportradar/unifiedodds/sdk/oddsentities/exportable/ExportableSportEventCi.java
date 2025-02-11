/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities.exportable;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "HiddenField" })
public class ExportableSportEventCi extends ExportableCi {

    private Date scheduled;
    private Date scheduledEnd;
    private Boolean startTimeTbd;
    private String replacedBy;

    ExportableSportEventCi(
        String id,
        Map<Locale, String> names,
        Date scheduled,
        Date scheduledEnd,
        Boolean startTimeTbd,
        String replacedBy
    ) {
        super(id, names);
        this.scheduled = scheduled;
        this.scheduledEnd = scheduledEnd;
        this.startTimeTbd = startTimeTbd;
        this.replacedBy = replacedBy;
    }

    public Date getScheduled() {
        return scheduled;
    }

    public void setScheduled(Date scheduled) {
        this.scheduled = scheduled;
    }

    public Date getScheduledEnd() {
        return scheduledEnd;
    }

    public void setScheduledEnd(Date scheduledEnd) {
        this.scheduledEnd = scheduledEnd;
    }

    public Boolean getStartTimeTbd() {
        return startTimeTbd;
    }

    public void setStartTimeTbd(Boolean startTimeTbd) {
        this.startTimeTbd = startTimeTbd;
    }

    public String getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(String replacedBy) {
        this.replacedBy = replacedBy;
    }
}
