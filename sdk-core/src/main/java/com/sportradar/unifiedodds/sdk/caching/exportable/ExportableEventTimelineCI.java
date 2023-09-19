package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({ "HiddenField" })
public class ExportableEventTimelineCi implements Serializable {

    private Locale cachedLocale;
    private List<ExportableTimelineEventCi> timelineEvents;
    private boolean isFinalized;

    public ExportableEventTimelineCi(
        Locale cachedLocale,
        List<ExportableTimelineEventCi> timelineEvents,
        boolean isFinalized
    ) {
        this.cachedLocale = cachedLocale;
        this.timelineEvents = timelineEvents;
        this.isFinalized = isFinalized;
    }

    public Locale getCachedLocale() {
        return cachedLocale;
    }

    public void setCachedLocale(Locale cachedLocale) {
        this.cachedLocale = cachedLocale;
    }

    public List<ExportableTimelineEventCi> getTimelineEvents() {
        return timelineEvents;
    }

    public void setTimelineEvents(List<ExportableTimelineEventCi> timelineEvents) {
        this.timelineEvents = timelineEvents;
    }

    public boolean isFinalized() {
        return isFinalized;
    }

    public void setFinalized(boolean finalized) {
        isFinalized = finalized;
    }
}
