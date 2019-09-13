package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.util.List;
import java.util.Locale;

public class ExportableEventTimelineCI {
    private Locale cachedLocale;
    private List<ExportableTimelineEventCI> timelineEvents;
    private boolean isFinalized;

    public ExportableEventTimelineCI(Locale cachedLocale, List<ExportableTimelineEventCI> timelineEvents, boolean isFinalized) {
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

    public List<ExportableTimelineEventCI> getTimelineEvents() {
        return timelineEvents;
    }

    public void setTimelineEvents(List<ExportableTimelineEventCI> timelineEvents) {
        this.timelineEvents = timelineEvents;
    }

    public boolean isFinalized() {
        return isFinalized;
    }

    public void setFinalized(boolean finalized) {
        isFinalized = finalized;
    }
}
