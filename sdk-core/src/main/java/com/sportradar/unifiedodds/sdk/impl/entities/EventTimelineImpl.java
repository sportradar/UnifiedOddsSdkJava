/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.EventTimelineCi;
import com.sportradar.unifiedodds.sdk.caching.ci.TimelineEventCi;
import com.sportradar.unifiedodds.sdk.entities.EventTimeline;
import com.sportradar.unifiedodds.sdk.entities.TimelineEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link EventTimeline} interface
 */
public class EventTimelineImpl implements EventTimeline {

    private final EventTimelineCi eventTimeline;

    public EventTimelineImpl(EventTimelineCi eventTimeline) {
        Preconditions.checkNotNull(eventTimeline);

        this.eventTimeline = eventTimeline;
    }

    /**
     * Returns a chronological list of events
     *
     * @return a chronological list of {@link TimelineEventCi}s
     */
    @Override
    public List<TimelineEvent> getTimelineEvents() {
        return eventTimeline.getTimelineEvents() == null
            ? null
            : eventTimeline
                .getTimelineEvents()
                .stream()
                .map(cacheItem -> new TimelineEventImpl(cacheItem, eventTimeline.getCachedLocale()))
                .collect(Collectors.toList());
    }
}
