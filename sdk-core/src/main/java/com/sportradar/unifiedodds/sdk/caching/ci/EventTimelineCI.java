/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPITimeline;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A cache representation of an event timeline
 */
public class EventTimelineCI {
    private final Locale cachedLocale;
    private final List<TimelineEventCI> timelineEvents;
    private final boolean isFinalized;

    public EventTimelineCI(SAPITimeline timeline, Locale dataLocale, boolean isFinalized) {
        Preconditions.checkNotNull(timeline);
        Preconditions.checkNotNull(dataLocale);

        this.timelineEvents = timeline.getEvent().stream()
                .map(TimelineEventCI::new)
                .collect(Collectors.toList());
        this.cachedLocale = dataLocale;
        this.isFinalized = isFinalized;
    }

    /**
     * Returns a chronological list of events
     *
     * @return a chronological list of {@link TimelineEventCI}s
     */
    public List<TimelineEventCI> getTimelineEvents() {
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
}
