/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.unifiedodds.sdk.caching.ci.TimelineEventCi;
import java.util.List;

/**
 * Defines methods used to access event timeline properties
 */
public interface EventTimeline {
    /**
     * Returns a chronological list of events
     *
     * @return a chronological list of {@link TimelineEventCi}s
     */
    List<TimelineEvent> getTimelineEvents();
}
