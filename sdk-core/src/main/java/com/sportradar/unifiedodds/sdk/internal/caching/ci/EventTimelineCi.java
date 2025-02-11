/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiTimeline;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableEventTimelineCi;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A cache representation of an event timeline
 */
public class EventTimelineCi {

    private final Locale cachedLocale;
    private final List<TimelineEventCi> timelineEvents;
    private final boolean isFinalized;

    public EventTimelineCi(SapiTimeline timeline, Locale dataLocale, boolean isFinalized) {
        Preconditions.checkNotNull(timeline);
        Preconditions.checkNotNull(dataLocale);

        this.timelineEvents =
            timeline.getEvent().stream().map(TimelineEventCi::new).collect(Collectors.toList());
        this.cachedLocale = dataLocale;
        this.isFinalized = isFinalized;
    }

    public EventTimelineCi(ExportableEventTimelineCi exportable) {
        Preconditions.checkNotNull(exportable);

        this.cachedLocale = exportable.getCachedLocale();
        this.timelineEvents =
            exportable.getTimelineEvents().stream().map(TimelineEventCi::new).collect(Collectors.toList());
        this.isFinalized = exportable.isFinalized();
    }

    /**
     * Returns a chronological list of events
     *
     * @return a chronological list of {@link TimelineEventCi}s
     */
    public List<TimelineEventCi> getTimelineEvents() {
        return timelineEvents;
    }

    /**
     * Returns the cached data {@link Locale}
     *
     * @return the locale in which the data is cached
     */
    public Locale getCachedLocale() {
        return cachedLocale;
    }

    /**
     * Returns an indication if the event timeline is finalized
     *
     * @return <code>true</code> if the timeline is finalized, otherwise <code>false</code>
     */
    public boolean isFinalized() {
        return isFinalized;
    }

    public ExportableEventTimelineCi export() {
        return new ExportableEventTimelineCi(
            cachedLocale,
            timelineEvents.stream().map(TimelineEventCi::export).collect(Collectors.toList()),
            isFinalized
        );
    }
}
